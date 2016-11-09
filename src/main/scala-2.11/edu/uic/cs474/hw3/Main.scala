package edu.uic.cs474.hw3

import akka.actor.{ActorSystem, Props}
import com.scitools.understand.{Database, Understand}
import edu.uic.cs474.hw3.analysis.NVersionsFirstLast
import edu.uic.cs474.hw3.graphing.ReferenceGraphBuilder
import edu.uic.cs474.hw3.messages.Start
import edu.uic.cs474.hw3.undestand.DbManager
import edu.uic.cs474.hw3.versioning.CommitPolicy

/**
  * The entry point of the application
  */
object Main extends App {
  val system = ActorSystem("GithubAnalyzer")
  val master = system.actorOf(Props[Master])
  Config.maxProjectVersionManagers_=(3)
  Config.maxProjectVersionParsers_=(3)
  Config.maxNVersions_=(20)
  Config.analysisPolicy_=(NVersionsFirstLast)
  Config.versionPolicy_=(CommitPolicy)
  master ! Start(2, "tetris", "java")
}
