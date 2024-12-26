package com.green.screen.analytics.engine.algebras

import cats.effect.kernel.{ Concurrent, MonadCancelThrow, Resource }
import com.green.screen.analytics.engine.domain.users.*
import cats.syntax.all.*

import skunk.Session
import skunk.*
import skunk.syntax.all.*
import com.green.screen.analytics.engine.domain.users.UserUuid.*
import com.green.screen.analytics.engine.domain.users.User.*
import UsersSQL.*

trait Users[F[_]]:
  def createUser(user: User): F[Unit]

  def getUser(userUuid: UserUuid): F[Option[User]]
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

end UsersSQL
