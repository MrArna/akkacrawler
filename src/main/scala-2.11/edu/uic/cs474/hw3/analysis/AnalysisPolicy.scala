package edu.uic.cs474.hw3.analysis

/**
  * Created by Alessandro on 01/11/16.
  */
sealed trait AnalysisPolicy

case object NVersionsTwoByTwo extends AnalysisPolicy
case object NVersionsFirstLast extends AnalysisPolicy
