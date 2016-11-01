package edu.uic.cs474.hw3

import akka.actor.{ActorSystem, Props}
import com.scitools.understand.{Database, Understand}
import edu.uic.cs474.hw3.graphing.ReferenceGraphBuilder
import edu.uic.cs474.hw3.messages.Start
import edu.uic.cs474.hw3.undestand.DbManager

/**
  * The entry point of the application
  */
object Main extends App {
  val system = ActorSystem("GithubAnalyzer")
  val master = system.actorOf(Props[Master])
  Config.maxProjectVersionManagers_=(2)
  Config.maxProjectVersionParsers_=(2)
  Config.maxProjectVersionGraphers_=(1)
  master ! Start

}
