package edu.uic.cs474.hw3.graphing

import edu.uic.cs474.hw3.parsing._

/**
  * Created by Alessandro on 30/10/16.
  */
sealed trait ReferenceEdge {
  def kind: String
  def source: EntityVertex
  def destination: EntityVertex
}

case class CallEdge(source: MethodVertex,
                    destination: MethodVertex) extends ReferenceEdge {
  override def kind: String = Call.kind
}

case class ClassExtendEdge(source: ClassVertex,
                           destination: ClassVertex) extends ReferenceEdge {
  override def kind: String = Extend.kind
}

case class InterfaceExtendEdge(source: InterfaceVertex,
                               destination: InterfaceVertex) extends ReferenceEdge {
  override def kind: String = Extend.kind
}

case class ImplementEdge(source: ClassVertex,
                         destination: InterfaceVertex) extends ReferenceEdge {
  override def kind: String = Implement.kind
}

case class FieldEdge(source: ClassVertex,
                     destination: FieldVertex) extends ReferenceEdge {
  override def kind: String = Define.kind
}

case class UseFieldEdge(source: MethodVertex,
                        destination: FieldVertex) extends ReferenceEdge {
  override def kind: String = Use.kind
}

case class SetLocalVariableEdge(source: MethodVertex,
                                destination: LocalVariableVertex) extends ReferenceEdge {
  override def kind: String = Set.kind
}

case class DefineParameterEdge(source: MethodVertex,
                               destination: ClassVertex) extends ReferenceEdge {
  override def kind: String = Define.kind
}

case class ReturnTypeEdge(source: MethodVertex,
                          destination: ClassVertex) extends ReferenceEdge {
  override def kind: String = "return"
}


