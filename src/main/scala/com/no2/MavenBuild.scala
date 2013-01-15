package com.no2.sbtmaven

import sbt._
import Keys._

/*
TODO:
- [DONE] project info resolution
- [DONE] dependency and dependency management(including scope, classifier)
- [DONE] build.sbt and Build.scala, interface design and impl
- [DONE] read maven settings.xml for mirrors
- [DONE] multiple modules projects support and API
- [DONE] single module project API
- [DONE] property resolution and common properties(encoding, source level, implicit properties(project.*,settings.*,env.*)
- [DONE] set scala verseion according to dependency declarsion

- Become a plugin, not a library!

- repositories in pom.xml
- project metadata(devloperers, website...)
- build paths settings(scalaSource in Compile := file(""), or <<= )
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
