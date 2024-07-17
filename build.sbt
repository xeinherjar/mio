val akkaVersion = "1.0.+"
val akkaHttpVersion = "1.0.+"

lazy val root = project
  .in(file("."))
  .settings(
    name := "mio",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "3.4.2",
    libraryDependencies ++= Seq(
      "org.apache.pekko" %% "pekko-actor-typed" % akkaVersion,
      "org.apache.pekko" %% "pekko-stream" % akkaVersion,
      "org.apache.pekko" %% "pekko-http" % akkaHttpVersion,
      "org.apache.pekko" %% "pekko-http-spray-json" % akkaHttpVersion,
      "ch.qos.logback" % "logback-classic" % "1.5.3",
      "org.bouncycastle" % "bcprov-jdk18on" % "1.77",
      "org.scalameta" %% "munit" % "0.7.29" % Test,
    )
  )
