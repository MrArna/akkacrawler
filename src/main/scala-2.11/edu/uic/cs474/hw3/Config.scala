package edu.uic.cs474.hw3

/**
  * The global configuration.
  */
object Config {

  //Maximum number of project version checkout to instantiated
  private var _maxProjectVersionCheckouts:Int = _
  //Maximum number of project parsers to be instantiated by a single project handler
  private var _maxProjectVersionParsers:Int = _
  //Maximum number of project parsers to be instantiated by a single project handler
  private var _maxProjectVersionGraphers:Int = _
  //The total number of projects to be downloaded
  private var _numProjects:Int = _
  //The language that projects to be downloaded should be written in
  private var _language:String = _
  //The main folder where downloaded and temporary files should be created
  private var _mainFolder:String = _

  //Getters
  def maxProjectVersionCheckouts = _maxProjectVersionCheckouts
  def maxProjectVersionParsers = _maxProjectVersionParsers
  def maxProjectVersionGraphers = _maxProjectVersionGraphers
  def numProjects = _numProjects
  def language:String = _language
  def mainFolder:String = _mainFolder

  //Setters
  def maxProjectVersionCheckouts_=(n:Int):Unit = _maxProjectVersionCheckouts=n
  def maxProjectVersionParsers_=(n:Int):Unit = _maxProjectVersionParsers=n
  def maxProjectVersionGraphers_=(n:Int):Unit = _maxProjectVersionGraphers=n
  def numProjects_=(n:Int):Unit = _numProjects=n
  def language_=(s:String):Unit = _language=s
  def mainFolder_=(s:String):Unit = _mainFolder=s
}
