package com.nil2.sbtmaven

import sbt._
import Keys._

object Common {
  val projectSettings: Seq[Setting[_]] = Seq(
    resolvers ++= MavenSettings.resolvers,
    credentials ++= MavenSettings.credentials
  )

  val publishSettings: Seq[Setting[_]] = Seq(
    publishMavenStyle := true,
    publishArtifact in Test := false
  )

  val javacOptions = Seq(
    "-source" -> "maven.compiler.source",  // javac -source
    "-target" -> "maven.compiler.target",  // javac -target
    "-encoding" -> "encoding"              // java -Dfile.encoding
  )

  val scalaLibrary = ("org.scala-lang", "scala-library")
}
