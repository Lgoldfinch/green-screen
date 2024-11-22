package com.green.screen.analytics.engine.algebras

import cats.effect.*
import com.green.screen.analytics.engine.domain.companies.Company.companyShow
import com.green.screen.analytics.engine.generators.*
import com.green.screen.analytics.engine.generators.companies.*
import com.green.screen.analytics.engine.{PostgresSuite, ResourceSuite}
import weaver.*
import weaver.scalacheck.*

object CompaniesSuite extends PostgresSuite:
  test("Should be able to create and retrieve companies") { postgres =>
    val companiesAlgebra: Companies[IO] = Companies.make[IO](postgres)
    forall(nelGen(companyGen)) { companies =>
      val uuids = companies.map(_.uuid)

      for {
        _ <- companiesAlgebra.createCompanies(companies)
        retrievedCompanies <- uuids.traverse(companiesAlgebra.getCompany)
        result = retrievedCompanies.toList.flatten
      } yield expect.same(retrievedCompanies.toList.flatten.sortBy(_.uuid), companies.toList.sortBy(_.uuid))
    }
  }
end CompaniesSuite
