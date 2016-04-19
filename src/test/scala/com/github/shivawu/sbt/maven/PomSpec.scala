package com.github.shivawu.sbt.maven

import java.io.File
import org.scalatest.{FunSpec, Matchers}

class PomSpec extends FunSpec with Matchers {
  val pomFixtureFile =  new File("src/test/resources/fixtures/test-proj/pom.xml")
  val pomFixture = Pom(pomFixtureFile)

  describe("Pom parsing") {
    it("parses and interprets properties properly") {
      pomFixture.properties("spark.version") shouldBe (Some("1.5.0-3.6.0"))
    }

    it("parses and interprets dependencies properly") {
      pomFixture.dependencies.list shouldBe (
        List(
          PomDependency("org.apache.spark", "spark-core_2.10",     Some("1.5.0-3.6.0"), Some("provided")),
          PomDependency("org.apache.spark", "spark-assembly_2.10", Some("1.5.0-3.6.0"), Some("provided")),
          PomDependency("org.apache.spark", "spark-sql_2.10",      Some("1.5.0-3.6.0"), Some("provided")),
          PomDependency("org.scala-lang",   "scala-library",       Some("2.10.4"),      Some("provided")),
          PomDependency("org.apache.spark", "spark-hive_2.10",     Some("1.5.0-3.6.0"), Some("provided")),
          PomDependency("com.github.scopt", "scopt_2.10",          Some("3.3.0"))))
    }
  }
}
