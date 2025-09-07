package com.green.screen.common

import cats.{Id, Monad}
import cats.effect.kernel.{CancelScope, MonadCancelThrow, Poll}

object CatsInstances {

    given mct(using m: Monad[Id]): MonadCancelThrow[Id] = {
      new MonadCancelThrow[Id] {
        override def pure[A](a: A): Id[A] = m.pure(a)

        override def flatMap[A, B](fa: Id[A])(f: A => Id[B]): Id[B] =
          m.flatMap(fa)(f)

        override def handleErrorWith[A](fa: Id[A])(f: Throwable => Id[A]): Id[A] =
          try
            fa
          catch
            case e: Exception => f(e)
          

        override def raiseError[A](e: Throwable): Id[A] =
          throw e

        override def tailRecM[A, B](a: A)(f: A => Id[Either[A, B]]): Id[B] = {
          ???
        }

        override def uncancelable[A](body: Poll[Id] => Id[A]): Id[A] =
          ???

        override def forceR[A, B](fa: Id[A])(fb: Id[B]): Id[B] =
          ???

        override def onCancel[A](fa: Id[A], fin: Id[Unit]): Id[A] =
          // No cancellation logic in this toy effect, so just run `fa`
          ???

        override def rootCancelScope: CancelScope = ???

        override def canceled: Id[Unit] = ???
      }
    }
}
