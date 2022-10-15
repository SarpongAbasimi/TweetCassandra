package algebras

import models.Models.{
  FollowersIds,
  FollowingIds,
  TwitterGetUserByUserNameResponseData,
  TwitterGetUserByUserNameResponseDataWithProfileUrl
}
import fs2.Stream

trait Twitter[F[_]] {
  def getUserBy(userName: String): F[TwitterGetUserByUserNameResponseData]
  def getUsersFollowedBy(userName: String): F[FollowingIds]
  def getUsersFollowing(
      userName: String,
      maxNumberOfFollowers: Int
  ): F[TwitterGetUserByUserNameResponseDataWithProfileUrl]
  def getIdsOfUsersFollowing(userName: String): F[FollowersIds]
  def getUnFollowersOf(userName: String): Stream[F, Long]
  def getUnFollowersDetailsFor(
      userName: String
  ): F[TwitterGetUserByUserNameResponseDataWithProfileUrl]
}
