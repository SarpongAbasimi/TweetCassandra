package algebrasImplementations

import models.Models.{
  FollowersIds,
  TwitterConfig,
  TwitterGetUserByUserNameResponseData,
  TwitterGetUserByUserNameResponseDataWithProfileUrl
}

import org.http4s.{EntityDecoder, Headers, Request, Uri}
import org.http4s.client.Client
import algebras.Twitter
import cats.effect.Sync
import cats.implicits._
import io.chrisdavenport.log4cats.SelfAwareStructuredLogger

object TwitterFollowsImp {
  def imp[F[_]: Sync](client: Client[F], twitterConfig: TwitterConfig)(
      logger: SelfAwareStructuredLogger[F]
  ): Twitter[F] =
    new Twitter[F] {
      def getUserBy(userName: String): F[TwitterGetUserByUserNameResponseData] =
        clientResponseDataForType[TwitterGetUserByUserNameResponseData](
          s"${twitterConfig.baseUrl.value}/${userName}"
        )

      def getFollowingOf(userName: String): F[TwitterGetUserByUserNameResponseDataWithProfileUrl] =
        for {
          _           <- logger.info(s"Request to get user details for ${userName}")
          userDetails <- getUserBy(userName)
          response <- clientResponseDataForType[TwitterGetUserByUserNameResponseDataWithProfileUrl](
            s"${twitterConfig.twitterFollowingBaseUrl.value}" +
              s"/${userDetails.data.id.id}/following?max_results=1000"
          )
        } yield response

      def getFollowersOf(
          userName: String,
          maxNumberOfFollowers: Int
      ): F[TwitterGetUserByUserNameResponseDataWithProfileUrl] = for {
        user <- getUserBy(userName)
        url <- Sync[F].pure(
          s"${twitterConfig.twitterFollowersBaseUrl.value}" +
            s"/${user.data.id.id}/followers?user.fields=profile_image_url&max_results=${maxNumberOfFollowers}"
        )
        response <- clientResponseDataForType[TwitterGetUserByUserNameResponseDataWithProfileUrl](
          url
        )
      } yield response

      def getFollowersIdsOf(
          userName: String
      ): F[FollowersIds] = for {
        _ <- getUserBy(userName)
        url <- Sync[F].pure(
          s"https://api.twitter.com/1.1/followers/ids.json"
        )
        response <- clientResponseDataForType[FollowersIds](url)
      } yield response

      private[algebrasImplementations] def clientResponseDataForType[T](
          url: String
      )(implicit entityDecoder: EntityDecoder[F, T]): F[T] = {
        for {
          uri <- Sync[F].fromEither(Uri.fromString(url))
          response <- client
            .expect[T](
              Request[F](
                uri = uri,
                headers = Headers("Authorization" -> s"Bearer ${twitterConfig.bearerToken.value}")
              )
            )
        } yield response
      }
    }
}
