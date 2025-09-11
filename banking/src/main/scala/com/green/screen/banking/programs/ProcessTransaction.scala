package com.green.screen.banking.programs

import cats.MonadThrow
import com.green.screen.banking.domain.transactions.*
import com.green.screen.banking.domain.companies.CompanyName
import cats.syntax.all.*
import ProcessTransaction.CompanyNotFound
import com.green.screen.banking.algebras.{Companies, OpenBankingTransactions}
import com.green.screen.common.effects.GenUUID
import com.green.screen.common.misc.CreatedAt
import org.typelevel.log4cats.Logger

import java.util.UUID
import scala.util.control.NoStackTrace

class ProcessTransaction[F[_]](companies: Companies[F], transactions: OpenBankingTransactions[F])(using
    MonadThrow[F],
    Logger[F],
    GenUUID[F]
):
  def createTransaction(request: CreateTransactionRequest): F[Unit] = {

    val companyName = request.name.toCompanyName

    for {
      companyUuidOpt  <- companies.getCompanyUuidByName(companyName)
      companyUuid     <- companyUuidOpt.liftTo[F](CompanyNotFound(companyName))
      transactionUuid <- GenUUID[F].make.map(TransactionUuid.apply)
      transaction = OpenBankingTransaction(
        transactionUuid,
        companyUuid,
        request.userUuid,
        request.amount,
        CreatedAt.now
      )
      _ <- transactions.createTransaction(transaction)
      _ <- Logger[F].info(s"Inserted transaction ${transaction.uuid} for company ${transaction.companyUuid}")
    } yield ()
  }

end ProcessTransaction

object ProcessTransaction:
  case class CompanyNotFound(companyName: CompanyName) extends NoStackTrace {
    override def getMessage: String = s"Could not find Company: ${companyName.value}"
  }
end ProcessTransaction
