package com.green.screen.banking.programs

import cats.MonadThrow
import com.green.screen.banking.domain.users.*
import cats.syntax.all.*
import com.green.screen.banking.algebras.Users

import scala.util.control.NoStackTrace

class GetUserScores[F[_]: MonadThrow](users: Users[F]):
  def getScores(userUuid: UserUuid): F[UserScore] =
    for {
      userOpt <- users.getUser(userUuid)
      _       <- userOpt.liftTo[F](GetUserScores.UserNotFound(userUuid))
      score   <- users.getScore(userUuid)
    } yield score
end GetUserScores

object GetUserScores:
  case class UserNotFound(userUuid: UserUuid) extends NoStackTrace {
    override def getMessage: String = s"Could not find User: ${userUuid.value}"
  }
end GetUserScores
