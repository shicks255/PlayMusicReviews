name := """PlayMusicReviews"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.8"

resolvers += Resolver.mavenLocal

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "org.playframework.anorm" %% "anorm" % "2.6.0"
libraryDependencies += jdbc
libraryDependencies += evolutions
libraryDependencies += "org.postgresql" % "postgresql" % "9.4-1206-jdbc4"
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.39"
libraryDependencies += "com.steven.hicks" % "LastFM_API" % "1.1"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
