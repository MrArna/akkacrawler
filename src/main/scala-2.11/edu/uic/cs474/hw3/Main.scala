package edu.uic.cs474.hw3

import akka.actor.{ActorSystem, Props}

/**
  * The entry point of the application
  */
object Main extends App {

  println("Hello World")

  val system = ActorSystem("GithubAnalyzer")
  val master = system.actorOf(Props[Master])
}
