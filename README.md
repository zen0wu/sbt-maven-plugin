sbt-maven-plugin
================

Yoho! Finally, a sbt plugin which reads project definitions from `pom.xml`

Why this?
---------

Maven is great stuff, while its choice of XML as its descriptor doomed that it's not good at describing _behaviors_. 
Maven's solution is using plugins. Maven has so many plugins.

As more expressive language coming on stage, like Groovy or Scala, programmers can use predefined DSL to better define _behaviors_.
By using the neat DSLs which Gradle or SBT provides, we can easily implement build system tasks.

How about, `pom.xml` for the model, and `build.sbt` for the behavior? Sounds awesome?

I myself, wrote this plugin because Intelij IDEA has great Maven support, while plugins for others(Gradle, SBT) are all
3rd-party. And let's be honest, they suck. If you're using SBT with IDEA, you have to reload the project, whenever your
project definition changes, but with Maven, it's just a fast update. 

By using this plugin, you can just update your dependencies(or whatever) in `pom.xml`, and `reload` on your SBT console.
Cool!

Features
--------

* Project basic info(groupId, artifactId, version)
* Dependencies and dependencies management from parent pom, including classifier and scope
* Multiple module project with inter-project dependencies(module `a` depends on module `b`)
* Properties resolution and common properties support(encoding, source level, target level, for now)
* Set scala version according to dependency declarsion
* Read maven global repository settings from `~/.m2/settings.xml`
* Neat API for both single module and multi module project(See _Usage_ section)

Usage
-----

### Configuration and Development note

Note that this plugin is still under development. There're still couples of features I want to implement.
After I'm done, I'll figure out a way to upload to a public repository.
For now, just use the `git` repository here, by add the following to your `project/project/Plugins.scala`

```scala
import sbt._
import Keys._

object Plugins extends Build {
  lazy val root = Project("root", file(".")).dependsOn(
    uri("git://github.com/shivawu/sbt-maven-plugin.git")
  )
}
```

### Single module project

__See the configuration section above? Do that, and done.__ Just make sure there's a `pom.xml` in your current folder.

I made a selfish assumption that if you're using this plugin, you will assure that `pom.xml` is at the same 
folder with `project` or `build.sbt`. So, I wrote this plugin so that minimal effort is needed for users. 

This is achieved by override the `settings` field in `Plugin` trait and this settings will be add to all projects' settings automatically, 
which is not the best practise. Ideally, I should provide an option for the user to choose whether to do this, but haven't figure out a way.
For now, I just `guess`. If there exists modules definition in `pom.xml`, then it's a multi-module project. Otherwise, I'll do that without
confirmation.

### Multi module project

Due to `sbt`'s design, multi-module project can only be defined in `project/xxxBuild.scala`. Here, we use these settings:

```scala
import com.no2.sbtmaven.MavenBuild

object MyBuild extends MavenBuild {
	// "*" is a selector which selects all sub modules
	project("*")(
		// Note that these properties like compile source, target, encoding 
		// are treated as common properties. So set a pom.xml property 
		// like "<maven.compiler.source>1.6</maven.compiler.source>" also works.
		javacOptions ++= Seq("-source", "1.6")
	)

	project("a") (
		// Project specific settings here
	)

	project("b" | "c") (
		// Note that you can select multiple(but not all) modules using the "|" operator
		assemblySettings ++ Seq(
      		test in assembly := {}
      	)
	:_*) // Finally convert it to a Setting[_]*
}
```
