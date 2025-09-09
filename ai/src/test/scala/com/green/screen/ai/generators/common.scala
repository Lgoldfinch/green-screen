package com.green.screen.ai.generators

import com.green.screen.ai.domain.common.*
import org.scalacheck.Gen
import com.green.screen.common.generators.*
import eu.timepit.refined.types.string.NonEmptyString

object common {
  val aiRequestMessageGen: Gen[AIRequestMessage] =
    nonEmptyStringGen(AIRequestMessage.apply)

  val aiAskRequestGen: Gen[AIAskRequest] =
    aiRequestMessageGen.map(AIAskRequest.apply)
}
