package com.no2.sbtmaven

import java.io.File
import xml._

import sbt._
import Keys._

class Pom(val baseDir: String, val pomFile: String = "pom.xml", val parent: Option[Pom] = None) { self =>
  require(new File(baseDir + "/" + pomFile).exists, baseDir + "/" + pomFile + " doesn't exist")

  val xml = XML.loadFile(baseDir + "/" + pomFile) \\ "project"

  // There's a little trick here
  // There're cyclic dependencies between properties and (groupId, ver)
  // It happens because super pom needs version and groupId's value
  // But, we only need super pom when ran into ${project.xxx}, 
  // so super pom can be obtained lazily
  // We use a by-name parameter here to avoid NullPointerException
  val properties: PomProperty =
    new PomProperty({
        val x = xml \ "properties" headOption;
        if (x == None) Map()
        else Map() ++ 
          x.get.child.map(p => p.label -> p.text)
            .filterNot(_._1 == "#PCDATA")
      },
      parent.map(_.properties),
      SuperPom.projectXml(this)
    )

  // Basic info
  val groupId: String = getInheritedProperty(xml, "groupId", _.groupId)
  val artifactId: String = getInheritedProperty(xml, "artifactId", _.artifactId)
  val ver: String = getInheritedProperty(xml, "version", _.ver)

  // Dependencies
  val dependencies = 
    new DependencySet(xml \ "dependencies" \ "dependency" map ( n => parseDependency(n, parent) ))
  val dependencyManagement: DependencySet = 
    new DependencySet(xml \ "dependencyManagement" \ "dependency" map ( n => parseDependency(n, None) ))

  // Repositories
  val repositories: Seq[(String, String)] = 
    (xml \ "repositories" \ "repository") map (n => (n \ "id" text, n \ "url" text))

  // Scala version
  val scalaVer = dependencies.lookup(Common.scalaLibraryGroupId, Common.scalaLibraryArtifactId).map(_.version)

  // Modules
  val modules: Seq[Pom] = 
    xml \ "modules" \ "module" map { p: NodeSeq => new Pom(baseDir = p text, parent = Some(self)) }

  // SBT Models
  lazy val root: Pom = if (parent == None) self else parent.get.root
  lazy val allModules: List[Pom] = self :: modules.toList.flatMap(_.allModules)
  lazy val allProjects: List[Project] = project :: subProjects
  lazy val subProjects: List[Project] = modules.toList.flatMap(_.allProjects)

  lazy val project: Project = {
    val (indeps, exdeps): (List[Project], List[PomDependency]) = {
      val depPoms = root.allModules.filter(p => dependencies.lookup(p.groupId, p.artifactId) != None)
      val set = new DependencySet(depPoms.map(p => dependencies.lookup(p.groupId, p.artifactId)).flatten)
      (
        depPoms.map(_.project),
        dependencies.list.filter{ p => set.lookup(p) == None }
      )
    }

    val metadata: Seq[Setting[_]] = Common.commonProjectSettings ++ 
      scalaVer.map(scalaVersion := _).toList ++
      Seq(
        name := artifactId,
        organization := groupId,
        version := ver,
        javacOptions ++= Common.commonJavacOptions.foldLeft(List[String]()) { (opts: List[String], kv: (String, String)) =>
          val rv = properties apply kv._2
          if (rv == None) opts
          else kv._1 :: rv.get :: opts
        }
      )

    val bare = Project(
      id = artifactId, 
      base = new File(baseDir)
    ).settings(metadata: _*)
    val withSubprojs = (bare /: subProjects) (_ aggregate _)
    val withInterDeps = (withSubprojs /: indeps) (_ dependsOn _)
    withInterDeps.settings(
      libraryDependencies ++= exdeps map (_.toDependency),
      resolvers ++= repositories.map(r => r._1 at r._2)
    )
  }

  protected def getInheritedProperty(node: NodeSeq, name: String, fromParent: Pom => String): String = {
    val res = (node \ name).headOption.map(_.text).orElse(parent map(fromParent)).orNull
    require(res != null, "project [" + baseDir + "]'s " + name + " is null")
    properties resolve res
  }

  protected def parseDependency(node: NodeSeq, parent: Option[Pom]): PomDependency = {
    val (groupId, name) = (
      properties.resolve(node \ "groupId" text),
      properties.resolve(node \ "artifactId" text)
    )
    require(groupId != "", "groupId is empty")
    require(name != "", "artifactId is empty")

    val fallback: Option[PomDependency] = parent flatMap{ _.dependencyManagement.lookup(groupId, name) }
    val version = (node \ "version").headOption.map(_.text).map(properties resolve _).orElse(fallback.map(_.version))
    require(version != None, "version is empty, although with parent's dependency management")
    val scope = (node \ "scope").headOption.map(_.text).map(properties resolve _).orElse(fallback.map(_.version))
    val classifier = (node \ "classifier").map(_.text).toList
    new PomDependency(groupId, name, version.get, scope, classifier)
  }
}
