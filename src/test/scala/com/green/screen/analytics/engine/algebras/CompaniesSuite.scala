package com.green.screen.analytics.engine.algebras

import cats.effect.*
import com.green.screen.analytics.engine.domain.companies.Company.companyShow
import com.green.screen.analytics.engine.generators.*
import com.green.screen.analytics.engine.generators.companies.companyGen
import com.green.screen.analytics.engine.{ PostgresSuite, ResourceSuite }
import weaver.*
import weaver.scalacheck.*

object CompaniesSuite extends PostgresSuite:
  test("Should be able to insert and retrieve a company") { postgres =>
    val companiesAlgebra: Companies[IO] = Companies.make[IO](postgres)
    forall(companyGen) { company =>
      for {
        _                  <- companiesAlgebra.createCompany(company)
        retrievedCompanies <- companiesAlgebra.getCompany(company.uuid)
      } yield expect.same(retrievedCompanies, Some(company))
    }
  }

  test("Should be able to retrieve a company by name") { postgres =>
    val companiesAlgebra: Companies[IO] = Companies.make[IO](postgres)
    val gen = for {
      company1 <- companyGen
    } yield (company1, company2)

    forall(gen) { case (company1, company2) =>
      for {
        _                <- companiesAlgebra.createCompany(company1)
        _                <- companiesAlgebra.createCompany(company2)
        retrievedCompany <- companiesAlgebra.getCompanyUuidByName(company1.name)
      } yield {
        expect.same(
          retrievedCompany,
          Some(company1.uuid)
        )
      }
    }
  }
end CompaniesSuite
