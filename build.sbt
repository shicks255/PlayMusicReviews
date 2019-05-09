name := """PlayMusicReviews"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, LauncherJarPlugin)

scalaVersion := "2.12.8"

maintainer := "shicks255@yahoo.com"

resolvers += Resolver.mavenLocal
resolvers += Resolver.mavenCentral
resolvers += "jbcrypt repo" at "http://mvnrepository.com/"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "org.playframework.anorm" %% "anorm" % "2.6.0"
libraryDependencies += jdbc
libraryDependencies += evolutions
libraryDependencies += "org.postgresql" % "postgresql" % "9.4-1206-jdbc4"
libraryDependencies += "com.steven.hicks" % "MusicAPI" % "1.1"
libraryDependencies += ehcache
libraryDependencies += "org.mindrot" % "jbcrypt" % "0.3m"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
