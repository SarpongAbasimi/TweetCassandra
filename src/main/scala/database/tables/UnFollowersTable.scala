package database.tables

import cats.effect.Sync
import com.datastax.driver.core.ConsistencyLevel
import com.outworkers.phantom.builder.Specified
import com.outworkers.phantom.builder.query.InsertQuery
import com.outworkers.phantom.dsl.Table
import com.outworkers.phantom.keys.PartitionKey
import models.Models.{DataWithProfileImageUrl, UnFollowers}
import shapeless.HNil

abstract class UnFollowersTable extends Table[UnFollowersTable, UnFollowers] {
  object id       extends StringColumn with PartitionKey
  object name     extends StringColumn
  object userName extends StringColumn
  object url      extends StringColumn

}

abstract class UnFollowersQueries[F[_]: Sync] extends UnFollowersTable {
  def storeUnFollowers(
      data: DataWithProfileImageUrl
  ): F[InsertQuery[UnFollowersTable, UnFollowers, Specified, HNil]] =
    Sync[F].delay {
      insert()
        .value[String](_.id, data.id.id)
        .value[String](_.url, data.profile_image_url.profile_image_url)
        .value[String](_.name, data.name.name)
        .value[String](_.userName, data.username.username)
        .consistencyLevel_=(ConsistencyLevel.ONE)
    }
}
