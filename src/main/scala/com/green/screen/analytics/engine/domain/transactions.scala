package com.green.screen.analytics.engine.domain

import cats.syntax.all.*
import com.green.screen.analytics.engine.domain.companies.CompanyUuid
import com.green.screen.analytics.engine.domain.companies.CompanyUuid.companyUuidCodec
import com.green.screen.analytics.engine.domain.transactions.TransactionAmount.transactionAmountCodec
import com.green.screen.analytics.engine.domain.transactions.TransactionUuid.*
import skunk.Codec
import skunk.codec.all.*

import java.util.UUID

object transactions {

  opaque type TransactionUuid = UUID

  object TransactionUuid {
    def apply(uuid: UUID): TransactionUuid = uuid

    extension (t: TransactionUuid)
      def value: UUID = t

    val transactionUuidCodec: Codec[TransactionUuid] = uuid.imap(TransactionUuid.apply)(_.value)
  }


  opaque type TransactionAmount = Double

  object TransactionAmount {
    def apply(amount: Double): TransactionAmount = amount

    val transactionAmountCodec: Codec[TransactionAmount] = float8.imap(TransactionAmount.apply)(
      _.value
    )

    extension (t: TransactionAmount)
      def value: Double = t
  }

  // Prefixed User here to differentiate from the Transaction class from Skunk.
  final case class UserTransaction(uuid: TransactionUuid, companyUuid: CompanyUuid, amount: TransactionAmount)

  object UserTransaction:
    val transactionCodec: Codec[UserTransaction] =
      (transactionUuidCodec, companyUuidCodec, transactionAmountCodec).tupled.imap(UserTransaction.apply) {
        case UserTransaction(uuid, companyUuid, amount) => (uuid, companyUuid, amount)
      }
  end UserTransaction
}
