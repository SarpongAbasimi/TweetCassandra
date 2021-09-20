package database.db

import com.outworkers.phantom.connectors.CassandraConnection
import com.outworkers.phantom.dsl.Database

class UnFollowersDataBase(override val connector: CassandraConnection)
    extends Database[UnFollowersDataBase](connector) {}
