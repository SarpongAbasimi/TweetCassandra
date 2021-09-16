package services

import models.Models.{
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
}

object TwitterService {
  def imp[F[_]: Sync](twitterFollows: TwitterFollows[F]): TwitterServiceAlgebra[F] =
    new TwitterServiceAlgebra[F] {
      def getUserByUserName(userName: String): F[TwitterGetUserByUserNameResponseData] =
        twitterFollows
          .getUserByUserName(userName)
          .adaptError(requestError => GetRequestError(requestError))

      def getTheFollowingOfUser(userName: String): F[FollowingIds] =
        twitterFollows.getUsersFollowedBy(userName)

      def getFollowersOfAUser(
          userName: String,
          maxNumberOfFollowersToReturn: Int
      ): F[TwitterGetUserByUserNameResponseDataWithProfileUrl] =
        twitterFollows.getUsersFollowing(userName, maxNumberOfFollowersToReturn)
    }
}
