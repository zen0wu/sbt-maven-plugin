package com.nil2.sbtmaven

import java.io.File
import xml._
import sbt._

object MavenSettings {
  val settingsXml: Option[NodeSeq] = {
    val file = sys.props("user.home") + "/.m2/settings.xml"
    if (new File(file).exists()) XML.loadFile(file) \\ "settings" headOption
    else None
  }

  /* 
   * I'm not sure why Resolver.file(, file()) and Resolver.url(, url())
   * doesn't work
   */
  private def resolveLocalRepository(settings: NodeSeq): Seq[Resolver] = 
    settings.flatMap(_ \ "localRepository" headOption)
      .map(_.text)
      .map(repo => "maven-local" at new File(repo).toURI.toString)
  private def resolveMirrors(settings: NodeSeq): Seq[Resolver] =
    settings.flatMap(_ \ "mirrors" \ "mirror")
      .map(n => (n \ "id" text, n \ "url" text))
      .map { case (id, url) => id at url }

  lazy val resolvers: Seq[Resolver] = Seq(
    settingsXml map resolveLocalRepository getOrElse Nil,
    settingsXml map resolveMirrors getOrElse Nil
  ).flatten
}
