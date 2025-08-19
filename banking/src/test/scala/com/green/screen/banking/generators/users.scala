package com.green.screen.generators

import org.scalacheck.Gen
import com.green.screen.banking.domain.*
import com.green.screen.common.generators.commonGenerators.*

object users:
  val userUuidGen: Gen[UserUuid] = Gen.uuid.map(UserUuid.apply)

  val userGen: Gen[User] = userUuidGen.map(User.apply)

  val userScore: Gen[UserScore] = nonNegDoubleGen(UserScore.apply)
end users
