package com.github.shivawu.sbt.maven

object PathUtil {
	import java.io.File

	def relativeTo(target: File, base: File = new File(".")) = {
		def toPathList(file: File) = {
			var f = file.getCanonicalFile
			val list = collection.mutable.ListBuffer[String]()
			while (f != null) {
				if (f.getName != "")
					list += f.getName
				f = f.getParentFile
			}
			list.reverse.toList
		}

		val pairs = toPathList(base).zipAll(toPathList(target), null, null).dropWhile(p => p._1 == p._2)
		val out = pairs.map(_._1).filter(_ != null).map(_ => "..")
		val in = pairs.map(_._2).filter(_ != null)
		(out ::: in).mkString("/")
	}

	// TODO: Windows path handling
	def chdir(path: String, base: String) = 
		if (path startsWith "/")
			path
		else
			base + "/" + path
}
