package edu.uic.cs474.hw3.messages

sealed trait Message

case class Start(numProjects:Int,language:String) extends Message
case object Stop extends Message

case class Parse(path:String) extends Message
case class DoneParsing(graph:Any)  extends Message

case class StartPool(numActors:Int) extends Message



