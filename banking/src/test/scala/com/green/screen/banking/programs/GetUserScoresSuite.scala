package com.green.screen.banking.programs

import org.scalacheck.effect.PropF.*
import com.green.screen.banking.generators.users.*
import com.green.screen.banking.algebras.Users
import com.green.screen.banking.programs.GetUserScores.UserNotFound
import munit.{ CatsEffectSuite, ScalaCheckEffectSuite }
import cats.effect.IO
import cats.syntax.all.*
import com.green.screen.banking.domain.*

class GetUserScoresSuite extends CatsEffectSuite with ScalaCheckEffectSuite {

  test("Should fail if no user is found for the given user UUID") {

    val failedUsersAlgebra: Users[IO] = new TestUsers {
      override def getUser(userUuid: UserUuid): IO[Option[User]] = IO(none[User])
    }

    forAllF(userUuidGen) { userUuid =>
      val result = GetUserScores(failedUsersAlgebra).getScores(userUuid)

      result.attempt.flatMap {
        case Left(value) if value.isInstanceOf[UserNotFound] => IO(assert(true))
        case Left(value)  => IO(fail(s"Failed with message ${value.getMessage}, but needed to be UserNotFound error"))
        case Right(value) => IO(fail("This test should have failed"))
      }
    }
  }

  test("Should succeed if a score for the user is found") {
    def usersAlgebra(getUserResult: User, getUserScore: UserScore): Users[IO] = new TestUsers {
      override def getScore(userUuid: UserUuid): IO[UserScore]   = IO.pure(getUserScore)
      override def getUser(userUuid: UserUuid): IO[Option[User]] = IO.pure(Some(getUserResult))
    }

    forAllF(userGen, userScore, userUuidGen) { case (user, userScore, userUuid) =>
      val users  = usersAlgebra(user, userScore)
      val result = GetUserScores[IO](users).getScores(userUuid)

      result.assertEquals(userScore)
    }
  }
}
