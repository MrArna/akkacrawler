package edu.uic.cs474.hw3.parsing

import akka.actor.{Actor, Props, Terminated}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import edu.uic.cs474.hw3.Config
import edu.uic.cs474.hw3.messages._

/**
  * This actor creates a pool of ProjectHandlers (whose size is specified in the global config) and routes projects
  * to analyze to each actor in the pool. The routing policy is round robin.
  */
class ProjectRouter extends Actor {

  //Create the ProjectHandlers in the pool
  private val routees = for(i <- 1 to Config.maxProjectHandlers) yield {

    val handler = context.actorOf(Props[ProjectHandler])
    context watch handler
    ActorRefRoutee(handler)
  }

  //Create the round robin router
  private var router = Router(RoundRobinRoutingLogic(), routees)

  //Handle received messages
  def receive = {
    //Forward Parse to a free ProjectHandler
    case parse:Parse =>
      router.route(parse, sender)
    //Forward DoneAnalyzing to the Master
    case done:DoneAnalyzing =>
      context.parent ! done
    //Handle unexpected termination of a ProjectHandler by restarting another one
    case Terminated(handler) =>
      router = router.removeRoutee(handler)
      val newHandler = context.actorOf(Props[ProjectHandler])
      context watch newHandler
      router = router.addRoutee(newHandler)
  }
}
