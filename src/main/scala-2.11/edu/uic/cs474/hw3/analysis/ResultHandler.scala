package edu.uic.cs474.hw3.analysis

import akka.actor.Actor
import akka.actor.Actor.Receive
import edu.uic.cs474.hw3.messages.{DoneAnalyzing, Stop}

/**
  * An actor to handle analysis results from the ProjectAnalyzers. This actor receives a DoneAnalyzing message, when
  * an analysis is terminated, together with a set of differences, and shows them to the user.
  */
class ResultHandler extends Actor {

  def receive = {

    case DoneAnalyzing(differences) =>

      println("#############################################################################################")
      println(differences.repositoryName)
      println("#############################################################################################")
      println("Version 1 : "+differences.v1)
      println("Version 2 : "+differences.v2)
      println("Number of methods to be re-tested : "+differences.getDifferences().size)
      println("---------------------------------------------------------------------------------------------")
      println("Methods to be re-tested")
      println("---------------------------------------------------------------------------------------------")
      differences.getDifferences().foreach(diff => {
        println(diff)
      })
      println("#############################################################################################\n")
  }
}
