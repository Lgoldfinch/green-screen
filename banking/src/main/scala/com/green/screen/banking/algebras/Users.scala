package com.green.screen.banking.algebras

import cats.effect.kernel.{ Concurrent, MonadCancelThrow, Resource }
import cats.syntax.all.*
import skunk.Session
import skunk.*
import skunk.syntax.all.*
import com.green.screen.banking.domain.users.*
import UsersSQL.*

trait Users[F[_]]:
  def createUser(user: User): F[Unit]

  def getUser(userUuid: UserUuid): F[Option[User]]

  def getScore(userUuid: UserUuid): F[UserScore]
end Users

object Users:
  def make[F[_]](resource: Resource[F, Session[F]])(using MonadCancelThrow[F], Concurrent[F]): Users[F] = new Users[F] {
    override def createUser(user: User): F[Unit] = resource.use(
      _.prepare(insertUserCommand)
        .flatMap(
          _.execute(user)
        )
        .void
    )

    override def getUser(userUuid: UserUuid): F[Option[User]] = resource.use(
      _.prepare(getUserQuery).flatMap(
        _.option(userUuid)
      )
    )

    override def getScore(userUuid: UserUuid): F[UserScore] =
      resource
        .use(
          _.prepare(getUserScoreQuery)
            .flatMap(
              _.unique(userUuid)
            )
        )
  }

end Users

object UsersSQL:
  val insertUserCommand: Command[User] =
    sql"""
         INSERT INTO users VALUES ($userCodec)
       """.command

  val getUserQuery: Query[UserUuid, User] =
    sql"""
         SELECT uuid FROM users
         WHERE uuid = $userUuidCodec
         """.query(userCodec)

  val getUserScoreQuery: Query[UserUuid, UserScore] =
    sql"""
         SELECT COALESCE(ROUND(AVG(c.co2_emissions::numeric), 2)::float8, 0) FROM users u
           JOIN transactions t
              ON u.uuid = t.user_uuid
           JOIN companies c
              ON t.company_uuid = c.uuid
         WHERE u.uuid = $userUuidCodec

         """.query(userScoreCodec)

end UsersSQL
