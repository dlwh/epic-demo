import AssemblyKeys._ // put this at the top of the file

name := "epic-demo"

version := "0.1-SNAPSHOT"

organization := "org.scalanlp"

scalaVersion := "2.11.2"

resolvers ++= Seq(
  "ScalaNLP Maven2" at "http://repo.scalanlp.org/repo",
  "Scala Tools Snapshots" at "http://scala-tools.org/repo-snapshots/",
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
)

libraryDependencies ++= Seq(
  "org.scalanlp" %% "epic" % "0.2",
  "org.scalanlp" %% "epic-parser-en-span" % "2014.9.15",
  "org.scalanlp" %% "epic-ner-en-conll" % "2014.10.26",
  "junit" % "junit" % "4.5" % "test"
)


credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")


scalacOptions ++= Seq("-deprecation", "-language:_", "-optimize")

javaOptions += "-Xmx2g"

seq(assemblySettings: _*)

assemblyOption in assembly ~= { _.copy(cacheOutput = false) }

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
{
  case PathList("org", "w3c", "dom", _) => MergeStrategy.first
  case PathList("javax", "xml", "stream", _ *) => MergeStrategy.first
  case PathList("org", "cyberneko", "html", _ *) => MergeStrategy.first
  case x => old(x)
}
}

excludedJars in assembly <<= (fullClasspath in assembly) map { cp =>
 cp filter {x => x.data.getName.matches(".*native.*") || x.data.getName.matches("sbt.*") || x.data.getName.matches(".*macros.*") }
}


