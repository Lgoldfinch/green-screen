//package com.environ.mental

//import cats.effect.kernel.Async
//import org.http4s.client.Client

//sealed abstract class AppResources[F[_]] {
//  val client: Client[F]
//}
//
//object AppResources {
//
//  def make[F[_]: Async](): Resource[F, AppResources[F]] = {
//
//  }
//}
