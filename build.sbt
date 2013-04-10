sbtPlugin := true

name := "sbt-maven-plugin"

organization := "com.github.shivawu"

version := "0.1.2-SNAPSHOT"

publishMavenStyle := true

scalaVersion := "2.9.2"

scalacOptions <<= scalaVersion map { v: String =>
  val default = Seq("-unchecked", "-deprecation")
  if (v startsWith "2.10")
    default :+ "-feature" :+ "-language:implicitConversions"
  else
    default
}

crossScalaVersions := Seq("2.9.1", "2.9.2", "2.10.0")

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

homepage := Some(url("https://github.com/shivawu/sbt-maven-plugin"))

licenses := Seq("Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))

pomExtra := (
  <scm>
    <url>git@github.com:shivawu/sbt-maven-plugin.git</url>
    <connection>scm:git:git@github.com:shivawu/sbt-maven-plugin.git</connection>
  </scm>
  <developers>
    <developer>
      <id>shivawu</id>
      <name>Shiva Wu</name>
    </developer>
  </developers>
)

