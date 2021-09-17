package services

import models.Models.{
  FollowersIds,
  FollowingIds,
  TwitterGetUserByUserNameResponseData,
  TwitterGetUserByUserNameResponseDataWithProfileUrl
}
import algebras.TwitterFollows
import cats.effect.Sync
import cats.implicits._
import errors.GetRequestError

trait TwitterServiceAlgebra[F[_]] {
  def getUserByUserName(userName: String): F[TwitterGetUserByUserNameResponseData]
  def getTheFollowingOfUser(userName: String): F[FollowingIds]
  def getFollowersOfAUser(
      userName: String,
      maxNumberOfFollowersToReturn: Int
  ): F[TwitterGetUserByUserNameResponseDataWithProfileUrl]
  def getTheIdsOfTheFollowersOf(userName: String): F[FollowersIds]
  def getUnFollowers(userName: String): F[List[Long]]
}

object TwitterService {
  def imp[F[_]: Sync](twitterFollows: TwitterFollows[F]): TwitterServiceAlgebra[F] =
    new TwitterServiceAlgebra[F] {
      def getUserByUserName(userName: String): F[TwitterGetUserByUserNameResponseData] =
        twitterFollows
          .getUserByUserName(userName)
          .adaptError(GetRequestError(_))

      def getTheFollowingOfUser(userName: String): F[FollowingIds] =
        twitterFollows.getUsersFollowedBy(userName).adaptError(GetRequestError(_))

      def getFollowersOfAUser(
          userName: String,
          maxNumberOfFollowersToReturn: Int
      ): F[TwitterGetUserByUserNameResponseDataWithProfileUrl] =
        twitterFollows.getUsersFollowing(userName, maxNumberOfFollowersToReturn)

      def getTheIdsOfTheFollowersOf(userName: String): F[FollowersIds] =
        twitterFollows.getIdsOfUsersFollowing(userName).adaptError(GetRequestError(_))

      def getUnFollowers(userName: String): F[List[Long]] =
        twitterFollows.getUnFollowersOf(userName).compile.toList
    }
}
