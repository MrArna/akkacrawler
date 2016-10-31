package edu.uic.cs474.hw3.graphing

import akka.actor.{Actor, Props, Terminated}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import edu.uic.cs474.hw3.Config
import edu.uic.cs474.hw3.messages._

/**
  * This actor creates a pool of ProjectVersionGrapher (whose size is specified in the global config) and routes versions
  * to checkout to each actor in the pool. The routing policy is round robin.
  */
class ProjectVersionGrapherRouter extends Actor {

  //Create the ProjectGrapher in the pool
  private val routees = for(i <- 1 to Config.maxProjectVersionGraphers) yield {
    val handler = context.actorOf(Props[ProjectVersionGrapher])
    context watch handler
    ActorRefRoutee(handler)
  }

  //Create the round robin router
  private var router = Router(RoundRobinRoutingLogic(), routees)

  //Handle received messages
  def receive = {
    //Forward GraphVersionDb to a free ProjectVersionGrapher
    case version:GraphVersionDb =>
      router.route(version, sender)

    //Forward DoneGraphVersionDb to the Master
    case done:DoneGraphVersionDb =>
      context.parent ! done

    //Handle unexpected termination of a ProjectVersionGrapher by restarting another one
    case Terminated(handler) =>
      router = router.removeRoutee(handler)
      val newHandler = context.actorOf(Props[ProjectVersionGrapher])
      context watch newHandler
      router = router.addRoutee(newHandler)
  }
}
