package com.github.shivawu.sbt.maven

import sbt._

object MavenPlugin extends Plugin {
  override val settings: Seq[Setting[_]] = {
    val pomFile = new java.io.File("./pom.xml")

    val res: Seq[Setting[_]] = MavenBuild.getInstanceClassName match {
      case Some(buildClass) => 
        ConsoleLogger().info("Using user-defined Build class of [" + buildClass + "]")
        Seq()
      case None =>
	pomFile.exists() match {
	  case true =>
            ConsoleLogger().info("Found a pom file at " + pomFile.getAbsolutePath())
            val pom = Pom(pomFile)
            val multiModuleBuild = !pom.modules.isEmpty
            if (!multiModuleBuild) {
              ConsoleLogger().info("Using auto-generated Build object")
              pom.project.settings
            } else {
              Seq()
            }

	  case false =>
            ConsoleLogger().info("POM not found at " + pomFile.getAbsolutePath())
            Seq()
	}
    }

    res
  }
}
