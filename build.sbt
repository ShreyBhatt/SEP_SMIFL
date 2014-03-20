name := "SMIFL"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "securesocial" %% "securesocial" % "master-SNAPSHOT",
  "mysql" % "mysql-connector-java" % "5.1.29"
)

resolvers ++= Seq(
  Resolver.url("sbt-plugin-snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns)
)

play.Project.playJavaSettings
