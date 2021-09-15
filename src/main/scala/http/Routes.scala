package http

import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import services.TwitterServiceAlgebra
import org.http4s.dsl.Http4sDsl
import org.http4s.HttpRoutes
import cats.effect.Sync
import cats.implicits._

object Routes {
  def twitterRoutes[F[_]: Sync](twitterService: TwitterServiceAlgebra[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] { case GET -> Root / userName =>
      for {
        logger              <- Slf4jLogger.create
        _                   <- logger.info("Making a GET to jsonPlaceHolder ...")
        twitterResponseData <- twitterService.getUserByUserName(userName)
        response <- logger.info("Request was successful -> returning response ...") >> Ok(
          twitterResponseData
        )
      } yield response
    }
  }
}
