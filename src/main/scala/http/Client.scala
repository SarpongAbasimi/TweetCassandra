package http

import algebras.JsonPlaceHolderAlgebra
import cats.effect.Sync
import models.Models.DummyData
import org.http4s.client.Client
import org.http4s.client.dsl.Http4sClientDsl
import cats.implicits._
import errors.GetRequestError

object Client {
  def imp[F[_]: Sync](client: Client[F]) = new JsonPlaceHolderAlgebra[F] {
    val dsl = new Http4sClientDsl[F] {}
    import dsl._
    def getDummyJsonData: F[DummyData] =
      client
        .expect[DummyData]("https://jsonplaceholder.typicode.com/todos/1")
        .adaptError { case error =>
          GetRequestError(error)
        }
  }
}
