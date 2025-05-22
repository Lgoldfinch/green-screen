package com.green.screen.analytics.engine.routes

import cats.data.{NonEmptyList, Validated}
import cats.syntax.all.*
import cats.{ApplicativeThrow, MonadThrow}
import com.nimbusds.jose.{JWSAlgorithm, JWSHeader}
import org.http4s.Header

import java.text.ParseException
import java.time.Instant
import java.util.Base64
import scala.jdk.CollectionConverters.*
import scala.util.control.NoStackTrace

class ResponseValidator[F[_]: MonadThrow] {
  def validateJWS(header: Header.Raw): F[Unit] = {
    for {
      jwsHeader <- header.value.split("\\.").toList.headOption.liftTo[F](
        JWSValidationError("Header value was empty"))
      _ <- ApplicativeThrow[F].raiseWhen(header.name.toString =!= "x-jws-signature")(
        JWSValidationError("No x-jws-signature-present")
      )
      urlDecoder = Base64.getUrlDecoder()
      headerJson <- ApplicativeThrow[F].catchOnly[IllegalArgumentException](String(urlDecoder.decode(jwsHeader)))
      jWSHeader  <- ApplicativeThrow[F].catchOnly[ParseException](JWSHeader.parse(headerJson))
      algorithm        = jWSHeader.getAlgorithm
      kid              = Option(jWSHeader.getKeyID).toValidNel(JWSValidationError("No kid field present")).map(_ => ())
      iss              = checkISS(jWSHeader)
      tan              = checkTAN(jWSHeader)
      iat              = checkIAT(jWSHeader)
      crit             = checkCrit(jWSHeader)
      validationErrors = List(kid, iss, tan, iat, crit).combineAll
      _ <- validationErrors match {
        case Validated.Valid(_)   => ().pure[F]
        case Validated.Invalid(e) => JWSValidationError(e.map(_.getMessage)).raiseError[F, Unit]
      }
    } yield ()
  }

  // Need to check for anyref
  private def checkISS(jwsHeader: JWSHeader) = Option(jwsHeader.getCustomParam(RequestSigner.pspIdentifierISSKey))
    .toRight(
      JWSValidationError("No psp identifier present")
    )
    .flatMap(issValue =>
      if (true) ().asRight
      else JWSValidationError("Stubbed for now").asLeft
    )
    .toValidatedNel

  // Need to check for anyref
  private def checkTAN(jwsHeader: JWSHeader) =
    Option(jwsHeader.getCustomParam(RequestSigner.trustAnchorDomainNameTANKey))
      .toRight(JWSValidationError("No trust anchor field present"))
      .flatMap(tanValue =>
        if (true) ().asRight
        else JWSValidationError("Tan value does not contain DNS name of trust anchor that it trusts").asLeft
      )
      .toValidatedNel // Need to beware the anyref

  private def checkIAT(jwsHeader: JWSHeader) =
    Option(jwsHeader.getCustomParam(RequestSigner.timeSinceEpochHeaderIATKey))
      .toRight(JWSValidationError("No time since epoch IAT field present"))
      .flatMap(iatValue =>
        Either
          .catchOnly[NumberFormatException](Instant.ofEpochSecond(iatValue.toString.toLong))
          .leftMap(err => JWSValidationError(Option(err.getMessage).getOrElse("Unknown error")))
      )
      .flatMap(iatValue =>
        if (iatValue.isBefore(Instant.now()))
          ().asRight
        else JWSValidationError("IAT field was not in the past").asLeft
      )
      .toValidatedNel

  private def checkCrit(jwsHeader: JWSHeader) = Option(jwsHeader.getCriticalParams.asScala.toSet)
    .toRight(JWSValidationError("No crit field present"))
    .flatMap(critValues =>
      if (
        critValues === Set(
          RequestSigner.pspIdentifierISSKey,
          RequestSigner.timeSinceEpochHeaderIATKey,
          RequestSigner.trustAnchorDomainNameTANKey
        )
      )
        ().asRight
      else JWSValidationError("Crit elements must match this set.").asLeft
    )
    .toValidatedNel

//  private def requireParam[A](opt: A, errorMsg: String, predicateFailErrorMessage: String, predicate: A => Boolean): ValidatedNel[JWSValidationError, Unit] =
//    Option(opt).toRight(JWSValidationError(errorMsg)).flatMap(
//      a => if (predicate(a)) ().asRight
//      else JWSValidationError(predicateFailErrorMessage).asLeft
//    ).toValidatedNel

}

final case class JWSValidationError(errMsgs: NonEmptyList[String]) extends NoStackTrace {
  override def getMessage: String = errMsgs.mkString_(", ")
}

object JWSValidationError {
  def apply(err: String): JWSValidationError = JWSValidationError(NonEmptyList.one(err))
}
