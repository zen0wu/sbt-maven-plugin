package com.nil2.sbtmaven

import sbt._
import Keys._

/*
TODO:
- build paths settings(scalaSource in Compile := file(""), or <<= )
*/

abstract class MavenBuild extends PomBuild with SelectorDSL with GlobFactory with OrFactory {
	MavenBuild.instantiate

  implicit def stringToSelector(s: String) = produce(s)
}

object MavenBuild {
	private var instantiated = false

	private[MavenBuild] def instantiate { instantiated = true }

	def isInstantiated = instantiated
}

trait PomBuild extends Build {
  protected lazy val pom = new Pom(".", "pom.xml", None)

  lazy val root = pom.project

  override def projects = pom.allProjects
}
