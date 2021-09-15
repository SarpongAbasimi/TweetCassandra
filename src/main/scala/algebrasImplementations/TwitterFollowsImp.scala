package algebrasImplementations

import models.Models.{TwitterConfig, TwitterGetUserByUserNameResponseData}
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
        _      <- logger.info(s"Request Url -> ${twitterConfig.baseUrl.baseUrl} and user -> ${userName}")
        uri    <- Sync[F].fromEither(Uri.fromString(s"${twitterConfig.baseUrl.baseUrl}/${userName}"))
        response <- client
          .expect[TwitterGetUserByUserNameResponseData](
            Request[F](
              uri = uri,
              headers =
                Headers("Authorization" -> s"Bearer ${twitterConfig.bearerToken.bearerToken}")
            )
          )
      } yield response
    }
}
