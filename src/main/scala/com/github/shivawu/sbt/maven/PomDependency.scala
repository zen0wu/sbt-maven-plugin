package com.github.shivawu.sbt.maven

import sbt._

case class PomDependency(
  val groupId: String, 
  val name: String, 
  val version: Option[String],
  val scope: Option[String] = None,
  val classifier: Seq[String] = Nil,
  val exclusions: Seq[(String, String)] = Nil

) {
  def id = groupId + ":" + name

  def toDependency = {
    val v = version.getOrElse { throw new RuntimeException("Maven version is empty for dependency ${groupId} / ${name}; sbt does not support version-less dependencies") }
    val depWithoutScope = groupId % name % version.get
    val dep = scope.map(depWithoutScope % _).getOrElse(depWithoutScope)
    val classified = (dep /: classifier)((moduleId, clf) => moduleId classifier clf)
    (classified /: exclusions){
      case (dep, (exOrg, exName)) => dep.exclude(exOrg, exName)
    }
  }

  override def toString = id + ":" + version
}

class DependencySet(val list: Seq[PomDependency]) {
  import collection._

  private val gnMap = 
    mutable.Map[String, PomDependency]() ++ list.groupBy(_.id).mapValues{ _.head }

  def lookup(pd: PomDependency): Option[PomDependency] =
    gnMap.get(pd.id)

  def lookup(groupId: String, name: String): Option[PomDependency] = 
    gnMap.get(groupId + ":" + name)

  def lookup(id: (String, String)): Option[PomDependency] =
    lookup(id._1, id._2)
}
