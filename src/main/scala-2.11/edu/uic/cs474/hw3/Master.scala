package edu.uic.cs474.hw3

import akka.actor._
import edu.uic.cs474.hw3.analysis.ResultHandler
import edu.uic.cs474.hw3.graphing.{ProjectVersionGrapher, ProjectVersionGrapherRouter}
import edu.uic.cs474.hw3.http.ProjectDownloader
import edu.uic.cs474.hw3.parsing.{ProjectVersionParser, ProjectVersionParserRouter}
import edu.uic.cs474.hw3.versioning.{ProjectVersionCheckout, ProjectVersionCheckoutRouter}
import messages._

/**
  * The Master is the top level actor in the application. It starts three children: a ProjectDownloader,
  * a ProjectVersionCheckoutRouter, ProjectVersionParserRouter, ProjectVersionGrapherRouter and a ResultHandler.
  * When the Master is started (through the Start message), it forwards the message
  * to the ProjectDownloader to initiate the download procedure. During the execution, the Master forwards messages
  * between its children. Furthermore, the Master monitors the state of its children and, in case of failure, restarts them.
  */
abstract class Master extends Actor {

  //Actor references to the children
  private var projectDownloader:ActorRef = _
  private var projectVersionCheckoutRouter:ActorRef = _
  private var projectVersionParserRouter:ActorRef = _
  private var projectVersionGrapherRouter:ActorRef = _
  private var resultHandler:ActorRef = _

  //Handles messages from other actors
  def receive = {

    //Start the Master
    case Start =>
      projectDownloader = context.actorOf(Props[ProjectDownloader])
      projectVersionCheckoutRouter = context.actorOf(Props[ProjectVersionCheckoutRouter])
      projectVersionParserRouter = context.actorOf(Props[ProjectVersionParserRouter])
      projectVersionGrapherRouter = context.actorOf(Props[ProjectVersionGrapherRouter])
      resultHandler = context.actorOf(Props[ResultHandler])

      projectDownloader ! Start

    //Stop the Master (recursively stops all children first)
    case Stop =>
      context stop self

    //Handles failures of children
    case Terminated(actor) =>
      actor match {
        case a:ProjectDownloader =>
          projectDownloader = context.actorOf(Props[ProjectDownloader])
        case a:ProjectVersionCheckout =>
          projectVersionCheckoutRouter = context.actorOf(Props[ProjectVersionCheckoutRouter])
        case a:ProjectVersionParser =>
          projectVersionParserRouter = context.actorOf(Props[ProjectVersionParserRouter])
        case a:ProjectVersionGrapher =>
          projectVersionGrapherRouter = context.actorOf(Props[ProjectVersionGrapherRouter])
        case a:ResultHandler =>
          resultHandler = context.actorOf(Props[ResultHandler])
      }

    //Forwards a Checkout message from the ProjectDownloader to the ProjectVersionCheckoutRouter
    case CheckoutVersion(repository, version, projectPath) =>
      projectVersionCheckoutRouter ! CheckoutVersion(repository, version, projectPath)

    //Forwards a Parse message to the ProjectVersionParserRouter
    case DoneCheckoutVersion(repository, version, versionPath) =>
      projectVersionParserRouter ! ParseVersion(repository, version, versionPath)

    //Forwards a Graph message to the ProjectVersionGrapherRouter
    case DoneParseVersion(repository, version, versionDbPath) =>
      projectVersionGrapherRouter ! GraphVersionDb(repository, version, versionDbPath)

    //Forwards a DoneAnalyzing message from the ProjectRouter to the ResultHandler
    case DoneAnalyzing(differences) =>
      resultHandler ! differences

  }

}
