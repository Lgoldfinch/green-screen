package com.green.screen.analytics.engine.generators

import com.green.screen.analytics.engine.domain.companies.*
import org.scalacheck.Gen

object companies:
  val companyUuidGen: Gen[CompanyUuid] = Gen.uuid.map(CompanyUuid.apply)
  val companyNameGen: Gen[CompanyName] = nonEmptyStringGen(CompanyName.apply)
  val co2EmissionGen: Gen[CompanyCo2EmissionsMetricTonnes] =
    Gen.double.map(CompanyCo2EmissionsMetricTonnes.apply)

  val companyGen: Gen[Company] = for {
    uuid        <- companyUuidGen
    name        <- companyNameGen
    co2Emission <- co2EmissionGen
  } yield Company(uuid, name, co2Emission)
end companies
