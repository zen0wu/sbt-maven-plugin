package com.github.shivawu.sbt.maven.property

import scala.xml._
import com.github.shivawu.sbt.maven.MavenSettings

class PomProperty(
  kvs: Map[String, String], 
  parent: Option[PomProperty], 
  pom: NodeSeq,
  superPom: NodeSeq
) {

  private val resolvers: Seq[String => Option[String]] = Seq(
    Some(kvs.get _),
    parent.map(_.apply _),
    Some(resolvePredef _)
  ).flatten
  private val mainResolver: (String => String) = ResolveUtil.deepResolve(resolvers:_*) _
  def resolve(s: String): String = mainResolver(s)

  def apply(key: String): Option[String] = {
    val value = mainResolver("${" + key + "}")
    if (value startsWith "${") None
    else Some(value)
  }

  private val KeyPrefix = """([A-z]+\.)(.*)""".r
  val pomResolv = new XmlPropertyResolver(pom)
  val superPomResolv = new XmlPropertyResolver(superPom)
  val mavenSettingResolv = MavenSettings.settingsXml.map(new XmlPropertyResolver(_))
  private def resolvePredef(s: String): Option[String] = {
    s match {
      case KeyPrefix("settings.", key) => mavenSettingResolv.flatMap(_ apply key)
      case KeyPrefix("project.", key)  => pomResolv(key).orElse(superPomResolv(key))
      case KeyPrefix("env.", key)      => Option(System.getenv(key))
      case _ if sys.props.contains(s)  => Some(sys.props(s))
      case _                           => None
    }
  }
}
