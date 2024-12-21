package com.green.screen.analytics.engine.programs

import cats.MonadThrow
import com.green.screen.analytics.engine.algebras.Companies
import com.green.screen.analytics.engine.algebras.UserTransactions
import com.green.screen.analytics.engine.domain.companies.*
import com.green.screen.analytics.engine.domain.transactions.{CreateTransactionRequest, TransactionUuid, UserTransaction}
import cats.syntax.all.*

import java.util.UUID
import scala.util.control.NoStackTrace
import ProcessTransaction.CompanyNotFound
import CompanyName._

class ProcessTransaction[F[_]: MonadThrow](companies: Companies[F], transactions: UserTransactions[F]):
  def createTransaction(request: CreateTransactionRequest): F[Unit] = {

    val companyName = CompanyName(request.name.value)

    for {
      companyUuidOpt <- companies.getCompanyUuidByName(companyName)
      companyUuid <- companyUuidOpt.liftTo[F](CompanyNotFound(companyName))
      transaction = UserTransaction(
        TransactionUuid(UUID.randomUUID()),
        companyUuid,
        request.amount
      )
      _ <- transactions.createTransaction(transaction)
    } yield ()
}

end ProcessTransaction

object ProcessTransaction:
  case class CompanyNotFound(companyName: CompanyName) extends NoStackTrace {
    override def getMessage: String = s"Could not find Company: ${companyName.value}"
  }
end ProcessTransaction
