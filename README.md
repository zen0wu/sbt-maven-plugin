sbt-maven-plugin
================

Yoho! Finally, a SBT plugin for Maven

### You should fall in love with it

* Seamless mirgation from Maven to SBT, no rewriting in `build.sbt`, just import your `pom.xml`.  
  BTW, a single-module project means you don't have to do **anything** except adding the plugin
* Maven with SBT's awesome REPL
* Use SBT without losing the richness of Maven's plugins

Why made this?
--------------

Maven is great stuff, while its choice of XML as its descriptor doomed that it's not good at describing _behaviors_. 
Maven's solution is using plugins. Maven has so many plugins.

As more expressive language coming on stage, like Groovy or Scala, programmers can use predefined DSL to better define _behaviors_.
By using the neat DSLs which Gradle or SBT provides, we can easily implement build system *tasks*.

How about, `pom.xml` for the model, and `build.sbt` for the behavior? Sounds great?
Also, `pom.xml` and SBT's cool REPL at the same time!

I myself, wrote this plugin because Intelij IDEA has great Maven support, while plugins for others(Gradle, SBT) are all
3rd-party. And let's be honest, they suck. If you're using SBT with IDEA, you have to reload the project, whenever your
project definition changes, but with Maven, it's just a fast update. 

By using this plugin, you can just update your dependencies(or whatever) in `pom.xml`, and `reload` on your SBT console.
Cool!

Usage
-----

### Configuration

Add the following to `project/plugins.sbt`
```scala
addSbtPlugin("com.github.shivawu" % "sbt-maven-plugin" % "0.1.3-SNAPSHOT")

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
```

Or go the hard way, add the following code to `project/project/Plugins.scala`

```scala
import sbt._
import Keys._

object Plugins extends Build {
  lazy val root = Project("root", file(".")).settings(
  	addSbtPlugin("com.github.shivawu" % "sbt-maven-plugin" % "0.1.3-SNAPSHOT")
	resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

  )
}
```

### Single module project

__See the configuration section above? Do that, and done.__ Just make sure there's a `pom.xml` in your current folder.

I assume that if you're using this plugin, you will assure that `pom.xml` is at the same 
folder with `project` or `build.sbt`. So, this plugin is designed that minimal effort is needed for users. 

This is achieved by override the `settings` field in `Plugin` trait and this settings will be add to all projects' settings automatically.

This default behavior will be disabled **only when** there is module definition in `pom.xml` 
or any `MavenBuild` is instantiated from `.scala` build definition. (Here we took the advantages of `.scala` def is executed before importing `.sbt` def)

### Multi module project

Due to `sbt`'s design, multi-module project can only be defined in `project/???Build.scala`. Here, we use these settings:

```scala
import com.github.shivawu.sbt.maven.MavenBuild // Sorry for the long package name :-(

object MyBuild extends MavenBuild {
	// "*" is a selector which selects all modules
	project("*")(
		// Note that these properties like compile source, target, encoding 
		// are treated as common properties. So set a pom.xml property 
		// like "<maven.compiler.source>1.6</maven.compiler.source>" also works.
		javacOptions ++= Seq("-source", "1.6")
	)

	// Here "a" is a project id, which is set to the artifactId
	// BUT! SBT doesn't allow "." in the id, so the "." is replaced with "_"
	project("a") (
		// Project specific settings here
	)

	// Note that you can select multiple(but not all) modules using the "|" operator
	project("b" | "c") (
		assemblySettings ++ Seq(
      		test in assembly := {}
      	)
	:_*) // Finally convert it to a Setting[_]*
}
```

Features
--------

* Project basic info(groupId, artifactId, version)
* Neat API for both single module and multi module project(See _Usage_ section)
* Dependencies and dependencies management from parent pom, including classifier, scope and exclusion
* Multiple module project with inheritance and inter-project dependencies(module `a` depends on module `b`)
* Properties resolution and common properties support(encoding, source level, target level, for now)
* POM extra info(licenses, developers, ...)
* Set scala version according to dependency declarsion
* Read maven global repository settings from `~/.m2/settings.xml`

Licenses
--------
Copyright 2012 Shiva Wu

Licensed under _Apache License, Version 2.0_. You may obtain a copy of the license in the _LICENSE_ file, or at:

[http://www.apache.org/licenses/LICENSE-2.0]()

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
