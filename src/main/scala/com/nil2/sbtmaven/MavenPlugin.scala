package com.nil2.sbtmaven

import sbt._

object MavenPlugin extends Plugin {
  def init(path: String): Seq[Setting[_]] =
    new Pom(path, "pom.xml", None).project.settings

  override val settings: Seq[Setting[_]] = {
    val pom = new Pom(".")
    // If this is a multiple module pom, we won't do anything
    if (!pom.modules.isEmpty)
      Seq()
    else 
      pom.project.settings
  }
}
