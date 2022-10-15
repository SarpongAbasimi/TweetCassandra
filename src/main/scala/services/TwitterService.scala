package services

import models.Models.{
  FollowersIds,
  FollowingIds,
  TwitterGetUserByUserNameResponseData,
  TwitterGetUserByUserNameResponseDataWithProfileUrl
}
import algebras.Twitter
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
      twitterFollows: Twitter[F],
      unFollowDataBase: UnFollowersDataBase[F]
  ): TwitterServiceAlgebra[F] =
    new TwitterServiceAlgebra[F] {
      def getUserByUserName(userName: String): F[TwitterGetUserByUserNameResponseData] =
        twitterFollows
          .getUserBy(userName)
          .adaptError(error =>
            GetRequestError(
              "An error occurred whiles getting username",
              error
            )
          )

      def getTheFollowingOfUser(userName: String): F[FollowingIds] =
        twitterFollows
          .getUsersFollowedBy(userName)
          .adaptError(error =>
            GetRequestError(
              s"An error occurred whiles getting the followers of $userName",
              error
            )
          )

      def getFollowersOfAUser(
          userName: String,
          maxNumberOfFollowersToReturn: Int
      ): F[TwitterGetUserByUserNameResponseDataWithProfileUrl] =
        twitterFollows.getUsersFollowing(userName, maxNumberOfFollowersToReturn)

      def getTheIdsOfTheFollowersOf(userName: String): F[FollowersIds] =
        twitterFollows
          .getIdsOfUsersFollowing(userName)
          .adaptError(
            GetRequestError(
              "An error occurred while getting the ids",
              _
            )
          )

      def getUnFollowers(userName: String): F[List[Long]] =
        twitterFollows.getUnFollowersOf(userName).compile.toList

      def getUnFollowersDetails(
          userName: String
      ): F[TwitterGetUserByUserNameResponseDataWithProfileUrl] = for {
        unFollowersDetails <- twitterFollows
          .getUnFollowersDetailsFor(userName)
          .adaptError(
            GetRequestError(
              s"An error occurred whiles getting followers details for $userName",
              _
            )
          )
        _ <- unFollowersDetails.data.traverse(data =>
          unFollowDataBase.twitterUnFollowers
            .storeUnFollowers(data)
        )
      } yield unFollowersDetails
    }
}
