package com.nil2.sbtmaven

import sbt._
import Keys._

/*
TODO:
- build paths settings(scalaSource in Compile := file(""), or <<= )
- project metadata(devloperers, website...)
- project publishing(to maven, to ivy)
- dependency exclude
- logging
*/

abstract class MavenBuild extends PomBuild with SelectorDSL with GlobFactory with OrFactory {
  implicit def stringToSelector(s: String) = produce(s)
}

trait PomBuild extends Build {
  protected lazy val pom = new Pom(".", "pom.xml", None)

  lazy val root = pom.project

  override def projects = pom.allProjects
}
