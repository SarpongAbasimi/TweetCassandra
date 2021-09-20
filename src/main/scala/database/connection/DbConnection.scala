package database.connection

/** Phantom not compatible with Scala Version * */
/**
 * *
 * import cats.effect.{Resource, Sync}
 * import com.datastax.driver.core.SocketOptions
 * import com.outworkers.phantom.connectors.{ContactPoint, KeySpace}
 * import com.outworkers.phantom.dsl._
 *
 * object DbConnection {
 *  def connection[F[_]: Sync]: Resource[F, CassandraConnection] = Resource.eval {
 *    Sync[F].delay {
 *      ContactPoint.local
 *        .withClusterBuilder(
 *          _.withSocketOptions(
 *            new SocketOptions()
 *              .setConnectTimeoutMillis(2000)
 *              .setReadTimeoutMillis(2000)
 *          )
 *        )
 *        .noHeartbeat()
 *        .keySpace(
 *          KeySpace("twitter")
 *            .ifNotExists()
 *            .`with`(replication eqs (SimpleStrategy.replication_factor(3)))
 *        )
 *    }
 *  }
 * }
 */
