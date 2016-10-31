package edu.uic.cs474.hw3.parsing

import akka.actor.{Actor, Props, Terminated}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import edu.uic.cs474.hw3.Config
import edu.uic.cs474.hw3.messages._

/**
  * This actor creates a pool of ProjectVersionParser (whose size is specified in the global config) and routes versions
  * to checkout to each actor in the pool. The routing policy is round robin.
  */
class ProjectVersionParserRouter extends Actor {

  //Create the ProjectVersionParser in the pool
  private val routees = for(i <- 1 to Config.maxProjectVersionParsers) yield {
    val handler = context.actorOf(Props[ProjectVersionParser])
    context watch handler
    ActorRefRoutee(handler)
  }

  //Create the round robin router
  private var router = Router(RoundRobinRoutingLogic(), routees)

  //Handle received messages
  def receive = {
    //Forward ParseVersion to a free ProjectVersionCheckout
    case version:ParseVersion =>
      router.route(version, sender)

    //Forward DoneParseVersion to the Master
    case done:DoneParseVersion =>
      context.parent ! done

    //Handle unexpected termination of a ProjectVersionCheckout by restarting another one
    case Terminated(handler) =>
      router = router.removeRoutee(handler)
      val newHandler = context.actorOf(Props[ProjectVersionParser])
      context watch newHandler
      router = router.addRoutee(newHandler)
  }
}
