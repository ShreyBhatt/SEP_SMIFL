name := "SMIFL"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "securesocial" %% "securesocial" % "master-SNAPSHOT"
)     

resolvers ++= Seq(
  Resolver.url("sbt-plugin-snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns)
)

play.Project.playJavaSettings
