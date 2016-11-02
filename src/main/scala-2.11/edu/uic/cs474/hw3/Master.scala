package edu.uic.cs474.hw3

import akka.actor._
import edu.uic.cs474.hw3.analysis.{ProjectAnalyzer, ResultHandler}
import edu.uic.cs474.hw3.graphing.{ProjectVersionGrapher, ProjectVersionGrapherRouter}
import edu.uic.cs474.hw3.http.ProjectDownloader
import edu.uic.cs474.hw3.parsing.{ProjectVersionParser, ProjectVersionParserRouter}
import edu.uic.cs474.hw3.versioning.{ProjectVersionManager, ProjectVersionManagerRouter}
import messages._

/**
  * The Master is the top level actor in the application. It starts three children: a ProjectDownloader,
  * a ProjectVersionManagerRouter, ProjectVersionParserRouter, ProjectVersionGrapherRouter and a ResultHandler.
  * When the Master is started (through the Start message), it forwards the message
  * to the ProjectDownloader to initiate the download procedure. During the execution, the Master forwards messages
  * between its children. Furthermore, the Master monitors the state of its children and, in case of failure, restarts them.
  */
class Master extends Actor {

  //Actor references to the children
  private var projectDownloader:ActorRef = _
  private var ProjectVersionManagerRouter:ActorRef = _
  private var projectVersionParserRouter:ActorRef = _
  private var projectVersionGrapherRouter:ActorRef = _
  private var projectAnalyzer:ActorRef = _
  private var resultHandler:ActorRef = _

  //Handles messages from other actors
  def receive = {

    //Start the Master
    case Start =>
      projectDownloader = context.actorOf(Props[ProjectDownloader])
      ProjectVersionManagerRouter = context.actorOf(Props[ProjectVersionManagerRouter])
      projectVersionParserRouter = context.actorOf(Props[ProjectVersionParserRouter])
      projectVersionGrapherRouter = context.actorOf(Props[ProjectVersionGrapher].withDispatcher("blocking-io-dispatcher"))
      projectAnalyzer = context.actorOf(Props[ProjectAnalyzer])
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
        case a:ProjectVersionManager =>
          ProjectVersionManagerRouter = context.actorOf(Props[ProjectVersionManagerRouter])
        case a:ProjectVersionParser =>
          projectVersionParserRouter = context.actorOf(Props[ProjectVersionParserRouter])
        case a:ProjectVersionGrapher =>
          projectVersionGrapherRouter = context.actorOf(Props[ProjectVersionGrapher].withDispatcher("blocking-io-dispatcher"))
        case a:ProjectAnalyzer =>
          projectAnalyzer = context.actorOf(Props[ProjectAnalyzer])
        case a:ResultHandler =>
          resultHandler = context.actorOf(Props[ResultHandler])
      }

    //Forwards a Checkout message from the ProjectDownloader to the ProjectVersionManagerRouter
    case GetLastMaxNVersions(repository, projectPath, n) =>
      println("Master received get last max n versions")
      ProjectVersionManagerRouter ! GetLastMaxNVersions(repository, projectPath, n)

    //Forwards a Checkout message to the ProjectVersionManagerRouter
    case DoneGetLastMaxNVersions(repository, projectPath, nVersionList) =>
      println("Master received done get last max n versions")
      nVersionList.foreach(version => ProjectVersionManagerRouter ! CheckoutVersion(repository, nVersionList, version, projectPath))

    //Forwards a Parse message to the ProjectVersionParserRouter
    case DoneCheckoutVersion(repository, nVersionList, version, versionPath) =>
      projectVersionParserRouter ! ParseVersion(repository, nVersionList, version, versionPath)

    //Forwards a Graph message to the ProjectVersionGrapherRouter
    case DoneParseVersion(repository, nVersionList, version, versionDbPath) =>
      projectVersionGrapherRouter ! GraphVersionDb(repository, nVersionList, version, versionDbPath)

    //Forwards a Graph message to the ProjectVersionGrapherRouter
    case DoneGraphVersionDb(repository, nVersionList, version, versionDbGraph) =>
      projectAnalyzer ! Analyze(repository, nVersionList, version, versionDbGraph)

    //Forwards a DoneAnalyzing message from the ProjectRouter to the ResultHandler
    case DoneAnalyzing(differences) =>
      print("Start result handler")
      resultHandler ! DoneAnalyzing(differences)
  }

}
