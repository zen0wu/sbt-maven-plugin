package com.nil2.sbtmaven

import sbt._

object MavenPlugin extends Plugin {
  def init(path: String): Seq[Setting[_]] =
    new Pom(path, "pom.xml", None).project.settings

  override val settings: Seq[Setting[_]] = {
    val pom = new Pom(".")
    val singleModule = pom.modules.isEmpty
    val autoSettings = singleModule && pom.properties("single.module") != Some(false)
    // If this is a multiple module pom, we won't do anything
    if (autoSettings) {
      println("Importing default settings, since it's a single-module project")
      pom.project.settings
    }
    else {
      println("Assuming this is a Multi-module project")
      Seq()
    }
  }
}
