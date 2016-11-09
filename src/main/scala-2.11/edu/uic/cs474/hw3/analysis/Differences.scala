package edu.uic.cs474.hw3.analysis

import scala.collection.immutable.HashMap

/**
  * Created by andrea on 31/10/16.
  */
class Differences(repository:String,version1:String,version2:String) {

  private val differences = scala.collection.mutable.HashMap[String,Difference]()

  def add(methodName:String,reason:String) : Unit = {

    differences.contains(methodName) match {
      case true =>
        differences(methodName).addReason(reason)
      case false =>
        val diff = new Difference(methodName)
        diff.addReason(reason)
        differences(methodName) = diff
    }
  }

  def getDifferences() : Iterator[Difference] = differences.valuesIterator

  def repositoryName = repository
  def v1 = version1
  def v2 = version2
}
