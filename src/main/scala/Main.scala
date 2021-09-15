import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import cats.effect.{ExitCode, IO, IOApp}
import conf.TwitterConfig
import cats.implicits._
import http.Server
import fs2.Stream

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = (for {
    logger <- Stream.eval(Slf4jLogger.create[IO])
    twitterConfig <- Stream.eval(logger.info("Loading Twitter config ...")) *> Stream.eval(
      TwitterConfig.getConfig[IO]("conf")
    )
    _     <- Stream.eval(logger.info(s"*** Twitter config loaded ${twitterConfig} ***"))
    _     <- Stream.eval(logger.info("***Starting Twitter Cassandra Server . . .***"))
    serve <- Server.stream[IO](twitterConfig)
  } yield serve).compile.drain.as(ExitCode.Success)
}
