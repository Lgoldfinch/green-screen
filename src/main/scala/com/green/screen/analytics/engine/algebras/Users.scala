package com.green.screen.analytics.engine.algebras

import cats.effect.kernel.{ Concurrent, MonadCancelThrow, Resource }
import com.green.screen.analytics.engine.domain.users.*
import cats.syntax.all.*

import skunk.Session
import skunk.*
import skunk.syntax.all.*
import com.green.screen.analytics.engine.domain.users.UserUuid.*
import com.green.screen.analytics.engine.domain.users.User.*
import com.green.screen.analytics.engine.domain.users.UserScore.*
import UsersSQL.*

trait Users[F[_]]:
  def createUser(user: User): F[Unit]

  def getUser(userUuid: UserUuid): F[Option[User]]

  def getScore(userUuid: UserUuid): F[UserScore]
end Users

object Users:
  def make[F[_]: MonadCancelThrow: Concurrent](resource: Resource[F, Session[F]]): Users[F] = new Users[F] {
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
         SELECT ROUND(AVG(c.co2_emissions::numeric), 2)::float8 FROM users u
           JOIN transactions t
              ON u.uuid = t.user_uuid
           JOIN companies c
              ON t.company_uuid = c.uuid
         WHERE u.uuid = $userUuidCodec
         """.query(userScoreCodec)

end UsersSQL
