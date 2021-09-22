package algebrasImplementations

import models.Models.{
  FollowersIds,
  FollowingIds,
  TwitterConfig,
  TwitterGetUserByUserNameResponseData,
  TwitterGetUserByUserNameResponseDataWithProfileUrl
}
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.{EntityDecoder, Headers, Request, Uri}
import org.http4s.client.Client
import algebras.TwitterFollows
import cats.effect.Sync
import cats.implicits._
import fs2.Stream

object TwitterFollowsImp {
  def imp[F[_]: Sync](client: Client[F], twitterConfig: TwitterConfig): TwitterFollows[F] =
    new TwitterFollows[F] {
      def getUserByUserName(userName: String): F[TwitterGetUserByUserNameResponseData] = for {
        logger <- Slf4jLogger.create[F]
        _ <- logger.info(
          s"*** Request Url -> ${twitterConfig.baseUrl.baseUrl} and user -> ${userName} ***"
        )
        url      <- Sync[F].pure(s"${twitterConfig.baseUrl.baseUrl}/${userName}")
        response <- clientResponseDataForType[TwitterGetUserByUserNameResponseData](url)
      } yield response

      def getUsersFollowedBy(userName: String): F[FollowingIds] = for {
        logger      <- Slf4jLogger.create[F]
        _           <- logger.info(s"*** Request to get user details for ${userName} ***")
        userDetails <- getUserByUserName(userName)
        _           <- logger.info("**** Making request to retrieve followings on Twitter ***")
        url <- Sync[F].pure(
          s"${twitterConfig.twitterFollowingBaseUrl.twitterFollowingBaseUrl}/friends/ids.json?screen_name=${userDetails.data.username.username}"
        )
        response <- clientResponseDataForType[FollowingIds](url)
      } yield response

      def getUsersFollowing(
          userName: String,
          maxNumberOfFollowers: Int
      ): F[TwitterGetUserByUserNameResponseDataWithProfileUrl] = for {
        logger <- Slf4jLogger.create[F]
        _      <- logger.info("*** Getting user details 🌎 ***")
        user   <- getUserByUserName(userName)
        url <- Sync[F].pure(
          s"${twitterConfig.twitterFollowersBaseUrl.twitterFollowersBaseUrl}" +
            s"/${user.data.id.id}/followers?user.fields=profile_image_url&max_results=${maxNumberOfFollowers}"
        )
        response <- clientResponseDataForType[TwitterGetUserByUserNameResponseDataWithProfileUrl](
          url
        )
      } yield response

      def getIdsOfUsersFollowing(userName: String): F[FollowersIds] = for {
        logger <- Slf4jLogger.create[F]
        user   <- getUserByUserName(userName)
        _      <- logger.info(s"😃 Getting all the ids of users following ${userName}")
        url <- Sync[F].pure(
          s"${twitterConfig.twitterFollowingBaseUrl.twitterFollowingBaseUrl}/followers/ids.json?screen_name=${user.data.username.username}"
        )
        response <- clientResponseDataForType[FollowersIds](url)
        _        <- logger.info(s"Success 🚀 -> Length of Data : ${response.ids.ids.length}")
      } yield response

      def getUnFollowersOf(userName: String): Stream[F, Long] = for {
        logger <- Stream.eval(Slf4jLogger.create[F])
        _      <- Stream.eval(logger.info(s"😝 Getting list of UnFollowers for ${userName}"))
        idsOfUsersFollowed <- Stream
          .eval(getUsersFollowedBy(userName))
          .evalTap(data =>
            logger.info(
              s"1️⃣ Length of user Ids followed by ${userName} is ${data.ids.ids.length}"
            )
          )
          .map(_.ids.ids)
        idsOfUsersFollowing <- Stream
          .eval(getIdsOfUsersFollowing(userName))
          .evalTap(data =>
            logger.info(
              s"2️⃣ Length of user Ids following ${userName} is ${data.ids.ids.length}"
            )
          )
          .map(_.ids.ids)
        response <- Stream
          .emits(idsOfUsersFollowed)
          .covary[F]
          .filterNot(ids => idsOfUsersFollowing.contains(ids))
      } yield response

      def getUnFollowersDetailsFor(
          userName: String
      ): F[TwitterGetUserByUserNameResponseDataWithProfileUrl] = {
        for {
          logger               <- Slf4jLogger.create[F]
          _                    <- logger.info("💭 💭 Getting details for unFollowers")
          listOfUnFollowersIds <- getUnFollowersOf(userName).compile.toList
          url <- logger.info("(^Building Url^)") >> Sync[F].pure(
            s"${twitterConfig.twitterFollowersBaseUrl.twitterFollowersBaseUrl}?user.fields=profile_image_url&ids=${listOfUnFollowersIds
              .mkString(",")}"
          )
          response <- clientResponseDataForType[TwitterGetUserByUserNameResponseDataWithProfileUrl](
            url
          )

        } yield response
      }

      private def clientResponseDataForType[T](
          url: String
      )(implicit entityDecoder: EntityDecoder[F, T]): F[T] = {
        for {
          uri <- Sync[F].fromEither(Uri.fromString(url))
          response <- client
            .expect[T](
              Request[F](
                uri = uri,
                headers =
                  Headers("Authorization" -> s"Bearer ${twitterConfig.bearerToken.bearerToken}")
              )
            )
        } yield response
      }
    }
}
