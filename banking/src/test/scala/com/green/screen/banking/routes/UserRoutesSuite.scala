package com.green.screen.banking.routes

import cats.effect.IO
import cats.syntax.all.*
import com.green.screen.banking.domain.users.{User, UserScore, UserUuid}
import com.green.screen.banking.generators.users.{userScore, userUuidGen}
import com.green.screen.banking.programs.{CreateUser, GetUserScores, TestUsers}
import com.green.screen.common.auth.UserType
import munit.{CatsEffectSuite, ScalaCheckEffectSuite}
import org.http4s.*
import org.http4s.Method.POST
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.scalacheck.effect.PropF.forAllF
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.noop.NoOpLogger

class UserRoutesSuite extends CatsEffectSuite with ScalaCheckEffectSuite {

  given Logger[IO] = NoOpLogger[IO]

  def routes(mkResponse: IO[Response[IO]]): HttpApp[IO] = {
    HttpRoutes
      .of[IO] { case POST -> root / "users" =>
        mkResponse
      }
      .orNotFound
  }

  test("POST /users should return 201 Created for valid request") {

    val userAlgebra = new TestUsers[IO] {
      override def createUser(user: User): IO[Unit] = {
        IO.unit
      }
    }

    val createUserAlgebra = new CreateUser(userAlgebra)
    val getUserScores = new GetUserScores(userAlgebra)

    val authedReq = Request[IO](Method.POST, uri"/users")
    val routes = UserRoutes.routes[IO](createUserAlgebra, getUserScores)

    val resp = routes.run(AuthedRequest(UserType.Admin, authedReq)).value

    resp.map(_.map(_.status)).assertEquals(Status.Created.some)
  }

  test("GET /users/:uuid/score should return 200 OK and the correct score") {
    forAllF(userUuidGen, userScore) { (uuid, score) =>
      val userAlgebra = new TestUsers[IO] {
        override def getScore(userUuid: UserUuid): IO[UserScore] =
          IO.pure(score)

        override def getUser(userUuid: UserUuid): IO[Option[User]] =
          IO.pure(Some(User(uuid)))
      }

      val createUserAlgebra = new CreateUser(userAlgebra)
      val getUserScores = new GetUserScores(userAlgebra)
      val routes = UserRoutes.routes[IO](createUserAlgebra, getUserScores)
      val authedReq = Request[IO](Method.GET, uri"/users".addPath(uuid.value.toString).addPath("score"))
      val respIO = routes.run(AuthedRequest(UserType.Admin, authedReq)).value

      respIO.flatMap {
        case Some(resp) =>
          IO.pure(assertEquals(resp.status, Status.Ok))

        case None => IO.pure(fail("No response returned"))
      }
    }
  }

  test("GET /users/:uuid/score should return Not found if user does not exist") {
    forAllF(userUuidGen, userScore) { (uuid, score) =>
      val userAlgebra = new TestUsers[IO] {
        override def getUser(userUuid: UserUuid): IO[Option[User]] =
          IO.pure(none[User])
      }

      val createUserAlgebra = new CreateUser(userAlgebra)
      val getUserScores = new GetUserScores(userAlgebra)
      val routes = UserRoutes.routes[IO](createUserAlgebra, getUserScores)
      val authedReq = Request[IO](Method.GET, uri"/users".addPath(uuid.value.toString).addPath("score"))
      val respIO = routes.run(AuthedRequest(UserType.Admin, authedReq)).value

      respIO.flatMap {
        case Some(resp) =>
          IO.pure(assertEquals(resp.status, Status.NotFound))
        case None => IO.pure(fail("No response returned"))
      }
    }
  }
}