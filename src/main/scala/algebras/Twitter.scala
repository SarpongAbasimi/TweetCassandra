package algebras

import models.Models.{
  FollowersIds,
  TwitterGetUserByUserNameResponseData,
  TwitterGetUserByUserNameResponseDataWithProfileUrl
}

trait Twitter[F[_]] {
  def getUserBy(userName: String): F[TwitterGetUserByUserNameResponseData]
  def getFollowingOf(userName: String): F[TwitterGetUserByUserNameResponseDataWithProfileUrl]
  def getFollowersOf(
      userName: String,
      maxNumberOfFollowers: Int
  ): F[TwitterGetUserByUserNameResponseDataWithProfileUrl]
  def getFollowersIdsOf(
      userName: String
  ): F[FollowersIds]
}
