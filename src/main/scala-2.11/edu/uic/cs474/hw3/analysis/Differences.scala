package edu.uic.cs474.hw3.analysis

import scala.collection.immutable.HashMap

/**
  * A collection of differences between two projects.
  * @param repository The name of the repository considered in this analysis.
  * @param version1 The name of the first version to compare.
  * @param version2 The name of the second version to compare.
  */
class Differences(repository:String,version1:String,version2:String) {

  //The collection of differences
  private val differences = scala.collection.mutable.HashMap[String,Difference]()

  /**
    * Add a new difference (a method to retest together with the reason why)
    * @param methodName The method to retest.
    * @param reason The reason why this method should be retested.
    */
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

  /**
    * @return An iterator over the differences
    */
  def getDifferences() : Iterator[Difference] = differences.valuesIterator

  /**
    *
    * @return The name of the considered repository
    */
  def repositoryName = repository

  /**
    *
    * @return The name of the first version of the project
    */
  def v1 = version1

  /**
    *
    * @return The name of the second version of the project
    */
  def v2 = version2
}
