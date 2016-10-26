package edu.uic.cs474.hw3

import akka.actor._
import edu.uic.cs474.hw3.analysis.ResultHandler
import edu.uic.cs474.hw3.http.ProjectDownloader
import edu.uic.cs474.hw3.parsing.ProjectRouter
import messages._

/**
  * The Master is the top level actor in the application. It starts three children: a ProjectDownloader,
  * a ProjectRouter and a ResultHandler. When the Master is started (through the Start message), it forwards the message
  * to the ProjectDownloader to initiate the download procedure. During the execution, the Master forwards messages
  * between its children. Furthermore, the Master monitors the state of its children and, in case of failure, restarts them.
  */
class Master extends Actor {

  //Actor references to the children
  private var projectDownloader:ActorRef = _
  private var projectRouter:ActorRef = _
  private var resultHandler:ActorRef = _

  //Handles messages from other actors
  def receive = {

    //Start the Master
    case Start =>
      projectDownloader = context.actorOf(Props[ProjectDownloader])
      projectRouter = context.actorOf(Props[ProjectRouter])
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
        case a:ProjectRouter =>
          projectRouter = context.actorOf(Props[ProjectRouter])
        case a:ResultHandler =>
          resultHandler = context.actorOf(Props[ResultHandler])
      }
    //Forwards a Parse message from the ProjectDownloader to the ProjectRouter
    case Parse(repository,path) =>
      projectRouter ! Parse(repository,path)
    //Forwards a DoneAnalyzing message from the ProjectRouter to the ResultHandler
    case DoneAnalyzing(differences) =>
      resultHandler ! differences
  }
}
