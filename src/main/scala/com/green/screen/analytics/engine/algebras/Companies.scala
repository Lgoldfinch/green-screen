package com.green.screen.analytics.engine.algebras

import cats.data.NonEmptyList
import cats.effect.*
import cats.effect.kernel.{Concurrent, MonadCancelThrow, Resource}
import cats.syntax.all.*
import com.green.screen.analytics.engine.algebras.CompaniesSQL.*
import com.green.screen.analytics.engine.domain.companies.*
import com.green.screen.analytics.engine.domain.companies.Company.companyCodec
import com.green.screen.analytics.engine.domain.companies.CompanyUuid.companyUuidCodec
import skunk.*
import skunk.syntax.all.*

trait Companies[F[_]] {
  def createCompanies(companies: NonEmptyList[Company]): F[Unit]

  def getCompany(companyUuid: CompanyUuid): F[Option[Company]]
}

object Companies:
  def make[F[_]: MonadCancelThrow: Concurrent](resource: Resource[F, Session[F]]): Companies[F] = new Companies[F]:
    override def createCompanies(cs: NonEmptyList[Company]): F[Unit] = {
      val companies = cs.toList
      resource.use(
        _.prepare(queryInsertCompanies(companies)).flatMap(
          _.execute(companies).void
        )
      )
    }

    override def getCompany(companyUuid: CompanyUuid): F[Option[Company]] = resource.use(_.prepare(queryGetCompany).flatMap(
      _.option(companyUuid)
      )
    )

end Companies

object CompaniesSQL:
  def queryInsertCompanies(companies: List[Company]): Command[companies.type] = {
    val enc = companyCodec.values.list(companies)
    sql"""INSERT INTO companies VALUES $enc""".command
  }

  val queryGetCompany: Query[CompanyUuid, Company] =
    sql"""
         SELECT * FROM companies
         WHERE uuid = $companyUuidCodec
         """.query(companyCodec)
end CompaniesSQL

