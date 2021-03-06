package edu.uic.cs474.hw3.graphing

/**
  * Created by Alessandro on 30/10/16.
  */
sealed trait EntityVertex {
  def name: String
  def longName: String
}
case class ClassVertex(name: String, longName: String) extends EntityVertex
case class InterfaceVertex(name: String, longName: String) extends EntityVertex
case class MethodVertex(name: String, longName: String, returnType: String, parameters: List[String]) extends EntityVertex
case class VariableVertex(name: String, longName: String) extends EntityVertex
case class EnumConstantVertex(name: String, longName: String) extends EntityVertex
case class EnumVertex(name: String, longName: String) extends EntityVertex




