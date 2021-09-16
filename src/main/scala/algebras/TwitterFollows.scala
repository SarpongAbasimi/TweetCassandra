package algebras

import models.Models.{
  FollowersIds,
  FollowingIds,
  TwitterGetUserByUserNameResponseData,
  TwitterGetUserByUserNameResponseDataWithProfileUrl
}

trait TwitterFollows[F[_]] {
  def getUserByUserName(userName: String): F[TwitterGetUserByUserNameResponseData]
  def getUsersFollowedBy(userName: String): F[FollowingIds]
  def getUsersFollowing(
      userName: String,
      maxNumberOfFollowers: Int
  ): F[TwitterGetUserByUserNameResponseDataWithProfileUrl]
  def getIdsOfUsersFollowing(userName: String): F[FollowersIds]
}
