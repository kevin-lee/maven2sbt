logLevel := sbt.Level.Warn

addSbtPlugin("org.foundweekends" % "sbt-bintray" % "0.5.5")

addSbtPlugin("org.wartremover" % "sbt-wartremover" % "2.4.13")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.1")

addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.2.7")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.8.1")

addSbtPlugin("io.kevinlee" % "sbt-devoops" % "2.1.0")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.10.0")

addSbtPlugin("io.kevinlee" % "sbt-docusaur" % "0.4.0")

addSbtPlugin("ch.epfl.lamp" % "sbt-dotty" % "0.5.3")
