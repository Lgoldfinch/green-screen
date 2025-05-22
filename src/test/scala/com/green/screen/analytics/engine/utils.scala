package com.green.screen.analytics.engine

import cats.{ Id, Monad, MonadThrow }

object utils {
  given monadThrowId(using M: Monad[Id]): MonadThrow[Id] = new MonadThrow[Id] {
    override def flatMap[A, B](fa: Id[A])(f: A => Id[B]): Id[B] = M.flatMap(fa)(f)

    override def tailRecM[A, B](a: A)(f: A => Id[Either[A, B]]): Id[B] = M.tailRecM(a)(f)

    override def raiseError[A](e: Throwable): Id[A] = throw e
    

    override def handleErrorWith[A](fa: Id[A])(f: Throwable => Id[A]): Id[A] = f(
      new RuntimeException("Oops! Something went wrong")
    )

    override def pure[A](x: A): Id[A] = M.pure(x)
  }
}
