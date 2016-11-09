package edu.uic.cs474.hw3.analysis

import org.scalatest.FunSuite

class DifferenceTest extends FunSuite {

  test("Test Difference") {

    val diff = new Difference("A")
    assert(diff.method=="A")
    assert(diff.reasons.isEmpty)

    diff.addReason("B")
    assert(diff.reasons.contains("B"))
    assert(diff.reasons.size==1)

  }
}
