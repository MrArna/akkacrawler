package edu.uic.cs474.hw3.messages

import edu.uic.cs474.hw3.analysis.Differences
import edu.uic.cs474.hw3.graphing.{EntityVertex, ReferenceEdge}
import org.jgrapht.DirectedGraph

//A trait representing messages
sealed trait Message

//Start the receiving actor
case object Start extends Message
//Stop the receiving actor
case object Stop extends Message

case class GetLastMaxNVersions(repository:String, projectPath:String, n:Int) extends Message
case class DoneGetLastMaxNVersions(repository:String, projectPath:String, nVersionList:List[String]) extends Message

//Tell a ProjectVersionCheckout to checkout a version
case class CheckoutVersion(repository:String, nVersionList:List[String], version:String, projectPath:String) extends Message
//Tell a ProjectCheckoutVersionRouter that the checkout of one version is complete
case class DoneCheckoutVersion(repository:String, nVersionList:List[String], version:String, versionPath: String) extends Message

//Tell a ProjectVersionParser to parse a version
case class ParseVersion(repository:String, nVersionList:List[String], version:String, versionPath:String) extends Message
//Tell a ProjectVersionCheckoutRouter that the checkout of one version is complete
case class DoneParseVersion(repository:String, nVersionList:List[String], version:String, versionDbPath: String) extends Message

//Tell a ProjectVersionGrapher to graph a version db
case class GraphVersionDb(repository:String, nVersionList:List[String], version:String, versionDbPath:String) extends Message
//Tell a ProjectVersionGrapherRouter that the graph of a version db is done
case class DoneGraphVersionDb(repository:String,
                              nVersionList:List[String],
                              version:String,
                              versionDbGraph: DirectedGraph[EntityVertex, ReferenceEdge]) extends Message

//Tell a ProjectAnalyzer to analyze the project's graphs
case class Analyze(repository: String,
                   nVersionList:List[String],
                   version: String,
                   graph:DirectedGraph[EntityVertex, ReferenceEdge]) extends Message

//Tell the ResultHandler that the analysis is complete
case class DoneAnalyzing(differences:Differences) extends Message

