package com.timcharper.sbt.maven

import sbt._

object MavenPlugin extends AutoPlugin {
  object autoImport {
    val pom = Pom(new java.io.File("./pom.xml"))

    lazy val settingsFromMaven: Seq[Setting[_]] = {
      if (pom.modules.nonEmpty)
        throw new RuntimeException("Multiple module pom detected; settings are not available")

      ConsoleLogger().info("Use auto-generated Build object")
      val res: Seq[Setting[_]] = pom.project.settings
      ConsoleLogger().success("POM definition loaded")
      res
    }
  }
}
