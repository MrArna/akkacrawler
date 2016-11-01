package edu.uic.cs474.hw3.graphing

import edu.uic.cs474.hw3.undestand._
import edu.uic.cs474.hw3.parsing._

/**
  * Created by Alessandro on 30/10/16.
  */
sealed trait ReferenceEdge {
  def kind: String
  def source: EntityVertex
  def destination: EntityVertex
}

case class CallEdge(source: EntityVertex,
                    destination: EntityVertex) extends ReferenceEdge {
  override def kind: String = Call.kind
}

case class ClassExtendEdge(source: EntityVertex,
                           destination: EntityVertex) extends ReferenceEdge {
  override def kind: String = Extend.kind
}

case class InterfaceExtendEdge(source: EntityVertex,
                               destination: EntityVertex) extends ReferenceEdge {
  override def kind: String = Extend.kind
}

case class ImplementEdge(source: EntityVertex,
                         destination: EntityVertex) extends ReferenceEdge {
  override def kind: String = Implement.kind
}

case class DefineFieldVariableEdge(source: EntityVertex,
                           destination: EntityVertex) extends ReferenceEdge {
  override def kind: String = Define.kind
}

case class UseFieldVariableEdge(source: EntityVertex,
                        destination: EntityVertex) extends ReferenceEdge {
  override def kind: String = Use.kind
}

case class UseLocalFieldEdge(source: EntityVertex,
                        destination: EntityVertex) extends ReferenceEdge {
  override def kind: String = Use.kind
}

case class SetLocalVariableEdge(source: EntityVertex,
                                destination: EntityVertex) extends ReferenceEdge {
  override def kind: String = Set.kind
}

case class UseLocalVariableEdge(source: EntityVertex,
                                destination: EntityVertex) extends ReferenceEdge {
  override def kind: String = Set.kind
}

case class DefineMethodEdge(source: EntityVertex,
                               destination: EntityVertex) extends ReferenceEdge {
  override def kind: String = Define.kind
}




