package http

import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import services.TwitterServiceAlgebra
import org.http4s.dsl.Http4sDsl
import org.http4s.HttpRoutes
import cats.effect.Sync
import cats.implicits._
import models.Models.OptionalMaxResultQueryParamMatcher
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import scala.util.{Failure, Success, Try}

object Routes {
  def twitterRoutes[F[_]: Sync](twitterService: TwitterServiceAlgebra[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] {
      case GET -> Root / "twitter" / "user" / userName =>
        for {
          logger              <- Slf4jLogger.create[F]
          _                   <- logger.info("Making a GET request 🚀 to Twitter 🐦 ...")
          twitterResponseData <- twitterService.getUserByUserName(userName)
          response <- logger.info("Request was successful 🎉 -> returning response 💭 ...") >> Ok(
            twitterResponseData
          )
        } yield response

      case GET -> Root / "twitter" / "following" / userName =>
        for {
          logger                 <- Slf4jLogger.create[F]
          listOfTwitterFollowing <- twitterService.getTheFollowingOfUser(userName)
          _ <- logger.info("🚀 Successfully got listOfTwitterFollowing") *> logger.info(
            s"🚀 Number of Ids -> ${listOfTwitterFollowing.ids.ids.length}"
          )
          response <- Ok(listOfTwitterFollowing)
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
              logger            <- Slf4jLogger.create[F]
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
              _ <- logger.info("🚀 Successful Request")
            } yield result
        }

      case GET -> Root / "twitter" / "followers" / "ids" / userName =>
        for {
          logger                 <- Slf4jLogger.create[F]
          listOfTwitterFollowing <- twitterService.getTheIdsOfTheFollowersOf(userName)
          _ <- logger.info("🚀 Successfully got ids of listOfTwitterFollowers") *> logger.info(
            s"🚀 Number of Ids -> ${listOfTwitterFollowing.ids.ids.length}"
          )
          response <- Ok(listOfTwitterFollowing)
        } yield response

      case GET -> Root / "twitter" / "unfollowers" / userName =>
        for {
          logger    <- Slf4jLogger.create[F]
          listOfIds <- twitterService.getUnFollowers(userName)
          _         <- logger.info("Getting list of unFollowers 🤦🏿‍♂️")
          response  <- Ok(listOfIds)
        } yield response

      case GET -> Root / "twitter" / "unfollowers" / "details" / username =>
        for {
          logger   <- Slf4jLogger.create[F]
          _        <- logger.info("Getting details of all unFollowers 🤙🏿 🚀 ⭐️ ")
          response <- Ok(twitterService.getUnFollowersDetails(username))
        } yield response
    }
  }
}
