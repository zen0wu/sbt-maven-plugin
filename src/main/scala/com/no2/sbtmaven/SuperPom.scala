package com.not2.sbtmaven

import scala.xml._

object SuperPom {
  val build = """
    <build>
      <directory>${project.basedir}/target</directory>
      <outputDirectory>${project.build.directory}/classes</outputDirectory>
      <finalName>${project.artifactId}-${project.version}</finalName>
      <testOutputDirectory>${project.build.directory}/test-classes</testOutputDirectory>
      <sourceDirectory>${project.basedir}/src/main/java</sourceDirectory>
      <scriptSourceDirectory>src/main/scripts</scriptSourceDirectory>
      <testSourceDirectory>${project.basedir}/src/test/java</testSourceDirectory>
      <resources>
        <resource>
          <directory>${project.basedir}/src/main/resources</directory>
        </resource>
      </resources>
      <testResources>
        <testResource>
          <directory>${project.basedir}/src/test/resources</directory>
        </testResource>
      </testResources>
    </build>
  """

  def projectXml(pom: Pom): NodeSeq = {
    val xmlStr = build.replace("${project.basedir}", pom.baseDir)
      .replace("${project.build.directory}", pom.baseDir)
      .replace("${project.artifactId}", pom.artifactId)
      .replace("${project.version}", pom.ver)
    XML loadString xmlStr
  }
}
