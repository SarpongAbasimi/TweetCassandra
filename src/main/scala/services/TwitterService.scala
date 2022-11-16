package services

import models.Models.{
  FollowersIds,
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
  def getTheFollowingOfUser(userName: String): F[TwitterGetUserByUserNameResponseDataWithProfileUrl]
  def getFollowersOfAUser(
      userName: String,
      maxNumberOfFollowersToReturn: Int
  ): F[TwitterGetUserByUserNameResponseDataWithProfileUrl]
  def getIdsOfFollowersOf(
      userName: String
  ): F[FollowersIds]
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

      def getTheFollowingOfUser(
          userName: String
      ): F[TwitterGetUserByUserNameResponseDataWithProfileUrl] =
        twitterFollows
          .getFollowingOf(userName)
          .adaptError(error =>
            GetRequestError(
              s"An error occurred whiles getting the following of $userName",
              error
            )
          )

      def getFollowersOfAUser(
          userName: String,
          maxNumberOfFollowersToReturn: Int
      ): F[TwitterGetUserByUserNameResponseDataWithProfileUrl] =
        twitterFollows.getFollowersOf(userName, maxNumberOfFollowersToReturn)

      def getIdsOfFollowersOf(
          userName: String
      ): F[FollowersIds] =
        twitterFollows
          .getFollowersIdsOf(userName)
          .adaptError(
            GetRequestError(
              "An error occurred while getting the ids",
              _
            )
          )
    }
}
