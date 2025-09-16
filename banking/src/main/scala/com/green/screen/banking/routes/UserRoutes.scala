package com.green.screen.banking.routes

import cats.effect.Concurrent
import cats.syntax.all.*
import com.green.screen.banking.domain.users.UserUuid
import com.green.screen.banking.programs.GetUserScores.UserNotFound
import com.green.screen.banking.programs.{CreateUser, GetUserScores}
import com.green.screen.common.auth.UserType
import org.http4s.*
import org.http4s.dsl.Http4sDsl
import org.typelevel.log4cats.Logger
import org.http4s.circe.CirceEntityCodec.*

object UserRoutes:
  def routes[F[_]](
      createUser: CreateUser[F],
      userScores: GetUserScores[F]
  )(using Concurrent[F], Logger[F]): AuthedRoutes[UserType, F] = {
    val dsl = new Http4sDsl[F] {}
    import dsl.*

    AuthedRoutes.of {
      case authReq @ POST -> Root / "users" as _ =>
        createUser.createUser().flatMap( user => Created(user.uuid))

      case authReq @ GET -> Root / "users" / UUIDVar(userUuid) / "score" as _ =>
        userScores.getScores(UserUuid(userUuid)).flatMap(Ok(_)).recoverWith { case err: UserNotFound =>
          NotFound(err.getMessage)
        }
    }
  }
end UserRoutes
