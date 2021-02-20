logLevel := sbt.Level.Warn

addSbtPlugin("org.foundweekends" % "sbt-bintray" % "0.5.5")

addSbtPlugin("org.wartremover" % "sbt-wartremover" % "2.4.13")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.0")

addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.2.7")

addSbtPlugin("io.kevinlee" % "sbt-devoops" % "2.0.0")

addSbtPlugin("org.scalameta" % "sbt-mdoc" % "2.2.13" )

addSbtPlugin("io.kevinlee" % "sbt-docusaur" % "0.4.0")

addSbtPlugin("ch.epfl.lamp" % "sbt-dotty" % "0.5.3")
