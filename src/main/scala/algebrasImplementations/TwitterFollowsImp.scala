package algebrasImplementations

import models.Models.{
  FollowingIds,
  TwitterConfig,
  TwitterGetUserByUserNameResponseData,
  TwitterGetUserByUserNameResponseDataWithProfileUrl
}
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.{Headers, Request, Uri}
import org.http4s.client.Client
import algebras.TwitterFollows
import cats.effect.Sync
import cats.implicits._

object TwitterFollowsImp {
  def imp[F[_]: Sync](client: Client[F], twitterConfig: TwitterConfig): TwitterFollows[F] =
    new TwitterFollows[F] {
      def getUserByUserName(userName: String): F[TwitterGetUserByUserNameResponseData] = for {
        logger <- Slf4jLogger.create[F]
        _ <- logger.info(
          s"*** Request Url -> ${twitterConfig.baseUrl.baseUrl} and user -> ${userName} ***"
        )
        uri <- Sync[F].fromEither(Uri.fromString(s"${twitterConfig.baseUrl.baseUrl}/${userName}"))
        response <- client
          .expect[TwitterGetUserByUserNameResponseData](
            Request[F](
              uri = uri,
              headers =
                Headers("Authorization" -> s"Bearer ${twitterConfig.bearerToken.bearerToken}")
            )
          )
      } yield response

      def getUsersFollowedBy(userName: String): F[FollowingIds] = for {
        logger      <- Slf4jLogger.create[F]
        _           <- logger.info(s"*** Request to get user details for ${userName} ***")
        userDetails <- getUserByUserName(userName)
        _           <- logger.info("**** Making request to retrieve followings on Twitter ***")
        uri <- Sync[F].fromEither(
          Uri.fromString(
            s"${twitterConfig.twitterFollowingBaseUrl.twitterFollowingBaseUrl}/friends/ids.json?screen_name=${userDetails.data.username.username}"
          )
        )
        response <- client
          .expect[FollowingIds](
            Request[F](
              uri = uri,
              headers =
                Headers("Authorization" -> s"Bearer ${twitterConfig.bearerToken.bearerToken}")
            )
          )
      } yield response

      def getUsersFollowing(
          userName: String,
          maxNumberOfFollowers: Int
      ): F[TwitterGetUserByUserNameResponseDataWithProfileUrl] = for {
        logger <- Slf4jLogger.create[F]
        _      <- logger.info("*** Getting user details 🌎 ***")
        user   <- getUserByUserName(userName)
        uri <- Sync[F].fromEither(
          Uri.fromString(
            s"${twitterConfig.twitterFollowersBaseUrl.twitterFollowersBaseUrl}" +
              s"/${user.data.id.id}/followers?user.fields=profile_image_url&max_results=${maxNumberOfFollowers}"
          )
        )
        response <- client.expect[TwitterGetUserByUserNameResponseDataWithProfileUrl](
          Request[F](
            uri = uri,
            headers = Headers("Authorization" -> s"Bearer ${twitterConfig.bearerToken.bearerToken}")
          )
        )
      } yield response
    }
}
