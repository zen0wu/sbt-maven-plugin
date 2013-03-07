package com.nil2.sbtmaven

import sbt._
import Keys._

object Common {
  val projectSettings: Seq[Setting[_]] = Seq(
    resolvers ++= MavenSettings.resolvers,
    credentials ++= MavenSettings.credentials,
    
    // Disable scala library since it's the default behavior of maven
    // scala-library will be added to libraryDependencies if it's declared as dependency in pom.xml
    autoScalaLibrary := false
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
