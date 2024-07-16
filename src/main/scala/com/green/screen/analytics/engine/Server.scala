//package com.environ.mental
//
//import cats.effect.Async
//import cats.syntax.all._
//import com.comcast.ip4s._
//import fs2.io.net.Network
//import org.http4s.ember.client.EmberClientBuilder
//import org.http4s.ember.server.EmberServerBuilder
//import org.http4s.implicits._
//import org.http4s.server.middleware.Logger
//import com.environ.mental.analytics.engine.AnalyticsEngineRoutes
//object Server {
//
//  def run[F[_]: Async: Network]: F[Nothing] = {
//
//    val httpApp = AnalyticsEngineRoutes.analyticsRoutes("hello")
//
//   val finalHttpApp = Logger.httpApp(true, true)(httpApp)
//
//    for {
////      helloWorldAlg = HelloWorld.impl[F]
////      jokeAlg = Jokes.impl[F](client)
//      // Combine Service Routes into an HttpApp.
//      // Can also be done via a Router if you
//      // want to extract segments not checked
//      // in the underlying routes.
////      httpApp = (
////      ).orNotFound
//
//        // With Middlewares in place
//
//      _ <-
//        EmberServerBuilder.default[F]
//          .withHost(ipv4"0.0.0.0")
//          .withPort(port"8080")
//          .withHttpApp(finalHttpApp)
//          .build
//    } yield ()
//  }.useForever
//}