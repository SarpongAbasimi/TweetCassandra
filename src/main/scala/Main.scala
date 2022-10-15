import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import cats.effect.{ExitCode, IO, IOApp}
import conf.{CassandraConfig, TwitterConfig}
import cats.implicits._
import database.connection.DbConnection
import database.db.UnFollowersDataBase
import http.Server
import fs2.Stream
import io.chrisdavenport.log4cats.SelfAwareStructuredLogger

object Main extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = (for {
    logger: SelfAwareStructuredLogger[IO] <- Stream.eval(Slf4jLogger.create[IO])
    _                                     <- Stream.eval(logger.info("Loading config... 🐦"))
    twitterConfig <- Stream
      .eval(
        TwitterConfig.getConfig[IO]("conf")
      )
    cassandraConf <- Stream.eval(CassandraConfig.getConfig[IO]("cassandra"))
    connection    <- Stream.resource(DbConnection.connection[IO](cassandraConf))
    db            <- Stream.eval(IO(new UnFollowersDataBase[IO](connection)))
    _             <- Stream.eval(IO(db.twitterUnFollowers))
    _             <- Stream.eval(logger.info("*** Starting Twitter Cassandra Server 🤙🏿 🚀 . . . ***"))
    serve         <- Server.stream[IO](twitterConfig, db)(logger)
  } yield serve).compile.drain.as(ExitCode.Success)
}
