package com.environ.mental

import cats.effect.{IO, IOApp}

object Main extends IOApp.Simple:
  val run = VironmentalServer.run[IO]
