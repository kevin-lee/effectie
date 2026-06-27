logLevel := sbt.Level.Warn

addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.11.2")
addSbtPlugin("org.wartremover" % "sbt-wartremover" % "3.6.0")

addSbtPlugin("ch.epfl.scala" % "sbt-scalafix"  % "0.14.7")
addSbtPlugin("org.scalameta" % "sbt-scalafmt"  % "2.6.1")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "2.4.4")

addSbtPlugin("org.scala-js"       % "sbt-scalajs"              % "1.20.2")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.3.2")

addSbtPlugin("org.scala-native" % "sbt-scala-native" % "0.5.10")
addSbtPlugin("org.portable-scala" % "sbt-scala-native-crossproject" % "1.3.2")

addSbtPlugin("org.scalameta" % "sbt-native-image" % "0.5.0")

val sbtDevOopsVersion = "3.5.1"
addSbtPlugin("io.kevinlee" % "sbt-devoops-scala"     % sbtDevOopsVersion)
addSbtPlugin("io.kevinlee" % "sbt-devoops-sbt-extra" % sbtDevOopsVersion)
addSbtPlugin("io.kevinlee" % "sbt-devoops-github"    % sbtDevOopsVersion)

addSbtPlugin("io.kevinlee" % "sbt-devoops-starter"    % sbtDevOopsVersion)

addSbtPlugin("org.jetbrains.scala" % "sbt-idea-compiler-indices" % "1.0.16")
