package com.green.screen.banking.generators

import org.scalacheck.Gen
import com.green.screen.banking.domain.companies.*
import com.green.screen.common.generators.*

object companies:
  val companyUuidGen: Gen[CompanyUuid]                     = Gen.uuid.map(CompanyUuid.apply)
  val companyNameGen: Gen[CompanyName]                     = nonEmptyStringGen(CompanyName.apply)
  val co2EmissionGen: Gen[CompanyCo2EmissionsMetricTonnes] =
    Gen.double.map(CompanyCo2EmissionsMetricTonnes.apply)

  val companyGen: Gen[Company] = for {
    uuid        <- companyUuidGen
    name        <- companyNameGen
    co2Emission <- Gen.option(co2EmissionGen)
  } yield Company(uuid, name, co2Emission)
  
  val createCompanyRequestGen: Gen[CreateCompanyRequest] = for {
    name        <- companyNameGen
    co2Emission <- Gen.option(co2EmissionGen)
  } yield CreateCompanyRequest(name, co2Emission)
end companies
