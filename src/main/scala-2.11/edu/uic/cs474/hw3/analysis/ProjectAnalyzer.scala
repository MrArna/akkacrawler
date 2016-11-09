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
  * Created by andrea on 23/10/16.
  */
class ProjectAnalyzer extends Actor {

  val graphListBuffer = ListBuffer.empty[(String, List[String], String, DirectedGraph[EntityVertex,ReferenceEdge])]

  def NVersionsFirstLastAnalysis(nVersionList: List[String]): Unit = {
    graphListBuffer.toList
      .sliding(2) //there are only two by preconditions, but this way I get a list which is easy to index
      .map(listOfTwo => analyze(listOfTwo(0)._1, listOfTwo(0)._3, listOfTwo(1)._3, listOfTwo(0)._4, listOfTwo(1)._4))
      .foreach(differences => sender ! DoneAnalyzing(differences))
  }

  def NVersionsTwoByTwoAnalysis(nVersionList: List[String]): Unit = {
    graphListBuffer.toList
      .sortWith((firstTuple, secondTuple) => nVersionList.indexOf(firstTuple._3) < nVersionList.indexOf(secondTuple._3))
      .sliding(2)
      .map(listOfTwo => analyze(listOfTwo(0)._1, listOfTwo(0)._3, listOfTwo(1)._3, listOfTwo(0)._4, listOfTwo(1)._4))
      .foreach(differences => sender ! DoneAnalyzing(differences))
  }

  def receive = {
    case Analyze(repository, nVersionList, version, graph) =>
      graphListBuffer += ((repository, nVersionList, version, graph))
      println("Graph buffer size is " + graphListBuffer.size)
      if (graphListBuffer.size == nVersionList.size) {
        Config.analysisPolicy match {
          case NVersionsFirstLast => NVersionsFirstLastAnalysis(nVersionList)
          case NVersionsTwoByTwo => NVersionsTwoByTwoAnalysis(nVersionList)
        }
      }
  }

  private def analyze(repository:String,version1:String,version2:String,graph1:DirectedGraph[EntityVertex, ReferenceEdge],graph2:DirectedGraph[EntityVertex, ReferenceEdge]) : Differences = {

    val diffs = new Differences(repository,version1,version2)

    val newEdges = getDifferentEdges(graph1,graph2)
    val oldEdges = getDifferentEdges(graph2,graph1)

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

  private def getVertexEdges[T](graph:DirectedGraph[EntityVertex, ReferenceEdge],vertex:EntityVertex,direction:Direction) : Set[ReferenceEdge] = {

    var edges = HashSet[ReferenceEdge]()

    (if(direction==IN) graph.incomingEdgesOf(vertex) else graph.outgoingEdgesOf(vertex)).asScala.foreach(e => {
      if(e.isInstanceOf[T])
        edges += e
    })

    edges
  }

  private def getDifferentNodes(graph1:DirectedGraph[EntityVertex, ReferenceEdge],graph2:DirectedGraph[EntityVertex, ReferenceEdge]) : Set[EntityVertex] = {

    var diffs = HashSet[EntityVertex]()

    graph2.vertexSet().asScala.foreach(v => {
      if(!graph1.vertexSet().contains(v)) {

        diffs += v
      }
    })

    diffs
  }

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

sealed trait Direction
case object IN extends Direction
case object OUT extends Direction