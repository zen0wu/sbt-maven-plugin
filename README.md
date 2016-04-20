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
addSbtPlugin("com.timcharper" % "sbt-maven-plugin" % "0.1.3-RC2")
```

Or go the hard way, add the following code to `project/project/Plugins.scala`

```scala
import sbt._
import Keys._

object Plugins extends Build {
  lazy val root = Project("root", file(".")).settings(
    addSbtPlugin("com.timcharper" % "sbt-maven-plugin" % "0.1.3-RC2")
  )
}
```

### Single module project

By default, the `pom.xml` in the same build is loaded, parsed, and made
available to sbt as `pom`. However, the settings must be included into your
project. You can do that by creating a single `build.sbt` file, and adding
`settingsFromMaven` to it. You can also access an entire project definition
itself, like this:

```
val root = pom.project
```

### Multi module project

If you have a multi-module pom file, then create a `build.sbt` file in the same
folder containing your multi-module `pom.xml`.

For example, if your `pom.xml` contains the following modules:

```
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <!-- ... -->
  <modules>
    <module>super-duper-core</module>
    <module>super-duper-app</module>
  </modules>
  <!-- ... -->
```

Also, say `super-duper-app` depends on `super duper-core`.


Multi-module projects can be accessed in `build.sbt`. If you wish to use all of the settings, vanilla, simply declare them as follows:

```
lazy val `super-duper-app` = pom.modules("super-duper-app").project.
  setting() // additional settings can go here

/* `super-duper-app` project will be automatically configured to depend on an
   sbt project with id `super-duper-core`; naming the following project as
   follows fulfills this dependency, which will otherwise result in error */

lazy val `super-duper-core` = pom.modules("super-duper-core").project
```

`pom.modules("super-duper-app")` returns a Pom class. If you need to further customize the project configuration, `projectSettings` and `dependencies` are available methods on the Pom class. `dependencies.list` returns a `List[PomDependency]`, and `PomDependency` has a method `.toDependency` which returns an SBT dependency. For further help, post an issue, or read the source.

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

This project has been forked from https://github.com/shivawu/sbt-maven-plugin by Tim Harper (https://github.com/timcharper), because it was abandoned.
