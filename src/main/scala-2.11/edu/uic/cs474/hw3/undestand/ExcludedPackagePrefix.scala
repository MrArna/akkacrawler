package edu.uic.cs474.hw3.undestand

/**
  * Created by Alessandro on 30/10/16.
  */

sealed trait ExcludedPackagePrefix { def prefix: String }

case object Java extends ExcludedPackagePrefix { val prefix = "java."}
case object Android extends ExcludedPackagePrefix { val prefix ="android."}
case object Sun extends ExcludedPackagePrefix { val prefix = "sun."}
