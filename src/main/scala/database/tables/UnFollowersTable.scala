package database.tables

import models.Models.{DataWithProfileImageUrl, UnFollowers}
import com.datastax.driver.core.ConsistencyLevel
import com.outworkers.phantom.keys.PartitionKey
import com.outworkers.phantom.dsl.{Table}
import com.outworkers.phantom.dsl._
import scala.concurrent.Future
import cats.effect.{Sync}

abstract class UnFollowersTable extends Table[UnFollowersTable, UnFollowers] {
  object id       extends StringColumn with PartitionKey
  object name     extends StringColumn
  object userName extends StringColumn
  object url      extends StringColumn

}

abstract class UnFollowersQueries[F[_]: Sync] extends UnFollowersTable {
  def storeUnFollowers(
      data: DataWithProfileImageUrl
  ): F[Future[ResultSet]] =
    Sync[F].delay {
      insert()
        .value[String](_.id, data.id.id)
        .value[String](_.url, data.profile_image_url.profile_image_url)
        .value[String](_.name, data.name.name)
        .value[String](_.userName, data.username.username)
        .consistencyLevel_=(ConsistencyLevel.ONE)
        .future()
    }
}
