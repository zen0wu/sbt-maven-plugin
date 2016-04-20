package com.timcharper.sbt.maven

import sbt._
import Keys._

abstract class MavenBuild extends PomBuild with SelectorDSL with GlobFactory with OrFactory {
	MavenBuild.instantiate(this.getClass.getName)

  implicit def stringToSelector(s: String) = produce(s)
}

object MavenBuild {
	private var instanceName: Option[String] = None
	private[MavenBuild] def instantiate(name: String) { instanceName = Some(name) }

	def getInstanceClassName: Option[String] = instanceName
}

trait PomBuild extends Build {
  protected lazy val pom = Pom(new java.io.File("./pom.xml"))

  lazy val root = pom.project

  override def projects = root :: pom.allModuleProjects
}
