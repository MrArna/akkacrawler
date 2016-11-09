package edu.uic.cs474.hw3.versioning

/**
  * Created by Alessandro on 08/11/16.
  */
sealed trait VersionPolicy
object TagPolicy extends VersionPolicy
object CommitPolicy extends VersionPolicy
