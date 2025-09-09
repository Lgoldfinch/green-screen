package com.green.screen.banking.programs

import cats.syntax.all.*
import cats.{ ApplicativeThrow, MonadThrow }
import com.green.screen.banking.algebras.UserOpenBankingData
import com.green.screen.banking.algebras.clients.AccountAccessConsentClient
import com.green.screen.common.effects.GenUUID
import com.green.screen.banking.domain.users.*
import com.green.screen.banking.domain.openBanking.*
import com.green.screen.banking.domain.openBanking.AccountAccessConsentsStatus.AWAU
import com.green.screen.common.misc.CreatedAt
import org.typelevel.log4cats.Logger

import java.time.Instant

final class CreateAccountAccessConsent[F[_]](
    accountAccessConsentClient: AccountAccessConsentClient[F],
    userOpenBankingData: UserOpenBankingData[F]
)(using MonadThrow[F], Logger[F], GenUUID[F]):
  def run(request: CreateAccountAccessConsentsRequest, bankPrefix: BankPrefix, userUuid: UserUuid): F[Unit] =
    for {
      response <- accountAccessConsentClient.setAccountAccessConsent(request, bankPrefix)
      _        <- ApplicativeThrow[F].raiseWhen(response.status != AWAU)(
        new RuntimeException(
          s"Account access consent status should be AWAU, but instead got ${response.status} for User: $userUuid"
        )
      )
      userOpenBankingDataUuid <- GenUUID[F].make.map(UserOpenBankingDataUuid.apply)
      userOpenBankingDataDB = UserOpenBankingDataDB(
        userOpenBankingDataUuid,
        response.consentId,
        userUuid,
        CreatedAt(Instant.now)
      )
      _ <- userOpenBankingData.create(userOpenBankingDataDB)
      _ <- Logger[F].info(
        s"Account access consent awaiting authorisation for Consent id: ${response.consentId} and User: $userUuid"
      )
    } yield ()
end CreateAccountAccessConsent
