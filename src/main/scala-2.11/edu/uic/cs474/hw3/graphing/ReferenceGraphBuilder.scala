package edu.uic.cs474.hw3.graphing

import com.scitools.understand.{Entity, Reference}
import edu.uic.cs474.hw3.undestand._
import org.jgrapht.DirectedGraph
import org.jgrapht.graph.{ClassBasedEdgeFactory, DefaultDirectedGraph}

import scala.collection.JavaConverters._

/**
  * Created by Alessandro on 30/10/16.
  */
class ReferenceGraphBuilder(dbManager: DbManager) {
  var referenceGraph: DirectedGraph[EntityVertex, ReferenceEdge] =
    new DefaultDirectedGraph[EntityVertex, ReferenceEdge](new ClassBasedEdgeFactory[EntityVertex, ReferenceEdge](classOf[ReferenceEdge]))

  def build = {

    implicit class Regex(sc: StringContext) {
      def r = new util.matching.Regex(sc.parts.mkString, sc.parts.tail.map(_ => "x"): _*)
    }

    def matchKind(entity: Entity): EntityKind = entity.kind().name match {
      case r".*Class.*" => Class
      case r".*Interface.*" => Interface
      //first match the local variables
      case r".*Variable Local.*" => LocalVariable
      //then match the non local variables
      case r".*Variable.*" => LocalVariable
      case r".*Method.*" => Method
      case r".*Constructor.*" => Method
      case r".*Annotation.*" => Annotation
      case r".*Enum.*" => Enum
      case r".*EnumConstant.*" => EnumConstant
      case _ => UnknownEntityKind(entity.kind().name())
    }

    def getReferenceEntityList(fromEntity: Entity, referenceKind: ReferenceKind, toEntityKind: EntityKind): List[Reference] = {
      return fromEntity.refs(referenceKind.kind, toEntityKind.kind, true).toList
    }

    def getEntityVertex(entity: Entity): EntityVertex = matchKind(entity) match {
      case Class => ClassVertex(entity.simplename(), entity.longname(true))
      case Interface => InterfaceVertex(entity.simplename(), entity.longname(true))
      case Method =>
        val parameterTypeList : List[String] = getReferenceEntityList(entity, Define, Parameter).map(_.ent().`type`())
        MethodVertex(entity.simplename(),
          entity.longname(true).concat("(").concat(parameterTypeList.mkString(",")).concat(")"),
          entity.`type`(),
          parameterTypeList)
      case LocalVariable => VariableVertex(entity.simplename(), entity.longname(true))
      case FieldVariable => VariableVertex(entity.simplename(), entity.longname(true))
      case Enum => EnumVertex(entity.simplename(), entity.longname(true))
      case EnumConstant => EnumConstantVertex(entity.simplename(), entity.longname(true))
      case _ => println("unknown entity vertex " + entity.kind()); Nil.head
    }

    def getReferenceEdge(referenceKind: ReferenceKind, toEntityKind: EntityKind, fromVertex: EntityVertex, toVertex: EntityVertex): ReferenceEdge = (referenceKind,toEntityKind) match {
      case (Extend, Class) => ClassExtendEdge(fromVertex, toVertex)
      case (Extend, Interface) => InterfaceExtendEdge(fromVertex, toVertex)
      case (Implement, Interface) => ImplementEdge(fromVertex, toVertex)
      case (Call, Method) => CallEdge(fromVertex, toVertex)
      case (Define, Method) => DefineMethodEdge(fromVertex, toVertex)
      case (Set, LocalVariable) => SetLocalVariableEdge(fromVertex, toVertex)
      case (Define, FieldVariable) => DefineFieldVariableEdge(fromVertex, toVertex)
      case (Use, FieldVariable) => UseFieldVariableEdge(fromVertex, toVertex)
      case (Use, LocalVariable) => UseLocalVariableEdge(fromVertex, toVertex)
      case _ => println("unknown reference edge " + referenceKind.kind + "," + toEntityKind.kind); Nil.head
    }

    def addReferenceHelper(entity: Entity, referenceKind: ReferenceKind, toEntityKind: EntityKind) = {
      getReferenceEntityList(entity, referenceKind, toEntityKind).map(_.ent()).map(referencedEntity => {
        val fromVertex = getEntityVertex(entity)
        val referencedVertex = getEntityVertex(referencedEntity)
        referenceGraph.addVertex(referencedVertex)
        referenceGraph.addEdge(fromVertex, referencedVertex, getReferenceEdge(referenceKind, toEntityKind, fromVertex, referencedVertex))
      })
    }

    def addReferencesToGraph(entity: Entity): Unit = matchKind(entity) match {
      case Class =>
        addReferenceHelper(entity, Extend, Class)
        addReferenceHelper(entity, Implement, Interface)
        addReferenceHelper(entity, Define, FieldVariable)
        addReferenceHelper(entity, Define, Method)
      case Interface =>
        addReferenceHelper(entity, Extend, Interface)
        addReferenceHelper(entity, Define, Method)
      case Method =>
        addReferenceHelper(entity, Call, Method)
        addReferenceHelper(entity, Set, LocalVariable)
        addReferenceHelper(entity, Use, FieldVariable)
        addReferenceHelper(entity, Use, LocalVariable)
      case _ => println("Unknown reference from " + entity.kind().toString())
    }

    def addEntityToGraph(entity: Entity): Unit = matchKind(entity) match {
      case Class | Interface | Method | LocalVariable | FieldVariable | Enum | EnumConstant =>
        val vertex = getEntityVertex(entity)
        referenceGraph.addVertex(vertex)
      case _ => println("Unknown entity " + entity.kind().toString())
    }

    def buildGraph(entityKindList: List[EntityKind]): Unit = {
      entityKindList
        .map(entityKind => dbManager.getEntityListByTypeListFromDb(entityKind))
        .map(entityList => entityList.filterNot(e => e.longname(true).startsWith(Java.prefix) || e.longname(true).startsWith(Sun.prefix))
          .map(entity => addEntityToGraph(entity)))
      entityKindList
        .filterNot((entityKind => entityKind == LocalVariable || entityKind == FieldVariable))
        .map(entityKind => dbManager.getEntityListByTypeListFromDb(entityKind))
        .map(entityList => entityList.filterNot(e => e.longname(true).startsWith(Java.prefix) || e.longname(true).startsWith(Sun.prefix))
          .map(entity => addReferencesToGraph(entity)))
    }

    buildGraph(List(FieldVariable, LocalVariable, Method, Class, Interface))

    referenceGraph.edgeSet().asScala.map(edge => println(edge.source.name + " " + edge.kind + " " + edge.destination.name))
  }
}
