package database.connection

import cats.effect.{Resource, Sync}
import com.datastax.driver.core.SocketOptions
import com.outworkers.phantom.connectors.{ContactPoint, KeySpace}
import com.outworkers.phantom.dsl._
import models.Models.CassandraConfig

object DbConnection {
  def connection[F[_]: Sync](conf: CassandraConfig): Resource[F, CassandraConnection] =
    Resource.eval {
      Sync[F].delay {
        ContactPoint(conf.port.port)
          .withClusterBuilder(
            _.withSocketOptions(
              new SocketOptions()
                .setConnectTimeoutMillis(2000)
                .setReadTimeoutMillis(2000)
            )
          )
          .noHeartbeat()
          .keySpace(
            KeySpace(conf.keySpace.keySpace)
              .ifNotExists()
              .`with`(replication eqs (SimpleStrategy.replication_factor(3)))
          )
      }
    }
}
