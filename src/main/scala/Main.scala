import http.Server
import fs2.Stream
import cats.effect.{ExitCode, IO, IOApp}
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = (for {
    logger <- Stream.eval(Slf4jLogger.create[IO])
    _      <- Stream.eval(logger.info("Starting Twitter Cassandra Server . . ."))
    serve  <- Server.stream[IO]
  } yield serve).compile.drain.as(ExitCode.Success)
}
