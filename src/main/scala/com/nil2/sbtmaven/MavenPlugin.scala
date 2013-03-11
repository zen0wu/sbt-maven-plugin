package com.nil2.sbtmaven

import sbt._

object MavenPlugin extends Plugin {
  override val settings: Seq[Setting[_]] = {
    val res: Seq[Setting[_]] = 
      if (MavenBuild.isInstantiated) {
        // User has defined a MavenBuild object
        // So we should not import pom.xml here
        ConsoleLogger().info("Disable auto-importing pom.xml, use user-defined Build instead")
        Seq()
      }
      else {
        val pom = new Pom(".")
        val noModuleDef = pom.modules.isEmpty
        // If this is a multiple module pom, we won't do anything
        if (!noModuleDef) {
          // Here we should use log.info, but I don't know how
          // UPDATE: I figured it out!! Use ConsoleLogger().info
          //         SBT's documentation and design is unbelievable!
          ConsoleLogger().info("Use pom.xml automatically")
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
