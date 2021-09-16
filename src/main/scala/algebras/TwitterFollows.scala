package algebras

import models.Models.{
  FollowingIds,
  TwitterGetUserByUserNameResponseData,
  TwitterGetUserByUserNameResponseDataWithProfileUrl
}

trait TwitterFollows[F[_]] {
  def getUserByUserName(userName: String): F[TwitterGetUserByUserNameResponseData]
  def getUsersFollowedBy(userName: String): F[FollowingIds]
  def getUsersFollowing(
      userName: String,
      maxNumberOfFollowers: Int = 1000
  ): F[TwitterGetUserByUserNameResponseDataWithProfileUrl]
}
