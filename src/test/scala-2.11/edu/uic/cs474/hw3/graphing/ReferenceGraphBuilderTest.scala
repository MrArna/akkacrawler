package edu.uic.cs474.hw3.graphing

import com.scitools.understand.{Entity, Kind, Reference}
import edu.uic.cs474.hw3.undestand.{DbManager, EntityKind}
import org.scalatest._
import org.scalatest.mock._
import org.mockito.Mockito._
import edu.uic.cs474.hw3.undestand._
import org.jgrapht.DirectedGraph
import org.jgrapht.graph.{ClassBasedEdgeFactory, SimpleDirectedGraph}
import org.mockito.Mockito
import org.scalatest.mockito.MockitoSugar

/**
  * Created by Alessandro on 08/11/16.
  */
class ReferenceGraphBuilderTest extends FunSuite with MockitoSugar {

  test("testBuildGraph") {

    val mockDbManager = mock[DbManager]
    val firstClass = mock[Entity]
    val secondClass = mock[Entity]
    val interface = mock[Entity]
    val extendReference: Reference = mock[Reference]
    val interfaceReference: Reference = mock[Reference]
    val classKind = mock[Kind]
    val interfaceKind = mock[Kind]
    var classEntities = List[Entity](firstClass, secondClass)
    val expectedGraph: DirectedGraph[EntityVertex, ReferenceEdge] = new SimpleDirectedGraph[EntityVertex, ReferenceEdge](new ClassBasedEdgeFactory[EntityVertex, ReferenceEdge](classOf[ReferenceEdge]))
    val firstClassVertex: ClassVertex = new ClassVertex("package.class1", "long.package.class1")
    val secondClassVertex: ClassVertex = new ClassVertex("package.class2", "long.package.class2")
    val interfaceVertex: InterfaceVertex = new InterfaceVertex("package.interface", "long.package.interface")

    Mockito.when(classKind.name).thenReturn("Class")
    Mockito.when(interfaceKind.name).thenReturn("Interface")
    Mockito.when(extendReference.ent).thenReturn(secondClass)
    Mockito.when(interfaceReference.ent).thenReturn(interface)

    Mockito.when(interface.simplename).thenReturn("package.interface")
    Mockito.when(interface.longname(true)).thenReturn("long.package.interface")
    Mockito.when(interface.kind).thenReturn(interfaceKind)
    Mockito.when(firstClass.refs(Extend.kind, Interface.kind, true)).thenReturn(Array[Reference]())

    Mockito.when(firstClass.simplename).thenReturn("package.class1")
    Mockito.when(firstClass.longname(true)).thenReturn("long.package.class1")
    Mockito.when(firstClass.kind).thenReturn(classKind)
    Mockito.when(firstClass.refs(Implement.kind, Interface.kind, true)).thenReturn(Array(interfaceReference))
    Mockito.when(firstClass.refs(Extend.kind, Class.kind, true)).thenReturn(Array(extendReference))
    Mockito.when(firstClass.refs(Define.kind, FieldVariable.kind, true)).thenReturn(Array(extendReference))
    Mockito.when(firstClass.refs(Define.kind, Method.kind, true)).thenReturn(Array(extendReference))

    Mockito.when(secondClass.simplename).thenReturn("package.class2")
    Mockito.when(secondClass.longname(true)).thenReturn("long.package.class2")
    Mockito.when(secondClass.kind).thenReturn(classKind)
    Mockito.when(secondClass.refs(Implement.kind, Interface.kind, true)).thenReturn(Array[Reference]())
    Mockito.when(secondClass.refs(Extend.kind, Class.kind, true)).thenReturn(Array[Reference]())
    Mockito.when(secondClass.refs(Define.kind, FieldVariable.kind, true)).thenReturn(Array[Reference]())
    Mockito.when(secondClass.refs(Define.kind, Method.kind, true)).thenReturn(Array[Reference]())

    Mockito.when(mockDbManager.getEntityListByTypeListFromDb(Class)).thenReturn(classEntities)

    val graphBuilder: ReferenceGraphBuilder = new ReferenceGraphBuilder(mockDbManager)
    graphBuilder.buildGraph(List(Class))
    val graph = graphBuilder.referenceGraph

    expectedGraph.addVertex(firstClassVertex)
    expectedGraph.addVertex(secondClassVertex)
    expectedGraph.addVertex(interfaceVertex)
    expectedGraph.addEdge(firstClassVertex, secondClassVertex, new ClassExtendEdge(firstClassVertex, secondClassVertex))
    expectedGraph.addEdge(firstClassVertex, interfaceVertex, new ImplementEdge(firstClassVertex, interfaceVertex))

    assert(expectedGraph.vertexSet() == graph.vertexSet())
    assert(expectedGraph.edgeSet() == graph.edgeSet())
  }



}
