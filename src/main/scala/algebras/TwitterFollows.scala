package algebras

import models.Models.{FollowingIds, TwitterGetUserByUserNameResponseData}

trait TwitterFollows[F[_]] {
  def getUserByUserName(userName: String): F[TwitterGetUserByUserNameResponseData]
  def getUsersFollowedBy(userName: String): F[FollowingIds]
}
