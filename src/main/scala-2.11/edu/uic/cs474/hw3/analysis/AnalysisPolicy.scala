package edu.uic.cs474.hw3.analysis

/**
  * Created by Alessandro on 01/11/16.
  */
sealed trait AnalysisPolicy

case object NVersionsTwoByTwo
case object NVersionsEveryM
case object NVersionsFirstLast
