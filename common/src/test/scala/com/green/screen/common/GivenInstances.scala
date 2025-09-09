package com.green.screen.common

import cats.effect.kernel.{ CancelScope, MonadCancelThrow, Poll }
import cats.{ Id, Monad, MonadThrow }
import com.green.screen.common.effects.GenUUID

import java.util.UUID

object GivenInstances {
  given (using m: Monad[Id]): MonadThrow[Id] = {
    new MonadThrow[Id] {
      override def pure[A](a: A): Id[A] = m.pure(a)

      override def flatMap[A, B](fa: Id[A])(f: A => Id[B]): Id[B] =
        m.flatMap(fa)(f)

      override def handleErrorWith[A](fa: Id[A])(f: Throwable => Id[A]): Id[A] =
        try fa
        catch case e: Exception => f(e)

      override def raiseError[A](e: Throwable): Id[A] =
        throw e

      override def tailRecM[A, B](a: A)(f: A => Id[Either[A, B]]): Id[B] = {
        ???
      }
    }
  }

  given GenUUID[Id] with
    override def make: Id[UUID] = UUID.randomUUID()

    override def read(str: String): Id[UUID] = UUID.fromString(str)
}
