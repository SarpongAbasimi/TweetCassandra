package http

import services.TwitterServiceAlgebra
import org.http4s.dsl.Http4sDsl
import org.http4s.HttpRoutes
import cats.effect.Sync
import cats.implicits._
import io.chrisdavenport.log4cats.SelfAwareStructuredLogger
import models.Models.OptionalMaxResultQueryParamMatcher
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder

import scala.util.{Failure, Success, Try}

object Routes {
  def twitterRoutes[F[_]: Sync](
      twitterService: TwitterServiceAlgebra[F]
  )(logger: SelfAwareStructuredLogger[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "twitter" / "user" / userName =>
        for {
          twitterResponseData <- twitterService.getUserByUserName(userName)
          response            <- Ok(twitterResponseData)
        } yield response

      case GET -> Root / "twitter" / "following" / userName =>
        for {
          listOfTwitterFollowing <- twitterService.getTheFollowingOfUser(userName)
          response               <- Ok(listOfTwitterFollowing)
        } yield response

      case GET -> Root / "twitter" / "followers" / username :? OptionalMaxResultQueryParamMatcher(
            maxResult
          ) =>
        maxResult match {
          case None =>
            BadRequest(
              "Sorry Something went wrong 😕. " +
                "Add ?max_result=10 to the end of url 😉"
            )
          case Some(value) =>
            for {
              convertValueToInt <- Sync[F].delay(Try(value.optionalMaxResult.toInt))
              result <- convertValueToInt match {
                case Failure(_) => BadRequest("Query parameter must be a number")
                case Success(value) =>
                  for {
                    response <- twitterService.getFollowersOfAUser(username, value)
                    _ <- logger.info(
                      s"🐸 The size of data being returned -> ${response.data.length}"
                    )
                    res <- Ok(response)
                  } yield res
              }
            } yield result
        }

      case GET -> Root / "twitter" / "followers" / "ids" / userName =>
        for {
          listOfTwitterFollowing <- twitterService.getIdsOfFollowersOf(userName)
          _                      <- logger.info(s"🚀 Number of Ids -> ${listOfTwitterFollowing.ids.ids.length}")
          response               <- Ok(listOfTwitterFollowing)
        } yield response
    }
  }
}
