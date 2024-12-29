package com.green.screen.analytics.engine.generators

import com.green.screen.analytics.engine.domain.*

import org.scalacheck.Gen

object users:
  val userUuidGen: Gen[UserUuid] = Gen.uuid.map(UserUuid.apply)

  val userGen: Gen[User] = userUuidGen.map(User.apply)
end users
