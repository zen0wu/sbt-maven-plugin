package com.no2.sbtmaven

import sbt.{Build, Project, Setting}

trait SelectorDSL extends Build { this: SelectorFactory =>
  var projectBuffer = super.projects

  override def projects = projectBuffer.toList

  def project(selector: String => Boolean)(pSettings: Setting[_]*) {
    projectBuffer = projectBuffer.map { p: Project =>
      if (selector(p.id)) p.settings(pSettings: _*)
      else p
    }
  }
}

sealed trait SelectorFactory {
  def produce(s: String): (String => Boolean)
}

trait OrSelector extends (String => Boolean) {
  def |(s: String): OrSelector
}

trait OrFactory extends SelectorFactory {
  override abstract def produce(s: String): OrSelector = {
    val superFactory = super.produce _
    new OrSelector {
      val selectors = collection.mutable.ListBuffer[String => Boolean](superFactory(s))

      def |(s: String) = {
        selectors += superFactory(s)
        this
      }

      def apply(s: String): Boolean = (false /: selectors.map(_.apply(s)))(_ || _)
    }
  }
}

trait GlobFactory extends SelectorFactory {
  def produce(pattern: String): (String => Boolean) = {
    val regex = ("^" + pattern.map {
      case '*'  => ".*"
      case '?'  => "."
      case '.'  => "\\."
      case '\\' => "\\\\"
      case x: Char => "" + x
    }.mkString + "$").r
    (regex findFirstIn _) andThen (_ != None)
  }
}
