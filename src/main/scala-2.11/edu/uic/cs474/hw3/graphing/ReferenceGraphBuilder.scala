package edu.uic.cs474.hw3.graphing

import com.scitools.understand.{Entity, Reference}
import edu.uic.cs474.hw3.undestand._
import org.jgrapht.DirectedGraph
import org.jgrapht.graph.{ClassBasedEdgeFactory, DefaultDirectedGraph}

/**
  * Created by Alessandro on 30/10/16.
  */
class ReferenceGraphBuilder(dbManager: DbManager) {
  val referenceGraph: DirectedGraph[EntityVertex, ReferenceEdge] =
    new DefaultDirectedGraph[EntityVertex, ReferenceEdge](new ClassBasedEdgeFactory[EntityVertex, ReferenceEdge](classOf[ReferenceEdge]))

  def build = {

    def getReferenceEntityList(fromEntity: Entity, referenceKind: ReferenceKind, toEntityKind: EntityKind): List[Reference] = {
      return fromEntity.refs(referenceKind.kind, toEntityKind.kind, true).toList
    }

    def getEntityVertex(entity: Entity): EntityVertex = entity.kind().toString match {
      case Class.kind => ClassVertex(entity.simplename(), entity.longname(true))
      case Interface.kind => InterfaceVertex(entity.simplename(), entity.longname(true))
      case Method.kind =>
        val parameterTypeList : List[String] = getReferenceEntityList(entity, Define, Parameter).map(_.ent().`type`())
        MethodVertex(entity.simplename(),
          entity.longname(true).concat("(").concat(parameterTypeList.mkString(",")).concat(")"),
          entity.`type`(),
          parameterTypeList)
      case LocalVariable.kind => VariableVertex(entity.simplename(), entity.longname(true))
      case FieldVariable.kind => VariableVertex(entity.simplename(), entity.longname(true))
    }

    def getReferenceEdge(referenceKind: ReferenceKind, toEntityKind: EntityKind, fromVertex: EntityVertex, toVertex: EntityVertex): ReferenceEdge = (referenceKind,toEntityKind) match {
      case (Extend, Class) => ClassExtendEdge(fromVertex, toVertex)
      case (Extend, Interface) => InterfaceExtendEdge(fromVertex, toVertex)
      case (Implement, Interface) => ImplementEdge(fromVertex, toVertex)
      case (Call, Method) => CallEdge(fromVertex, toVertex)
      case (Define, Method) => DefineMethodEdge(fromVertex, toVertex)
      case (Define, Parameter) => DefineParameterEdge(fromVertex, toVertex)
      case (Set, LocalVariable) => SetLocalVariableEdge(fromVertex, toVertex)
      case (Define, FieldVariable) => DefineFieldVariableEdge(fromVertex, toVertex)
      case (Use, FieldVariable) => UseFieldVariableEdge(fromVertex, toVertex)
      case (Use, LocalVariable) => UseLocalVariableEdge(fromVertex, toVertex)
    }

    def addReferenceHelper(entity: Entity, referenceKind: ReferenceKind, toEntityKind: EntityKind) = {
      getReferenceEntityList(entity, Extend, Class).map(_.ent()).map(referencedEntity => {
        val fromVertex = getEntityVertex(entity)
        val referencedVertex = getEntityVertex(referencedEntity)
        referenceGraph.addVertex(referencedVertex)
        referenceGraph.addEdge(fromVertex, referencedVertex, getReferenceEdge(referenceKind, toEntityKind, fromVertex, referencedVertex))
      })
    }

    def addReferencesToGraph(entity: Entity): Unit = entity.kind().toString match {
      case Class.kind => {
        addReferenceHelper(entity, Extend, Class)
        addReferenceHelper(entity, Implement, Interface)
        addReferenceHelper(entity, Define, FieldVariable)
        addReferenceHelper(entity, Define, Method)
      }
      case Interface.kind => {
        addReferenceHelper(entity, Extend, Interface)
        addReferenceHelper(entity, Define, Method)
      }
      case Method.kind => {
        addReferenceHelper(entity, Call, Method)
        addReferenceHelper(entity, Set, LocalVariable)
        addReferenceHelper(entity, Define, Parameter)
        addReferenceHelper(entity, Use, FieldVariable)
        addReferenceHelper(entity, Use, LocalVariable)
      }
    }

    def addEntityToGraph(entity: Entity): Unit = entity.kind().toString match {
      case Class.kind =>
        val classVertex = getEntityVertex(entity)
        referenceGraph.addVertex(classVertex)
        addReferencesToGraph(entity)
      case Interface.kind =>
        val interfaceVertex = getEntityVertex(entity)
        referenceGraph.addVertex(interfaceVertex)
        addReferencesToGraph(entity)
      case Method.kind =>
        val methodVertex = getEntityVertex(entity)
        referenceGraph.addVertex(methodVertex)
        addReferencesToGraph(entity)
      //variables have no interesting refs
      case LocalVariable.kind =>
        val localVariableVertex = getEntityVertex(entity)
        referenceGraph.addVertex(localVariableVertex)
      case FieldVariable.kind =>
        val fieldVariableVertex = getEntityVertex(entity)
        referenceGraph.addVertex(fieldVariableVertex)
    }

    dbManager.getEntityListByTypeListFromDb(LocalVariable::Nil).map(e => addEntityToGraph(e))
    dbManager.getEntityListByTypeListFromDb(FieldVariable::Nil).map(e => addEntityToGraph(e))
    dbManager.getEntityListByTypeListFromDb(Class::Interface::Method::Nil).map(e => addEntityToGraph(e))

    println(referenceGraph)
  }
}
