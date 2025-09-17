package com.green.screen.banking.programs

import cats.Id
import cats.syntax.all.*
import com.green.screen.banking.algebras.TestCompanies
import com.green.screen.banking.domain.companies.Company
import com.green.screen.banking.generators.companies.createCompanyRequestGen
import com.green.screen.common.GivenInstances.given
import munit.{FunSuite, ScalaCheckSuite}
import org.scalacheck.Prop.forAll
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.noop.NoOpLogger

class CreateCompanySuite extends FunSuite with ScalaCheckSuite {

  given Logger[Id] = NoOpLogger[Id]

  test("Should return insert a company when given a valid request") {
    forAll(createCompanyRequestGen) { createCompanyRequest =>
      var insertedCompany: Option[Company] = None

      val companiesAlgebra = new TestCompanies[Id] {
        override def createCompany(company: Company): Id[Unit] = {
          insertedCompany = Some(company)
          (): Unit
        }
      }

        val program = new CreateCompany[Id](companiesAlgebra)

        val result = program.createCompany(createCompanyRequest)

        assertEquals(insertedCompany.map(c => (c.name, c.co2Emissions)), (createCompanyRequest.name, createCompanyRequest.co2Emissions).some)
    }
  }
}
