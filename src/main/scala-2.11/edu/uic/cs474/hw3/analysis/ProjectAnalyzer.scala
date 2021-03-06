package edu.uic.cs474.hw3.analysis

import akka.actor.Actor
import edu.uic.cs474.hw3.Config
import edu.uic.cs474.hw3.graphing._
import edu.uic.cs474.hw3.messages.{Analyze, DoneAnalyzing}
import edu.uic.cs474.hw3.undestand.ReferenceKind
import org.jgrapht.DirectedGraph
import org.jgrapht.graph.SimpleDirectedGraph

import scala.collection.immutable.HashSet
import collection.JavaConverters._
import scala.collection.mutable.ListBuffer

/**
  * An actor to analyze the differences between several versions of the same project. This actor receives an Analyze message
  * with the graphs of N different project versions and compares them to find functions to be retested. It then forwards a
  * DoneAnalyzing message with the analysis results to the ResultHandler.
  */
class ProjectAnalyzer extends Actor {

  //Analyze the first and last of N versions
  private def NVersionsFirstLastAnalysis(repository: String, nVersionList: List[String], versionGraphList: List[(String, DirectedGraph[EntityVertex, ReferenceEdge])]): Unit = {
    versionGraphList
      .sliding(2) //there are only two by preconditions, but this way I get a list which is easy to index
      .map(listOfTwo => analyze(repository, listOfTwo(0)._1, listOfTwo(1)._1, listOfTwo(0)._2, listOfTwo(1)._2))
      .foreach(differences => sender ! DoneAnalyzing(differences))
  }

  //Analyze N versions two by two
  private def NVersionsTwoByTwoAnalysis(repository: String, nVersionList: List[String], versionGraphList: List[(String, DirectedGraph[EntityVertex, ReferenceEdge])]): Unit = {
    versionGraphList
      .sortWith((firstTuple, secondTuple) => nVersionList.indexOf(firstTuple._1) < nVersionList.indexOf(secondTuple._1))
      .sliding(2)
      .map(listOfTwo => analyze(repository, listOfTwo(0)._1, listOfTwo(1)._1, listOfTwo(0)._2, listOfTwo(1)._2))
      .foreach(differences => sender ! DoneAnalyzing(differences))
  }

  def receive = {
    case Analyze(repository, nVersionList, versionGraphList) =>
      Config.analysisPolicy match {
        case NVersionsFirstLast => NVersionsFirstLastAnalysis(repository, nVersionList, versionGraphList)
        case NVersionsTwoByTwo => NVersionsTwoByTwoAnalysis(repository, nVersionList, versionGraphList)
      }
  }


  //Analyze two different graphs
  private def analyze(repository:String,version1:String,version2:String,graph1:DirectedGraph[EntityVertex, ReferenceEdge],graph2:DirectedGraph[EntityVertex, ReferenceEdge]) : Differences = {

    //Collection to keep track of the functions to retest
    val diffs = new Differences(repository,version1,version2)

    //Edges introduced in the newer version
    val newEdges = getDifferentEdges(graph1,graph2)
    //Edges removed from the older version
    val oldEdges = getDifferentEdges(graph2,graph1)

    //Analyze the new edges
    newEdges.foreach(edge => {

      edge match {
        //if we have a new method definition, either we have a new class defining the method or an old class defining it
        case DefineMethodEdge(source,destination) =>
          if(!graph1.vertexSet().contains(source)) {
            diffs.add(edge.destination.longName,"Method defined in new class "+source.longName)
          } else if (!graph1.vertexSet().contains(destination)) {
            diffs.add(edge.destination.longName,"New method "+destination.longName)
          }
        //New method invocation
        case CallEdge(source,destination) =>
          if(source.isInstanceOf[MethodVertex])
            diffs.add(source.longName,"New invocation of "+destination.longName)
        //New class field usage
        case UseFieldVariableEdge(source,destination) =>
          if(source.isInstanceOf[MethodVertex])
            diffs.add(source.longName,"New usage of class field "+destination.longName)
        //New field usage
        case UseLocalVariableEdge(source,destination) =>
          if(source.isInstanceOf[MethodVertex])
            diffs.add(source.longName,"New usage of local field "+destination.longName)
        case _ =>
      }
    })

    //Analyze the old edges
    oldEdges.foreach(edge => {

      edge match {
        //Removed method invocation
        case CallEdge(source,destination) =>
          if(source.isInstanceOf[MethodVertex])
            diffs.add(source.longName,"Removed invocation of "+destination.longName)
        //Removed class field usage
        case UseFieldVariableEdge(source,destination) =>
          if(source.isInstanceOf[MethodVertex])
            diffs.add(source.longName,"Removed usage of class field "+destination.longName)
        //Removed field usage
        case UseLocalVariableEdge(source,destination) =>
          if(source.isInstanceOf[MethodVertex])
            diffs.add(source.longName,"Removed usage of local field "+destination.longName)
        case _ =>
      }
    })

    diffs
  }

  //Get the different nodes between two graphs
  private def getDifferentNodes(graph1:DirectedGraph[EntityVertex, ReferenceEdge],graph2:DirectedGraph[EntityVertex, ReferenceEdge]) : Set[EntityVertex] = {

    var diffs = HashSet[EntityVertex]()

    graph2.vertexSet().asScala.foreach(v => {
      if(!graph1.vertexSet().contains(v)) {

        diffs += v
      }
    })

    diffs
  }

  //Get the different edges between two graphs
  private def getDifferentEdges(graph1:DirectedGraph[EntityVertex, ReferenceEdge],graph2:DirectedGraph[EntityVertex, ReferenceEdge]) : Set[ReferenceEdge] = {

    var diffs = HashSet[ReferenceEdge]()

    graph2.edgeSet().asScala.foreach(e => {
      if(!graph1.edgeSet().contains(e)) {

        diffs += e
      }
    })

    diffs
  }

}