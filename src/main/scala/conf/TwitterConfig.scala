package conf

import cats.effect.{Sync}
import com.typesafe.config.{Config, ConfigFactory}
import models.Models.TwitterConfig
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import cats.implicits._

object TwitterConfig {
  def getConfig[F[_]: Sync](configName: String): F[TwitterConfig] =
    Sync[F].delay(ConfigFactory.load()).flatMap(conf => loadConfig(conf.getConfig(configName)))

  private def loadConfig[F[_]: Sync](conf: Config): F[TwitterConfig] =
    Sync[F].delay(ConfigSource.fromConfig(conf).loadOrThrow[TwitterConfig])
}
