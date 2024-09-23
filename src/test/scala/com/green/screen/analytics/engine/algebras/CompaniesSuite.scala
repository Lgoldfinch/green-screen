package com.green.screen.analytics.engine.algebras

import cats.effect.IO
import com.green.screen.analytics.engine.generators.*
import com.green.screen.analytics.engine.generators.companies.*
import fs2.io.net.Network
import munit.{CatsEffectSuite, ScalaCheckSuite}
import natchez.Trace.Implicits.noop
import org.scalacheck.effect.PropF.forAllF
import skunk.*
import skunk.implicits.*

class CompaniesSuite extends CatsEffectSuite with ScalaCheckSuite:

 val session = Session.single[IO](
    host = "localhost",
    port = 5432,
    user = "postgres",
    database = "green-screen-postgres",
    password = Some("password")
 )

  val deleteEverythingSql: Command[Void] = sql"DELETE FROM companies".command
  val deleteFromTransactions: Command[Void] = sql"DELETE FROM transactions".command

  override def beforeEach(context: BeforeEach): Unit = {
    session.use(s =>
      s.execute(deleteEverythingSql) >> s.execute(deleteFromTransactions)
    ).void.unsafeRunSync()
  }
  
  private val companiesAlgebra = Companies.make(session)
  
    test("Should be able to create and retrieve companies") {
      forAllF(nelGen(companyGen)) {
         companies =>
           val uuids = companies.map(_.uuid)

           for {
             _ <- companiesAlgebra.createCompanies(companies)
             retrievedCompanies <- uuids.traverse(companiesAlgebra.getCompany)
             result = retrievedCompanies.toList.flatten
           } yield assertEquals(result.sortBy(_.uuid), companies.toList.sortBy(_.uuid))


          
      }

    }
end CompaniesSuite
