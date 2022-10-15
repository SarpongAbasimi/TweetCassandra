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
import algebras.Twitter
import cats.effect.Sync
import cats.implicits._
import fs2.Stream
import io.chrisdavenport.log4cats.SelfAwareStructuredLogger

object TwitterFollowsImp {
  def imp[F[_]: Sync](client: Client[F], twitterConfig: TwitterConfig)(
      logger: SelfAwareStructuredLogger[F]
  ): Twitter[F] =
    new Twitter[F] {
      def getUserBy(userName: String): F[TwitterGetUserByUserNameResponseData] =
        clientResponseDataForType[TwitterGetUserByUserNameResponseData](
          s"${twitterConfig.baseUrl.baseUrl}/${userName}"
        )

      def getUsersFollowedBy(userName: String): F[FollowingIds] = for {
        _           <- logger.info(s"Request to get user details for ${userName}")
        userDetails <- getUserBy(userName)
        response <- clientResponseDataForType[FollowingIds](
          s"${twitterConfig.twitterFollowingBaseUrl.twitterFollowingBaseUrl}" +
            s"/friends/ids.json?screen_name=${userDetails.data.username.username}"
        )
      } yield response

      def getUsersFollowing(
          userName: String,
          maxNumberOfFollowers: Int
      ): F[TwitterGetUserByUserNameResponseDataWithProfileUrl] = for {
        user <- getUserBy(userName)
        url <- Sync[F].pure(
          s"${twitterConfig.twitterFollowersBaseUrl.twitterFollowersBaseUrl}" +
            s"/${user.data.id.id}/followers?user.fields=profile_image_url&max_results=${maxNumberOfFollowers}"
        )
        response <- clientResponseDataForType[TwitterGetUserByUserNameResponseDataWithProfileUrl](
          url
        )
      } yield response

      def getIdsOfUsersFollowing(userName: String): F[FollowersIds] = for {
        user <- getUserBy(userName)
        url <- Sync[F].pure(
          s"${twitterConfig.twitterFollowingBaseUrl.twitterFollowingBaseUrl}" +
            s"/followers/ids.json?screen_name=${user.data.username.username}"
        )
        response <- clientResponseDataForType[FollowersIds](url)
      } yield response

      def getUnFollowersOf(userName: String): Stream[F, Long] = for {
        idsOfUsersFollowed <- Stream
          .eval(getUsersFollowedBy(userName))
          .map(_.ids.ids)
        idsOfUsersFollowing <- Stream
          .eval(getIdsOfUsersFollowing(userName))
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
          listOfUnFollowersIds <- getUnFollowersOf(userName).compile.toList
          url <- Sync[F].pure(
            s"${twitterConfig.twitterFollowersBaseUrl.twitterFollowersBaseUrl}?user.fields=profile_image_url&ids=${listOfUnFollowersIds
              .mkString(",")}"
          )
          response <- clientResponseDataForType[TwitterGetUserByUserNameResponseDataWithProfileUrl](
            url
          )

        } yield response
      }

      private[algebrasImplementations] def clientResponseDataForType[T](
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
