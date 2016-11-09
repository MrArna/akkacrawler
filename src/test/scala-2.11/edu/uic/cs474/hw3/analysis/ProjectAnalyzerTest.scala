package edu.uic.cs474.hw3.analysis

import akka.actor.{Actor, ActorSystem}
import akka.actor.Status.Success
import akka.testkit.TestActorRef
import edu.uic.cs474.hw3.Config
import edu.uic.cs474.hw3.graphing._
import edu.uic.cs474.hw3.messages.{Analyze, DoneAnalyzing}
import org.jgrapht.DirectedGraph
import org.jgrapht.graph.{ClassBasedEdgeFactory, DefaultDirectedGraph}
import org.scalatest.FunSuite

/**
  * Created by andrea on 31/10/16.
  */
class ProjectAnalyzerTest extends FunSuite {

  Config.analysisPolicy = NVersionsFirstLast

  val graph1: DirectedGraph[EntityVertex, ReferenceEdge] =
    new DefaultDirectedGraph[EntityVertex, ReferenceEdge](new ClassBasedEdgeFactory[EntityVertex, ReferenceEdge](classOf[ReferenceEdge]))
  val graph2: DirectedGraph[EntityVertex, ReferenceEdge] =
    new DefaultDirectedGraph[EntityVertex, ReferenceEdge](new ClassBasedEdgeFactory[EntityVertex, ReferenceEdge](classOf[ReferenceEdge]))

  val class1 = new ClassVertex("A","A")
  val class2 = new ClassVertex("B","B")
  val method1 = new MethodVertex("a","a",null,null)
  val method2 = new MethodVertex("b","b",null,null)
  val field1 = new VariableVertex("fa","fa")
  val class3 = new ClassVertex("C","C")
  val method3 = new MethodVertex("c","c",null,null)

  val def1 = DefineMethodEdge(class1,method1)
  val def2 = DefineMethodEdge(class2,method2)
  val call1 = CallEdge(method1,method2)
  val def3 = new DefineFieldVariableEdge(class1,field1)
  val use1 = new UseFieldVariableEdge(method1,field1)
  val call2 = CallEdge(method2,method1)
  val def4 = new DefineMethodEdge(class3,method3)

  graph1.addVertex(class1)
  graph1.addVertex(class2)
  graph1.addVertex(method1)
  graph1.addVertex(method2)
  graph1.addVertex(field1)
  graph1.addEdge(class1,method1,def1)
  graph1.addEdge(class2,method2,def2)
  graph1.addEdge(method1,method2,call1)
  graph1.addEdge(class1,field1,def3)
  graph1.addEdge(method1,field1,use1)

  graph2.addVertex(class1)
  graph2.addVertex(class2)
  graph2.addVertex(method1)
  graph2.addVertex(method2)
  graph2.addVertex(field1)
  graph2.addVertex(class3)
  graph2.addVertex(method3)
  graph2.addEdge(class1,method1,def1)
  graph2.addEdge(class2,method2,def2)
  graph2.addEdge(method1,method2,call1)
  graph2.addEdge(class1,field1,def3)
  graph2.addEdge(method2,method1,call2)
  graph2.addEdge(class3,method3,def4)

  test("Test Analyzer") {

    implicit val system = ActorSystem()

    val analyzer = TestActorRef[ProjectAnalyzer]
    val printer = TestActorRef[ResultHandler]
    val receiver = TestActorRef(new Receiver(analyzer,printer))
    receiver ! Analyze("Repository Name",List("V1","V2"),List(("V1",graph1),("V2",graph2)))

  }
}

class Receiver(ref:TestActorRef[ProjectAnalyzer],ref2:TestActorRef[ResultHandler]) extends Actor {

  def receive = {

    case Analyze(repository,nVersionList,versionGraphList) =>
      ref ! Analyze(repository,nVersionList,versionGraphList)
    case DoneAnalyzing(differences) =>
      ref2 ! DoneAnalyzing(differences)
      assert(differences.repositoryName=="Repository Name")
      assert(differences.v1=="V1")
      assert(differences.v2=="V2")
      assert(differences.getDifferences().size==3)
      val iter = differences.getDifferences()
      var methods = List[String]()
      iter.foreach(methods::=_.method)
      assert(methods.size==3)
      assert(methods.contains("a"))
      assert(methods.contains("b"))
      assert(methods.contains("c"))

  }
}
