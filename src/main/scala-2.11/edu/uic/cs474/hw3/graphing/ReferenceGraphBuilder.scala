package edu.uic.cs474.hw3.graphing

import com.scitools.understand.{Entity, Reference}
import edu.uic.cs474.hw3.undestand._
import org.jgrapht.DirectedGraph
import org.jgrapht.graph.{ClassBasedEdgeFactory, DefaultDirectedGraph}

/**
  * Created by Alessandro on 30/10/16.
  */
class ReferenceGraphBuilder(dbManager: DbManager) {
  val dependencyGraph: DirectedGraph[EntityVertex, ReferenceEdge] =
    new DefaultDirectedGraph[EntityVertex, ReferenceEdge](new ClassBasedEdgeFactory[EntityVertex, ReferenceEdge](classOf[ReferenceEdge]))

  def build = {

    def getReferenceEntityList(fromEntity: Entity, referenceKind: ReferenceKind, toEntityKind: EntityKind): List[Reference] = {
      return fromEntity.refs(referenceKind.kind, toEntityKind.kind, true).toList
    }

    def getEntityVertex(entity: Entity): EntityVertex = entity.kind().toString match {
      case Class.kind => ClassVertex(entity.simplename(), entity.longname(true))
      case Interface.kind => InterfaceVertex(entity.simplename(), entity.longname(true))
      case Method.kind => MethodVertex(entity.simplename(),
        entity.longname(true), entity.`type`(),
        getReferenceEntityList(entity, Define, Parameter).map(_.ent().`type`()))
      case Variable.kind => VariableVertex(entity.simplename(), entity.longname(true))
    }

    def getReferenceEdge(referenceKind: ReferenceKind, entityKind: EntityKind, fromVertex: EntityVertex, toVertex: EntityVertex): ReferenceEdge = (referenceKind, entityKind) match {
      case (Extend, Class) => ClassExtendEdge(fromVertex, toVertex)
      case (Implement, Interface) => ImplementEdge(fromVertex, toVertex)
      //TODO finish cases

    }

    def addReferenceHelper(entity: Entity, referenceKind: ReferenceKind, entityKind: EntityKind) = {
      getReferenceEntityList(entity, Extend, Class).map(_.ent()).map(referencedEntity => {
        val fromVertex = getEntityVertex(entity)
        val referencedVertex = getEntityVertex(referencedEntity)
        dependencyGraph.addVertex(referencedVertex)
        dependencyGraph.addEdge(fromVertex, referencedVertex, getReferenceEdge(referenceKind, entityKind, fromVertex, referencedVertex))
      })
    }

    def addReferencesToGraph(entity: Entity): Unit = entity.kind().toString match {
      case Class.kind => {
        addReferenceHelper(entity, Extend, Class)
        addReferenceHelper(entity, Implement, Interface)
      }
      //TODO: finish cases
    }

    def addEntityToGraph(entity: Entity): Unit = entity.kind().toString match {
      case Class.kind => {
        val classVertex = getEntityVertex(entity)
        dependencyGraph.addVertex(classVertex)
        addReferencesToGraph(entity)
      }
      //TODO: finish cases
    }
  }
}
