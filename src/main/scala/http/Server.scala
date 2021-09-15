package http

import cats.effect.{ConcurrentEffect, ExitCode, Sync, Timer}
import services.{TwitterService, TwitterServiceAlgebra}
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.blaze.server.BlazeServerBuilder
import algebrasImplementations.TwitterFollowsImp
import scala.concurrent.ExecutionContext.global
import models.Models.TwitterConfig
import cats.implicits._
import fs2.Stream

object Server {
  def stream[F[_]: Sync: ConcurrentEffect: Timer](
      twitterConfig: TwitterConfig
  ): Stream[F, ExitCode] = for {
    logger <- Stream.eval(Slf4jLogger.create[F])
    client <- Stream.eval(logger.info("*** Building Client . . . ***")) *> BlazeClientBuilder[F](
      global
    ).stream
    twitterAlgebra <- Stream.eval(Sync[F].delay(TwitterFollowsImp.imp[F](client, twitterConfig)))

    appClient: TwitterServiceAlgebra[F] = TwitterService.imp(twitterAlgebra)

    server <- BlazeServerBuilder[F](global)
      .bindHttp(5000, "localhost")
      .withHttpApp(Routes.twitterRoutes[F](appClient).orNotFound)
      .serve
  } yield server
}
