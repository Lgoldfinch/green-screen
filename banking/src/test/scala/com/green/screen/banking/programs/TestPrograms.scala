package com.green.screen.banking.programs

import com.green.screen.banking.algebras.Users
import cats.effect.IO
import com.green.screen.banking.domain.users.*

private class TestUsers extends Users[IO] {
  override def createUser(user: User): IO[Unit] = ???

  override def getScore(userUuid: UserUuid): IO[UserScore] = ???

  override def getUser(userUuid: UserUuid): IO[Option[User]] = ???
}
