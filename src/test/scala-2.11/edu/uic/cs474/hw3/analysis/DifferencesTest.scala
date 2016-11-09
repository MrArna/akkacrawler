package edu.uic.cs474.hw3.analysis

import org.scalatest.FunSuite

class DifferencesTest extends FunSuite {

  test("Test Differences") {

    val diffs = new Differences("repo","v1","v2")
    assert(diffs.repositoryName=="repo")
    assert(diffs.v1=="v1")
    assert(diffs.v2=="v2")
    assert(diffs.getDifferences().size==0)

    diffs.add("A","B")
    assert(diffs.getDifferences().size==1)
    assert(diffs.getDifferences().next().method=="A")
    assert(diffs.getDifferences().next().reasons.contains("B"))

    diffs.add("A","C")
    assert(diffs.getDifferences().size==1)
    assert(diffs.getDifferences().next().method=="A")
    assert(diffs.getDifferences().next().reasons.contains("B"))
    assert(diffs.getDifferences().next().reasons.contains("C"))

    diffs.add("D","A")
    assert(diffs.getDifferences().size==2)
  }
}
