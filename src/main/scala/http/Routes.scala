package http

import algebras.JsonPlaceHolderAlgebra
import cats.effect.Sync
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import cats.implicits._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

object Routes {
  def twitterRoutes[F[_]: Sync](placeHolder: JsonPlaceHolderAlgebra[F]): HttpRoutes[F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._
    HttpRoutes.of[F] { case GET -> Root =>
      for {
        logger          <- Slf4jLogger.create
        _               <- logger.info("Making a GET to jsonPlaceHolder ...")
        jsonPlaceholder <- placeHolder.getDummyJsonData
        response <- logger.info("Request was successful -> returning response ...") >> Ok(
          jsonPlaceholder
        )
      } yield response
    }
  }
}
