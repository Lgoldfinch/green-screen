package com.green.screen.banking.programs

import cats.Id
import com.green.screen.banking.algebras.TestUsers
import com.green.screen.banking.domain.users.User
import munit.{FunSuite, ScalaCheckSuite}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.noop.NoOpLogger
import com.green.screen.common.GivenInstances.given

class CreateUserSuite extends FunSuite with ScalaCheckSuite {

  given Logger[Id] = NoOpLogger[Id]

  test("Should insert a user when given a valid request") {
      var insertedUser: Option[User] = None
      val usersAlgebra = new TestUsers[Id] {
        override def createUser(user: User): Id[Unit] = {
          insertedUser = Some(user)
          (): Unit
        }
      }

      val program = new CreateUser[Id](usersAlgebra)
      val result = program.createUser()

      assert(insertedUser.isDefined, "User was not inserted")
    }
}
