package com.green.screen.analytics.engine.generators

import com.green.screen.analytics.engine.domain.companies.CompanyUuid
import org.scalacheck.Gen

object CompanyGenerators {
  val companyUuidGen: Gen[CompanyUuid] = Gen.uuid.map(CompanyUuid.apply)
}
