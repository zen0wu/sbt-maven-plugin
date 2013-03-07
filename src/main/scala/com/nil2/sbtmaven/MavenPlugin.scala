package com.nil2.sbtmaven

import sbt._

object MavenPlugin extends Plugin {
  override val settings: Seq[Setting[_]] = {
    val pom = new Pom(".")
    val noModuleDef = pom.modules.isEmpty
    // If this is a multiple module pom, we won't do anything
    if (!noModuleDef && !MavenBuild.isInstantiated) {
      // Here we should use log.info, but I don't know how
      println("No MavenBuild has been instantiated and not a multi-module project, auto importing pom.xml")
      pom.project.settings
    }
    else {
      println("It seems to be a Multi-module project")
      Seq()
    }
  }
}
