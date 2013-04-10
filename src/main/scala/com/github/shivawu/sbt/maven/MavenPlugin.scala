package com.github.shivawu.sbt.maven

import sbt._

object MavenPlugin extends Plugin {
  override val settings: Seq[Setting[_]] = {
    
    val res: Seq[Setting[_]] = MavenBuild.getInstanceClassName match {
      case Some(buildClass) => 
        // User has defined a MavenBuild object
        // So we should not import pom.xml here
        ConsoleLogger().info("Use user-defined Build class of [" + buildClass + "]")
        Seq()
      case None =>
        val pom = Pom(new java.io.File("./pom.xml"))
        val noModuleDef = pom.modules.isEmpty
        // If this is a multiple module pom, we won't do anything
        if (noModuleDef) {
          // Here we should use log.info, but I don't know how
          // UPDATE: I figured it out!! Use ConsoleLogger().info
          //         SBT's documentation and design is unbelievable!
          ConsoleLogger().info("Use auto-generated Build object")
          pom.project.settings
        }
        else {
          // This should not be reached
          Seq()
        }
    }
    ConsoleLogger().success("POM definition loaded")
    res
  }
}
