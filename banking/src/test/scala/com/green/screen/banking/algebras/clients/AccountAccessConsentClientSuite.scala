package com.green.screen.banking.algebras.clients

import com.green.screen.banking.generators.openBanking.*
import munit.{ CatsEffectSuite, ScalaCheckEffectSuite }
import org.http4s
import org.http4s.*
import org.http4s.Status.{ BadRequest, NotFound }
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.client.Client
import org.scalacheck.Gen
import org.scalacheck.effect.PropF.forAllF
import org.typelevel.log4cats.SelfAwareStructuredLogger
import org.typelevel.log4cats.noop.NoOpLogger
import cats.effect.*
import com.green.screen.banking.domain.openAPI.AccountAccessConsentsResponse
import cats.syntax.all.*

class AccountAccessConsentClientSuite extends CatsEffectSuite with ScalaCheckEffectSuite {

  given SelfAwareStructuredLogger[IO] = NoOpLogger[IO]

  test("Should make the correct GET request") {
    forAllF(bankPrefixGen, consentIdGen, accountAccessConsentsResponseGen) {
      case (bankPrefix, consentId, accountAccessConsentsResponse: AccountAccessConsentsResponse) =>
        var uri: Option[Uri]             = None
        var mediaType: Option[MediaType] = None

        val client = Client[IO] { req =>
          uri = req.uri.some
          mediaType = req.contentType.map(_.mediaType)
          Resource.pure(
            Response(Status.Ok).withEntity(accountAccessConsentsResponse)
          )
        }

        val getAccountAccessConsentResponse =
          AccountAccessConsentClient.make[IO](client).getAccountAccessConsent(consentId, bankPrefix)
        val expectedAccountAccessConsentUrl = bankPrefix.value + "account-access-consents" + s"/$consentId"

        getAccountAccessConsentResponse.assertEquals(
          accountAccessConsentsResponse
        )
          >> IO.pure(assertEquals(uri.map(_.path.toString), expectedAccountAccessConsentUrl.some))
          >> IO.pure(
            assertEquals(
              mediaType,
              MediaType.application.json.some
            )
          )
    }
  }

  test("Should make the correct POST request") {
    forAllF(bankPrefixGen, consentIdGen, createAccountAccessConsentsRequestBodyGen, accountAccessConsentsResponseGen) {
      case (bankPrefix, consentId, createAccountAccessConsentsRequestBody, accountAccessConsentsResponse) =>
        var uri: Option[Uri]             = None
        var mediaType: Option[MediaType] = None

        val client = Client[IO] { req =>
          uri = req.uri.some
          mediaType = req.contentType.map(_.mediaType)

          Resource.pure(
            Response(Status.Ok).withEntity(accountAccessConsentsResponse)
          )
        }

        val setAccountAccessConsentResponse =
          AccountAccessConsentClient
            .make[IO](client)
            .setAccountAccessConsent(createAccountAccessConsentsRequestBody, bankPrefix)

        val expectedAccountAccessConsentUrl = bankPrefix.value + "account-access-consents"

        setAccountAccessConsentResponse.assertEquals(
          accountAccessConsentsResponse
        )
          >> IO.pure(assertEquals(uri.map(_.path.toString), expectedAccountAccessConsentUrl.some))
          >> IO.pure(
            assertEquals(
              mediaType,
              MediaType.application.json.some
            )
          )
    }
  }

  private val badResponsesGen = Gen.oneOf(List(NotFound, BadRequest))

  test("A POST request should raise an error if ASPSP returns 400 or 404 ") {
    forAllF(bankPrefixGen, createAccountAccessConsentsRequestBodyGen, badResponsesGen) {
      case (bankPrefix, createAccountAccessConsentsRequestBody, responseCodeFromASPSP) =>
        val client = Client[IO] { _ =>
          Resource.pure(
            Response(responseCodeFromASPSP)
          )
        }

        val setAccountAccessConsentResponse =
          AccountAccessConsentClient
            .make[IO](client)
            .setAccountAccessConsent(createAccountAccessConsentsRequestBody, bankPrefix)

        setAccountAccessConsentResponse.attempt
          .map(_.leftMap(_.isInstanceOf[AccountAccessConsentClientError]))
          .assertEquals(true.asLeft)
    }
  }

  test("A GET request should raise an error if ASPSP returns 400 or 404") {
    forAllF(bankPrefixGen, consentIdGen, createAccountAccessConsentsRequestBodyGen) {
      case (bankPrefix, consentId, createAccountAccessConsentsRequestBody) =>
        val client = Client[IO] { _ =>
          Resource.pure(
            Response(Status.BadRequest)
          )
        }

        val setAccountAccessConsentResponse =
          AccountAccessConsentClient.make[IO](client).getAccountAccessConsent(consentId, bankPrefix)

        setAccountAccessConsentResponse.attempt
          .map(_.leftMap(_.isInstanceOf[AccountAccessConsentClientError]))
          .assertEquals(true.asLeft)
    }
  }
}
