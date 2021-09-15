package algebras

import models.Models.TwitterGetUserByUserNameResponseData

trait TwitterFollows[F[_]] {
  def getUserByUserName(userName: String): F[TwitterGetUserByUserNameResponseData]
}
