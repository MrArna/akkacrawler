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
case class MethodVertex(name: String, longName: String) extends EntityVertex
case class LocalVariableVertex(name: String, longName: String) extends EntityVertex
case class FieldVertex(name: String, longName: String) extends EntityVertex


