package com.green.screen.analytics.engine.algebras

import cats.effect.*
import com.green.screen.analytics.engine.generators.users.*
import com.green.screen.analytics.engine.{ PostgresSuite, ResourceSuite }

object UsersSuite extends PostgresSuite:
  test("Should be able to insert and get a user") { postgres =>
    val usersAlgebra: Users[IO] = Users.make[IO](postgres)

    forall(userGen) { user =>
      for {
        _      <- usersAlgebra.createUser(user)
        result <- usersAlgebra.getUser(user.uuid)
      } yield expect.same(result, Some(user))
    }
  }
end UsersSuite
