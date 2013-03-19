package com.github.shivawu.sbt.maven.property

object ResolveUtil {
  private val KeyPattern = """\$\{([A-z0-9\.-]+)\}""".r

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

  def deepResolve(resolvers: (String => Option[String])*)(s: String): String = {

    def rec(s: String, stk: Set[String]): String = {
      val keys = findAllKeys(s)
      keys.foreach { k => if (stk contains k) sys.error("Cyclic dependencies among properties [" + stk.mkString(", ") + "]") }
      val kvs: Iterable[(String, String)] = keys.map { k: String => 
        resolvers.view.map(_.apply(k)).reduceLeft(_ orElse _)
          .map(rec(_, stk + k))
          .map((k -> _))
      }.flatten

      kvs.foldLeft(s) {
        case (s: String, (k: String, v: String)) => s.replace("${" + k + "}", v) 
      }
    }

    rec(s, Set[String]())
  }
}

import scala.xml._

class XmlPropertyResolver(xml: NodeSeq) extends (String => Option[String]) {
	override def apply(key: String) = 
		findNode(xml, key).headOption.map(_.text)

  private def findNode(root: NodeSeq, path: String): NodeSeq = 
    path.split("\\.").foldLeft(root)(_ \ _)
}
