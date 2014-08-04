name := """spinal-commons"""

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.7",
  "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
//
  "com.typesafe.play" %% "play-json" % "2.3.2",
//
  "com.twitter" %% "util-eval" % "6.18.0",
//
  "org.rogach" %% "scallop" % "0.9.5",
// akka (because why not)
  "com.typesafe.akka" %% "akka-actor" % "2.3.4",
  "com.typesafe.akka" %% "akka-slf4j" % "2.3.4"
)
