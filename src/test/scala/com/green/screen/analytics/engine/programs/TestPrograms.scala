package com.green.screen.analytics.engine.programs

import cats.effect.IO
import com.green.screen.analytics.engine.domain.*
import com.green.screen.analytics.engine.algebras.Users

private class TestUsers extends Users[IO] {
  override def createUser(user: User): IO[Unit] = ???

  override def getScore(userUuid: UserUuid): IO[UserScore] = ???

  override def getUser(userUuid: UserUuid): IO[Option[User]] = ???
}
