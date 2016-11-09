package edu.uic.cs474.hw3.versioning

import akka.actor.{Actor, Props, Terminated}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import edu.uic.cs474.hw3.Config
import edu.uic.cs474.hw3.messages._

/**
  * This actor creates a pool of ProjectVersionManager (whose size is specified in the global config) and routes versions
  * to checkout to each actor in the pool. The routing policy is round robin.
  */
class ProjectVersionManagerRouter extends Actor {

  //Create the ProjectVersionManager in the pool
  private val routees = for(i <- 1 to Config.maxProjectVersionManagers) yield {
    val handler = context.actorOf(Props[ProjectVersionManager])
    context watch handler
    ActorRefRoutee(handler)
  }

  //Create the round robin router
  private var router = Router(RoundRobinRoutingLogic(), routees)

  //Handle received messages
  def receive = {
    //Forward GetLastMaxNVersions to a free ProjectVersionManager
    case nVersions:GetLastMaxNVersions => {
      println("VersionManagerRouter received get last max n versions")
      router.route(nVersions, sender)
    }

    //Forward DoneGetLastMaxNVersions to the Master
    case done:DoneGetLastMaxNVersions => {
      println("VersionManagerRouter received done get last max n versions")
      context.parent ! done
    }

    //Forward CheckoutVersion to a free ProjectVersionManager
    case version:CheckoutVersion =>
      router.route(version, sender)

    //Forward DoneCheckoutVersion to the Master
    case done:DoneCheckoutVersion =>
      context.parent ! done

    //Handle unexpected termination of a ProjectVersionManager by restarting another one
    case Terminated(handler) =>
      router = router.removeRoutee(handler)
      val newHandler = context.actorOf(Props[ProjectVersionManager])
      context watch newHandler
      router = router.addRoutee(newHandler)
  }
}
