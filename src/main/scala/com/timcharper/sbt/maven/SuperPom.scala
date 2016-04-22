package com.timcharper.sbt.maven

import scala.xml._

// A hardcoded pom.xml
object SuperPom {

  val build = """
    <project>
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
    </project>
  """

  def projectXml(baseDir: String): Node = {
    val xmlStr = build.replace("${project.basedir}", baseDir)
    XML.loadString(xmlStr).head
  }
}
