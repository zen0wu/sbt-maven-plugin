package com.not2.sbtmaven

import sbt._

class PomDependency(
  val groupId: String, 
  val name: String, 
  val version: String, 
  val scope: Option[String] = None,
  val classifier: Seq[String] = Nil
) {
  def id = groupId + ":" + name

  def toDependency = {
    val d = 
      if (scope == None) groupId % name % version
      else groupId % name % version % scope.get
    (d /: classifier)((d, clf) => d classifier clf)
  }
}

class DependencySet(val list: Seq[PomDependency]) {
  private val gnMap: Map[String, PomDependency] = 
    list.groupBy(_.id)
      .mapValues{ ds: Seq[PomDependency] => 
        require(ds.length == 1, "Each dependency should have unique groupId:artifactId")
        ds.head
      }

  def lookup(pd: PomDependency): Option[PomDependency] =
    gnMap.get(pd.id)

  def lookup(groupId: String, name: String): Option[PomDependency] = 
    gnMap.get(groupId + ":" + name)

  def lookup(id: (String, String)): Option[PomDependency] =
    lookup(id._1, id._2)
}
