package com.green.screen.banking.programs

import cats.MonadThrow
import com.green.screen.banking.algebras.Users
import com.green.screen.banking.domain.users.{User, UserUuid}
import org.typelevel.log4cats.Logger
import com.green.screen.common.effects.GenUUID
import cats.syntax.all.*

final class CreateUser[F[_]: { Logger, GenUUID, MonadThrow }](users: Users[F]) {
  def createUser(): F[User] = {
    
    for {
      userUuid <- GenUUID[F].make.map(UserUuid.apply)
      user = User(userUuid)
      _ <- users.createUser(user)
      _ <- Logger[F].info(s"Created user: ${user.uuid}")
    } yield user
  }
}
