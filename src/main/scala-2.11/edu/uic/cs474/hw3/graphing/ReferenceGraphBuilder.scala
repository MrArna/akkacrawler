package edu.uic.cs474.hw3.graphing

import com.scitools.understand.Entity
import org.jgrapht.DirectedGraph
import org.jgrapht.graph.{ClassBasedEdgeFactory, DefaultDirectedGraph}

/**
  * Created by Alessandro on 30/10/16.
  */
class ReferenceGraphBuilder(entityList: List[Entity]) {
  val dependencyGraph: DirectedGraph[EntityVertex, ReferenceEdge] =
    new DefaultDirectedGraph[EntityVertex, ReferenceEdge](new ClassBasedEdgeFactory[EntityVertex, ReferenceEdge](classOf[ReferenceEdge]))

  def build = {

  }
}
