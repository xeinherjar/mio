val pekkoVersion = "1.0.+"
val pekkoHttpVersion = "1.0.+"
val circeVersion = "0.14.1"

addCommandAlias("pretty", ";scalafixAll;scalafmtAll")

lazy val root = project
  .in(file("."))
  .settings(
    name := "mio",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "3.4.2",
    ThisBuild / evictionErrorLevel := Level.Info,
    semanticdbEnabled := true, // enable SemanticDB
    Compile / scalacOptions ++= Seq(
      "-Wunused:imports",
    ),
    libraryDependencies ++= Seq(
      "org.apache.pekko" %% "pekko-actor-typed" % pekkoVersion,
      "org.apache.pekko" %% "pekko-actor-testkit-typed" % pekkoVersion,
      "org.apache.pekko" %% "pekko-stream" % pekkoVersion,
      "org.apache.pekko" %% "pekko-http" % pekkoHttpVersion,
      "ch.qos.logback" % "logback-classic" % "1.5.3",
      "org.bouncycastle" % "bcprov-jdk18on" % "1.77",
      // "de.heikoseeberger" %% "akka-http-circe" % "1.40.+",
      "com.github.pjfanning" %% "pekko-http-circe" % "2.6.+",
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "org.typelevel" %% "cats-core" % "2.12.0",
      "org.typelevel" %% "cats-parse" % "0.3.9",
      "org.scalameta" %% "munit" % "0.7.29" % Test,
    )
  )
