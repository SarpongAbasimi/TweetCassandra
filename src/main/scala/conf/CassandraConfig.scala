package conf

import com.typesafe.config.ConfigFactory
import models.Models.{CassandraConfig}
import com.typesafe.config.Config
import pureconfig.generic.auto._
import cats.effect.Sync
import cats.implicits._
import pureconfig._

object CassandraConfig {
  def getConfig[F[_]: Sync](configName: String): F[CassandraConfig] =
    Sync[F].delay(ConfigFactory.load()).flatMap(conf => loadConfig(conf.getConfig(configName)))

  private def loadConfig[F[_]: Sync](conf: Config): F[CassandraConfig] =
    Sync[F].delay(ConfigSource.fromConfig(conf).loadOrThrow[CassandraConfig])
}
