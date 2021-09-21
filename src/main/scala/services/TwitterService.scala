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
import database.db.UnFollowersDataBase
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
  def getUnFollowersDetails(userName: String): F[TwitterGetUserByUserNameResponseDataWithProfileUrl]
}

object TwitterService {
  def imp[F[_]: Sync](
      twitterFollows: TwitterFollows[F],
      unFollowDataBase: UnFollowersDataBase[F]
  ): TwitterServiceAlgebra[F] =
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

//      def getUnFollowersDetails(
//          userName: String
//      ): F[TwitterGetUserByUserNameResponseDataWithProfileUrl] =
//        twitterFollows.getUnFollowersDetailsFor(userName).adaptError(GetRequestError(_))

      def getUnFollowersDetails(
          userName: String
      ): F[TwitterGetUserByUserNameResponseDataWithProfileUrl] = for {
        unFollowersDetails <- twitterFollows
          .getUnFollowersDetailsFor(userName)
          .adaptError(GetRequestError(_))
        _ <- unFollowersDetails.data.traverse(
          unFollowDataBase.twitterUnFollowers
            .storeUnFollowers(_)
        )
      } yield unFollowersDetails
    }
}
