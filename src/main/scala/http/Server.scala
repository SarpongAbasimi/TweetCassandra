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
import database.db.UnFollowersDataBase
import fs2.Stream
import io.chrisdavenport.log4cats.SelfAwareStructuredLogger

object Server {
  def stream[F[_]: Sync: ConcurrentEffect: Timer](
      twitterConfig: TwitterConfig,
      unFollowDataBase: UnFollowersDataBase[F]
  )(logger: SelfAwareStructuredLogger[F]): Stream[F, ExitCode] = for {
    client <- BlazeClientBuilder[F](global).stream

    twitterAlgebra <- Stream.eval(TwitterFollowsImp.imp[F](client, twitterConfig)(logger).pure[F])

    appClient: TwitterServiceAlgebra[F] = TwitterService.imp(twitterAlgebra, unFollowDataBase)

    server <- BlazeServerBuilder[F](global)
      .bindHttp(5000, "localhost")
      .withHttpApp(Routes.twitterRoutes[F](appClient)(logger).orNotFound)
      .serve
  } yield server
}
