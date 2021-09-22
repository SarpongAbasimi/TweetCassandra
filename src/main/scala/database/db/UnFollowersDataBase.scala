package database.db

import cats.effect.Sync
import com.outworkers.phantom.connectors.CassandraConnection
import com.outworkers.phantom.dsl.Database
import database.tables.{UnFollowersQueries}

class UnFollowersDataBase[F[_]: Sync](override val connector: CassandraConnection)
    extends Database[UnFollowersDataBase[F]](connector) {
  object twitterUnFollowers extends UnFollowersQueries[F] with connector.Connector
}
