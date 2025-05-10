package com.green.screen.analytics.engine.routes

import cats.*
import com.green.screen.analytics.engine.generators.openBanking.openApiBankingConfig
import com.green.screen.analytics.engine.routes.AuthHeaders.*
import com.green.screen.analytics.engine.utils.monadThrowId
import com.nimbusds.jose.{JWSAlgorithm, JWSHeader}
import munit.{CatsEffectSuite, ScalaCheckEffectSuite}
import org.scalacheck.Gen
import org.scalacheck.Prop.forAll
import scala.jdk.CollectionConverters.*
import java.util.Base64

class RequestSignerSuite extends CatsEffectSuite with ScalaCheckEffectSuite {
  private val testPrivateKey =
    """
      |-----BEGIN PRIVATE KEY-----
      |MIIEuwIBADANBgkqhkiG9w0BAQEFAASCBKUwggShAgEAAoIBAQCC+fpPEqfkdifT
      |Jh9/x6XCm4hmyb3tRS7dI/eKl1fBqi9ZSqx2atN6TXJZimFh7m3zA/zd5NbRkrx3
      |i0LuHO/jHKtqg9crEEx77NEMhpojGP0uN/d8bAp0FpMZbf2EKGoWx/Lko1LHLtPk
      |GOt7RIQTRRGSSXiW4TdrCSonLEehiBKZLNmWzvJqTYLEbagI57Wz8cVUWGZySW+I
      |jd+v8DJtnj7HMwA/2DYTF3RAMwYwDAP904Q49ZCJBdva7H93eIMS0lqFIwmeS0sU
      |fjL0FrQeQ73tpxFcypFjN3QhPYEQWGxIxlNPHgTrH78RULktz+lo+Jn/pg+EVZZm
      |S+mYcXUxAgMBAAECggEATNc9rABk8ZsEWFNobX4C2L6I8szvGXHaQbElPHD38k3X
      |A4rUzZB0y67+JFwUL/FDHiy7wK7N/RwaaAQS20Gs11BtnMsGhGRGivnv12psqaSy
      |fNgm4R89rsfDu0qVTmCrhPv/m3XaTbNCavLXzHTxC6Ec4vcpwVMWQTyaN2s6ByC8
      |i47pAb2WxPuRNBB0pSiuvmlXbR8tcCQ3ZUmRfs6U3p2RCJDynZ/iSkZmcgzQTrDz
      |9cZPFT4yEmr3BOy/5Ve45Y0cTodHFW/wXynjFu2ctLYR0yNtwPoQVvDuAMClmG0R
      |CY9xdAkVr577f4j4MBWNDqT+ytBwz74WWV1PoAT2tQKBgQD9TeV+G5qPc4csBBnq
      |DLyKfBPpdK8OZJ7giRasnpzFbBlS9ER1CcI/vT0mV3UhWbT9q3bO1CzZWY3ugF/n
      |hBlq6vB+BCqpQca5sstz31pZlbYSkE3w3cIVDq9EBvFOqU46rKOVyK7v4Oxm8MJG
      |ly53OmvsOKk+D1PzMFbtQMX16wKBgQCEXs+PTdvIbIyEoOcfZz4+wK9QDIv/Z4VD
      |OX3n3HPtLlo5hGs1zZiF25qpJlP//hnOHYH+yijUsLrdkWMuUXQ2ZTegTZJou4y9
      |HlRCNU88PkUJ2yOn6Esa7zgvBrYt6GI5m9292te15YQpCzbBM3jlliuX1wMh8/+U
      |Xd/+GReuUwKBgQCyJfLdenR9p3bN0iSXo1WG4DTw4AuuR74llMTJqtP/VUOKQBlV
      |ZwOi6Gj1alUKm5jbP5hTZo4UXxOfRdItdY6az5lMR4npeEVXnRPR7qgQkWZ4L91h
      |JdW2EZnacjeMZy6JvZlfYsEGHF6nF3TLSNB5MjFs9XyyFP2p77lzKzcO+QKBgDVc
      |Twmy1j2WXJL+lg0XvoTnggSf+jOsVJFD9NhqCyV6wQ5cridTSP/1BgTYGZahpV9s
      |j8HZVtpeoAW/nR7H4TQoW2M4wiOML3Dxb//8o4NiiswtVdNTFiu2cYyrhPc+rZIR
      |njLxrHF8I2m0Ro9do4fRoKMmHvIp/UafDTPOg7hXAn82wfxtJ1+crguj8PNTt8Cd
      |HP4LwzJ2SoFTXShWzZzMMVCXrM1Cdt2SYlEI9ZFzlya/CGqQBnJve/W/FgpYzeYC
      |9uaDvN5e9boNaEPmvrk+coarUSwV985rZmVUWWR7c/yIGkbp3Tqf36b5prZSTD+k
      |lquzdZpJVzQfql64KHjG
      |-----END PRIVATE KEY-----
      |
      |""".stripMargin

  test("RequestSigner should return the expected JWT") {
    forAll(openApiBankingConfig, Gen.alphaStr) { case (config, payload) =>
      val requestSigner = RequestSigner[Id](config)

      val jws = requestSigner.createDetachedJWS(payload, testPrivateKey)
      val jwsHeader = jws.value.split("\\.")(0)

      val urlDecoder = Base64.getUrlDecoder()
      val headerJson = String(urlDecoder.decode(jwsHeader))

      val jWSHeader: JWSHeader = JWSHeader.parse(headerJson)

      val algorithm: JWSAlgorithm = jWSHeader.getAlgorithm
      val kid = jWSHeader.getKeyID

      val iss = jWSHeader.getCustomParam(RequestSigner.pspIdentifierISSKey)
      val tan = jWSHeader.getCustomParam(RequestSigner.trustAnchorDomainNameTANKey)
      val crit = jWSHeader.getCriticalParams.asScala.toSet
      val expectedCritHeader = Set(RequestSigner.timeSinceEpochHeaderIATKey, RequestSigner.pspIdentifierISSKey, RequestSigner.trustAnchorDomainNameTANKey)

      assertEquals(algorithm, JWSAlgorithm.PS256)
      assertEquals(kid, "tbd")
      assertEquals(iss, config.orgId + "/" + config.softwareStatementId)
      assertEquals(tan, config.trustAnchorId)
      assertEquals(crit, expectedCritHeader)
    }
  }
}
