package com.no2.sbtmaven

import sbt._
import Keys._

object Common {
  val commonProjectSettings: Seq[Setting[_]] = Seq(
    autoScalaLibrary := false,
    resolvers ++= MavenSettings.resolvers
  )

  val commonJavacOptions = Seq(
    "-source" -> "maven.compiler.source",  // javac -source
    "-target" -> "maven.compiler.target",  // javac -target
    "-encoding" -> "encoding" // java -Dfile.encoding
  )

  val scalaLibraryGroupId = "org.scala-lang"
  val scalaLibraryArtifactId = "scala-library"
}
