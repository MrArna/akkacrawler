package edu.uic.cs474.hw3.messages

sealed trait Message

case object Start extends Message
case object Stop extends Message

case class Parse(repository:String,path:String) extends Message
case class DoneParsing(repository:String,graph:Any)  extends Message

case class Analyze(graphV1:Any,graphV2:Any) extends Message
case class DoneAnalyzing(differences:Any) extends Message



