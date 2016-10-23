package edu.uic.cs474.hw3

/**
  * The global configuration.
  */
object Config {

  //Maximum number of project handlers to instantiated
  var _maxProjectHandlers:Int = _
  //Maximum number of project parsers to be instantiated by a single project handler
  var _maxProjectParsers:Int = _
  //The total number of projects to be downloaded
  var _numProjects:Int = _
  //The language that projects to be downloaded should be written in
  var _language:String = _
  //The main folder where downloaded and temporary files should be created
  var _mainFolder:String = _

  //Getters
  def maxProjectHandlers = _maxProjectHandlers
  def maxProjectParsers = _maxProjectParsers
  def numProjects = _numProjects
  def language:String = _language
  def mainFolder:String = _mainFolder

  //Setters
  def maxProjectHandlers_=(n:Int):Unit = _maxProjectHandlers=n
  def maxProjectParsers_=(n:Int):Unit = _maxProjectParsers=n
  def numProjects_=(n:Int):Unit = _numProjects=n
  def language_=(s:String):Unit = _language=s
  def mainFolder_=(s:String):Unit = _mainFolder=s
}
