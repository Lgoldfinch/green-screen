package com.green.screen.analytics.engine.routes

import cats.syntax.all.*
import cats.{ApplicativeThrow, MonadThrow}
import com.green.screen.analytics.engine.config.OpenApiBankingConfig
import com.nimbusds.jose.crypto.RSASSASigner
import com.nimbusds.jose.{JWSAlgorithm, JWSHeader, JWSObject, Payload}

import java.security.spec.PKCS8EncodedKeySpec
import java.security.{KeyFactory, PrivateKey}
import java.time.Instant
import java.util
import scala.jdk.CollectionConverters.*
import AuthHeaders.JWSToken

import java.util.Base64

class RequestSigner[F[_]: MonadThrow](config: OpenApiBankingConfig) {

  // See https://openbankinguk.github.io/read-write-api-site3/v4.0/profiles/read-write-data-api-profile.html for details.
  def createDetachedJWS(payload: String, privateKey: String): F[JWSToken] = {
    val timeSinceEpochHeaderIATKey  = "http://openbanking.org.uk/iat"
    val pspIdentifierISSKey         = "http://openbanking.org.uk/iss"
    val trustAnchorDomainNameTANKey = "http://openbanking.org.uk/tan"

    val openAPIBankingParameters: util.Map[String, Object] =
      Map(
        timeSinceEpochHeaderIATKey -> Instant.EPOCH.toString,
        pspIdentifierISSKey -> config.makeISS, // Identify PSP, If issuer is using certificate -> needs to match subject, of signing certificate.
        trustAnchorDomainNameTANKey -> config.trustAnchorId,
      ).asJava

    val kid =
      "tbd" // Must match a value that can be used to look up the key in the key store hosted by the trust anchor.

    for {
      jwsHeader <- ApplicativeThrow[F].catchNonFatal(
        new JWSHeader.Builder(JWSAlgorithm.PS256).keyID(kid)
          .customParams(openAPIBankingParameters)
          .criticalParams(
            Set(
              timeSinceEpochHeaderIATKey,
              pspIdentifierISSKey,
              trustAnchorDomainNameTANKey).asJava
          )
          .build()
      )
      payload   <- ApplicativeThrow[F].catchNonFatal(new Payload(payload))
      jwsObject <- ApplicativeThrow[F].catchNonFatal(JWSObject(jwsHeader, payload))
      keySpec   <- ApplicativeThrow[F].catchNonFatal(decodePrivateKey(privateKey))
      signer = new RSASSASigner(keySpec)
      _      <- ApplicativeThrow[F].catchNonFatal(jwsObject.sign(signer))
      jwsStr <- ApplicativeThrow[F].catchNonFatal(jwsObject.serialize(true))
    } yield JWSToken(jwsStr)
  }

  private def decodePrivateKey(privateKey: String): PrivateKey = {
    val yes = privateKey.replace("-----BEGIN PRIVATE KEY-----", "")
      .replace("-----END PRIVATE KEY-----", "").replaceAll("\\s", "")

    val decodedPrivateKey = Base64.getDecoder().decode(yes)
    val keySpec           = new PKCS8EncodedKeySpec(decodedPrivateKey)
    val keyFactory        = KeyFactory.getInstance("RSA")

    keyFactory.generatePrivate(keySpec)
  }
}

private object RequestSigner {
  val timeSinceEpochHeaderIATKey = "http://openbanking.org.uk/iat"
  val pspIdentifierISSKey = "http://openbanking.org.uk/iss"
  val trustAnchorDomainNameTANKey = "http://openbanking.org.uk/tan"
}

object AuthHeaders {
  opaque type JWSToken = String

  object JWSToken {
    def apply(value: String): JWSToken = value

    extension (str: JWSToken) def value: String = str
  }
}
