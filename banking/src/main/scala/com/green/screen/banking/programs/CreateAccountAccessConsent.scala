package com.green.screen.banking.programs

import cats.syntax.all.*
import cats.{ ApplicativeThrow, MonadThrow }
import com.green.screen.banking.algebras.UserOpenApiData
import com.green.screen.banking.algebras.clients.AccountAccessConsentClient
import com.green.screen.common.effects.GenUUID
import com.green.screen.banking.domain.users.*
import com.green.screen.banking.domain.openAPI.*
import com.green.screen.banking.domain.openAPI.AccountAccessConsentsStatus.AWAU
import com.green.screen.common.misc.CreatedAt
import org.typelevel.log4cats.Logger

import java.time.Instant

final class CreateAccountAccessConsent[F[_]](
    accountAccessConsentClient: AccountAccessConsentClient[F],
    userOpenApiData: UserOpenApiData[F]
)(using MonadThrow[F], Logger[F], GenUUID[F]):
  def run(request: CreateAccountAccessConsentsRequest, bankPrefix: BankPrefix, userUuid: UserUuid): F[Unit] =
    for {
      response <- accountAccessConsentClient.setAccountAccessConsent(request, bankPrefix)
      _        <- ApplicativeThrow[F].raiseWhen(response.status != AWAU)(
        new RuntimeException(
          s"Account access consent status should be AWAU, but instead got ${response.status} for User: $userUuid"
        )
      )
      userOpenApiDataUuid <- GenUUID[F].make.map(UserOpenApiDataUuid.apply)
      userOpenApiDataDB = UserOpenApiDataDB(
        userOpenApiDataUuid,
        response.consentId,
        userUuid,
        CreatedAt(Instant.now)
      )
      _ <- userOpenApiData.create(userOpenApiDataDB)
      _ <- Logger[F].info(
        s"Account access consent awaiting authorisation for Consent id: ${response.consentId} and User: $userUuid"
      )
    } yield ()
end CreateAccountAccessConsent
