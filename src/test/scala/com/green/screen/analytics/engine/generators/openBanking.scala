package com.green.screen.analytics.engine.generators

import com.green.screen.analytics.engine.config.OpenApiBankingConfig
import org.scalacheck.Gen

object openBanking:
  val openApiBankingConfig: Gen[OpenApiBankingConfig] = for {
    orgId             <- Gen.alphaStr
    softwareStatement <- Gen.alphaStr
    trustAnchorId     <- Gen.alphaStr
  } yield OpenApiBankingConfig(orgId, softwareStatement, trustAnchorId)
end openBanking
