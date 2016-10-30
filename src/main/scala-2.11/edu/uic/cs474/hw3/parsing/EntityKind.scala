package edu.uic.cs474.hw3.parsing

/**
  * Created by Alessandro on 30/10/16.
  */
sealed trait EntityKind { def kind: String }

case object NotUnknown extends EntityKind { val kind = "~unknown"}
case object NotUnresolved extends EntityKind { val kind = "~unresolved"}
case object Method extends EntityKind { val kind = "method"}
case object Constructor extends EntityKind { val kind = "constructor"}
case object Interface extends EntityKind { val kind = "interface"}
case object Annotation extends EntityKind { val kind = "annotation"}
case object Parameter extends EntityKind { val kind = "parameter"}
case object Class extends EntityKind { val kind = "class"}
case object Enum extends EntityKind { val kind = "enum"}
case object ClassOrEnum extends EntityKind { val kind = "class enum"}
case object NotAnnotation extends EntityKind { val kind = "~annotation"}
case object NotTypevariable extends EntityKind { val kind = "~TypeVariable"}
  
case class UnknownEntityKind (kind: String) extends EntityKind


