package edu.uic.cs474.hw3.parsing

/**
  * Created by Alessandro on 30/10/16.
  */
sealed trait ReferenceKind { def kind: String }

case object Define extends ReferenceKind { val kind = "define"}
case object Extend extends ReferenceKind { val kind = "extend"}
case object Implement extends ReferenceKind { val kind = "implement"}
case object Call extends ReferenceKind { val kind = "call"}
case object Set extends ReferenceKind { val kind = "set"}
case object Use extends ReferenceKind { val kind = "use"}

case class UnknownReferenceKind (kind: String) extends ReferenceKind


