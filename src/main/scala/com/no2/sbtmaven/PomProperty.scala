package com.not2.sbtmaven

import scala.xml._

class PomProperty(
  kvs: Map[String, String], 
  parent: Option[PomProperty], 
  projectXml: => NodeSeq       // Note the laziness here
) {

  import PropertyUtil.deepResolve

  val resolve = deepResolve(apply _) _

  def apply(s: String): Option[String] = 
    kvMap.get(s).orElse(resolvePredef(s))

  val kvMap = collection.mutable.Map[String, String]()
  kvMap ++= parent.map(_.kvMap).flatten
  kvMap ++= {
    val resolve = deepResolve(apply _, kvs.get _) _  // Note that the apply() will look the current kvMap, which includes parent properties
    for ((k, v) <- kvs) yield (k -> resolve(v))
  }

  private lazy val KeyPrefix = """([A-z]+\.)(.*)""".r
  private def resolvePredef(s: String): Option[String] = {
    s match {
      case KeyPrefix("settings.", key) =>
        MavenSettings.settingsXml.map(findNode(_, key)).headOption.map(_.text)
      case KeyPrefix("project.", key) => 
        findNode(projectXml, key).headOption.map(_.text)
      case KeyPrefix("env.", key) => 
        Option(System.getenv(key))
      case _ if sys.props.contains(s) => 
        Some(sys.props(s))
      case _ => 
        None
    }
  }

  private def findNode(root: NodeSeq, path: String): NodeSeq = 
    path.split(".").foldLeft(root)(_ \ _)
}

object PropertyUtil {
  private val KeyPattern = """\$\{([A-z\.]+)\}""".r

  def deepResolve(resolvers: (String => Option[String])*)(s: String): String = {
    def findAllKeys(s: String): List[String] = {
      val iter = KeyPattern.findAllIn(s)
      val res = collection.mutable.ListBuffer[String]()
      while (iter.hasNext) { 
        iter.next
        val s = iter.group(1)
        val be = iter.before
        // escape $$
        // Note: Scala 2.10.0 has a findAllMatchesIn method, which suits here perfectly.
        // But we cannot use it, since 2.10.0 just released.
        if (be.length == 0 || be.charAt(be.length - 1) != '$') 
          res += s
      }
      res.toList
    }

    def rec(s: String, stk: Set[String]): String = {
      val keys = findAllKeys(s)
      keys.foreach { k => if (stk contains k) sys.error("Cyclic dependencies between properties") }
      val kvs: Iterable[(String, String)] = keys.map { k: String => 
        resolvers.view.map(_.apply(k)).reduceLeft(_ orElse _)
          .map(rec(_, stk + k))
          .map((k -> _))
      }.flatten

      kvs.foldLeft(s) {
        case (s: String, (k: String, v: String)) => s.replace("${" + k + "}", v) 
      }
    }

    // Here's a problem: if you're resolving a value on (k, v), the k should be the first one in stack
    // But that's just a matter of time for it to crash, if cyclic dependencies really exist.
    rec(s, Set[String]())
  }
}
