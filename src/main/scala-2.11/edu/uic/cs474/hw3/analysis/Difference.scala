package edu.uic.cs474.hw3.analysis

/**
  * A difference between two projects in terms of a method to be retested. This class also provides a set of reasons
  * why the method should be retested.
  * @param methodName The method to be retested
  */
class Difference(methodName:String) {

  //The set of reasons
  val reasons = scala.collection.mutable.HashSet[String]()

  /**
    * @return The method to be retested
    */
  def method = methodName

  /**
    * Add a new reason.
    * @param reason The reason to add.
    */
  def addReason(reason:String) : Unit = reasons += reason

  override def toString() : String = {

    var ret = methodName

    reasons.foreach(r => {
      ret += ("\n\t" + r)
    })

    ret
  }
}
