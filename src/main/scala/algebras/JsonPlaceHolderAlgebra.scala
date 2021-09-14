package algebras

import models.Models.DummyData

trait JsonPlaceHolderAlgebra[F[_]] {
  def getDummyJsonData: F[DummyData]
}
