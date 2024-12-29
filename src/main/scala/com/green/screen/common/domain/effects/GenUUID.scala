package com.green.screen.common.domain.effects

import cats.ApplicativeThrow
import cats.effect.Sync
import fs2.Stream

import java.util.UUID

trait GenUUID[F[_]] {
  def make: F[UUID]
  def read(str: String): F[UUID]
}

object GenUUID {
  def apply[F[_]: GenUUID]: GenUUID[F] = implicitly

  implicit def forSync[F[_]: Sync]: GenUUID[F] =
    new GenUUID[F] {
      def make: F[UUID] = Sync[F].delay(UUID.randomUUID())

      def read(str: String): F[UUID] =
        ApplicativeThrow[F].catchNonFatal(UUID.fromString(str))
    }

  implicit def forStream[F[_]](implicit F: GenUUID[F]): GenUUID[Stream[F, *]] =
    new GenUUID[Stream[F, *]] {
      override def make: Stream[F, UUID] = Stream.eval(F.make)

      override def read(str: String): Stream[F, UUID] = Stream.eval(F.read(str))
    }
}
