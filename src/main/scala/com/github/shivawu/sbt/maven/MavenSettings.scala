package com.github.shivawu.sbt.maven

import java.io.File
import xml._
import sbt._
import java.net.URL

object MavenSettings {
  val settingsXml: Option[NodeSeq] = {
    val file = sys.props("user.home") + "/.m2/settings.xml"
    if (new File(file).exists()) {
      ConsoleLogger().info("Loading maven settings from [" + file + "]")
      (XML.loadFile(file) \\ "settings").headOption
    }
    else None
  }

  val servers: Map[String, (String, String)] =
    settingsXml.toList.flatMap(_ \ "servers" \ "server").map{
      server =>
        (server \ "id").text -> ((server \ "username").text, (server \ "password").text)
    }.toMap

  /*
   * I'm not sure why Resolver.file(, file()) and Resolver.url(, url())
   * doesn't work
   */
  private def resolveLocalRepository(settings: NodeSeq): Seq[Resolver] =
    settings.flatMap(n => (n \ "localRepository").headOption)
      .map(_.text)
      .map(repo => "maven-local" at new File(repo).toURI.toString)
  private def resolveMirrors(settings: NodeSeq): Seq[Resolver] =
    settings.flatMap(_ \ "mirrors" \ "mirror")
      .map(n => ((n \ "id").text, (n \ "url").text))
      .map { case (id, url) => id at url }

  lazy val resolvers: Seq[Resolver] = Seq(
      settingsXml map resolveLocalRepository getOrElse Nil,
      settingsXml map resolveMirrors getOrElse Nil
    ).flatten

  /**
   * LIMITATION:
   * - The ID of server in settings.xml should be same with REALM of http basic auth
   * - No problem with getting host name from url ? (ivy uses host name not url unlike mvn)
   */
  lazy val credentials: Seq[Credentials] =
    resolvers.collect{
      case MavenRepository(name, url) if servers.contains(name) => {
        val (user, pw) = servers(name)
        Credentials(name, new URL(url).getHost, user, pw)
      }
    }
}
