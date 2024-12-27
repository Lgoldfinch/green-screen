package com.green.screen.analytics.engine.algebras

import cats.data.NonEmptyList
import cats.effect.*
import cats.effect.kernel.{ Concurrent, MonadCancelThrow, Resource }
import cats.syntax.all.*
import com.green.screen.analytics.engine.algebras.CompaniesSQL.*
import com.green.screen.analytics.engine.domain.companies.{ CompanyName, * }
import com.green.screen.analytics.engine.domain.companies.Company.companyCodec
import com.green.screen.analytics.engine.domain.companies.CompanyUuid.companyUuidCodec
import com.green.screen.analytics.engine.domain.companies.CompanyName.*
import com.green.screen.analytics.engine.domain.companies.CompanyCo2EmissionsMetricTonnes.co2EmissionsCodec
import skunk.*
import skunk.syntax.all.*
import skunk.codec.all.*

trait Companies[F[_]] {
  def createCompanies(companies: NonEmptyList[Company]): F[Unit]
  def createCompany(company: Company): F[Unit]

  def getCompany(companyUuid: CompanyUuid): F[Option[Company]]

  def getCompanyUuidByName(transactionEntity: CompanyName): F[Option[CompanyUuid]]
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

    override def createCompany(cs: Company): F[Unit] = {
      resource.use(
        _.prepare(companyCommand).flatMap(
          _.execute((cs.uuid, cs.name, cs.co2Emissions, cs.name.value.value)).void
        )
      )
    }

    override def getCompany(companyUuid: CompanyUuid): F[Option[Company]] = resource.use(
      _.prepare(queryGetCompany).flatMap(
        _.option(companyUuid)
      )
    )

    override def getCompanyUuidByName(companyName: CompanyName): F[Option[(CompanyUuid)]] = resource.use(
      _.prepare(queryGetUuidByName).flatMap(
        _.option((CompanyName(companyName.value), CompanyName(companyName.value))).map(_.map(_._1))
      )
    )

end Companies

object CompaniesSQL:
  def queryInsertCompanies(companies: List[Company]): Command[companies.type] = {
    val enc = companyCodec.values.list(companies)
    sql"""INSERT INTO companies VALUES $enc""".command
  }

  val companyCommand: Command[(CompanyUuid, CompanyName, CompanyCo2EmissionsMetricTonnes, String)] =
    sql"""
         INSERT INTO companies VALUES ($companyUuidCodec, $companyNameCodec, $co2EmissionsCodec, to_tsvector($text))
       """.command

  val queryGetCompany: Query[CompanyUuid, Company] =
    sql"""
         SELECT uuid, name, co2_emissions FROM companies
         WHERE uuid = $companyUuidCodec
         """.query(companyCodec)

  val queryGetUuidByName: Query[(CompanyName, CompanyName), (CompanyUuid, Float)] =
    sql"""
         SELECT uuid, ts_rank(name_tsv, plainto_tsquery($companyNameCodec)) AS rank FROM companies
         WHERE name_tsv @@ plainto_tsquery($companyNameCodec)
         ORDER BY rank DESC
         LIMIT 1
         """.query(companyUuidCodec ~ float4)

end CompaniesSQL
