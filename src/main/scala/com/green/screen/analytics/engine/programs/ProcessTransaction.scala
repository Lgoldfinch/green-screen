package com.green.screen.analytics.engine.programs

import cats.Monad
import com.green.screen.analytics.engine.algebras.Companies
import com.green.screen.analytics.engine.algebras.UserTransactions
import com.green.screen.analytics.engine.domain.companies.{Company, CompanyCo2EmissionsMetricTonnes, CompanyName, CompanyUuid}
import com.green.screen.analytics.engine.domain.transactions.CreateTransactionRequest
import eu.timepit.refined.types.string.NonEmptyString
import cats.syntax.all._
import java.util.UUID

class ProcessTransaction[F[_]: Monad](companies: Companies[F], transactions: UserTransactions[F]):
  def run(request: CreateTransactionRequest): F[Unit] = {
    for {
      _ <- companies.createCompany(Company(CompanyUuid(UUID.randomUUID()), CompanyName(NonEmptyString.unsafeFrom("Fucka doo dle do .com")), CompanyCo2EmissionsMetricTonnes(12)))
    } yield ()
  }

  def runAgain: F[Unit] = {
    for {
      _ <- companies.createCompany(Company(CompanyUuid(UUID.randomUUID()), CompanyName(NonEmptyString.unsafeFrom("Fucka doo dle do .com")), CompanyCo2EmissionsMetricTonnes(12)))
    } yield ()

  }

end ProcessTransaction

