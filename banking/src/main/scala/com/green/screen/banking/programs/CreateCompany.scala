package com.green.screen.banking.programs

import cats.MonadThrow
import com.green.screen.banking.algebras.Companies
import com.green.screen.banking.domain.companies.{Company, CompanyUuid, CreateCompanyRequest}
import org.typelevel.log4cats.Logger
import com.green.screen.common.effects.GenUUID
import cats.syntax.all.*

final class CreateCompany[F[_]: { Logger, GenUUID, MonadThrow }](companies: Companies[F]) {
  def createCompany(request: CreateCompanyRequest): F[Company] = {
    for {
      uuid <- GenUUID[F].make.map(CompanyUuid.apply)
      company = Company(uuid, request.name, request.co2Emissions)
      _ <- companies.createCompany(company)
      _ <- Logger[F].info(s"Created company: $company")
    } yield company
  }
}
