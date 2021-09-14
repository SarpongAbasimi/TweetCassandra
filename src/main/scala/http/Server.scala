package http

import algebras.JsonPlaceHolderAlgebra
import cats.effect.{ConcurrentEffect, ExitCode, Sync, Timer}
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.blaze.server.BlazeServerBuilder
import fs2.Stream

import scala.concurrent.ExecutionContext.global
import cats.implicits._

object Server {
  def stream[F[_]: Sync: ConcurrentEffect: Timer]: Stream[F, ExitCode] = for {
    logger <- Stream.eval(Slf4jLogger.create[F])
    client <- Stream.eval(logger.info("Building Client . . .")) *> BlazeClientBuilder[F](
      global
    ).stream

    appClient: JsonPlaceHolderAlgebra[F] = Client.imp(client)

    server <- BlazeServerBuilder[F](global)
      .bindHttp(5000, "localhost")
      .withHttpApp(Routes.twitterRoutes[F](appClient).orNotFound)
      .serve
  } yield server
}
