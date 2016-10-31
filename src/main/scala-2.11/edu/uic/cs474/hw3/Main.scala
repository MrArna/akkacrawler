package edu.uic.cs474.hw3

import akka.actor.{ActorSystem, Props}
import edu.uic.cs474.hw3.messages.Start

/**
  * The entry point of the application
  */
object Main extends App {

  println("Hello World")

  val system = ActorSystem("GithubAnalyzer")
  val master = system.actorOf(Props[Master])
  Config.maxProjectVersionManagers_=(2)
  Config.maxProjectVersionParsers_=(2)
  Config.maxProjectVersionGraphers_=(2)
  master ! Start
}
