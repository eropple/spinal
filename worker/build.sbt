name := """spinal-worker"""

libraryDependencies ++= Seq(
  // akka (because why not)
  "com.typesafe.akka" %% "akka-actor" % "2.3.4",
  "com.typesafe.akka" %% "akka-slf4j" % "2.3.4"
)