package edu.uic.cs474.hw3.analysis

/**
  * Created by andrea on 31/10/16.
  */
class Difference(methodName:String) {

  val reasons = scala.collection.mutable.HashSet[String]()

  def method = methodName

  def addReason(reason:String) : Unit = reasons += reason

  override def toString() : String = {

    var ret = methodName

    reasons.foreach(r => {
      ret += ("\n\t" + r)
    })

    ret
  }
}
