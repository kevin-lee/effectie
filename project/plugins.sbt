logLevel := sbt.Level.Warn

addSbtPlugin("com.github.sbt" % "sbt-ci-release" % "1.11.2")
addSbtPlugin("org.wartremover" % "sbt-wartremover" % "3.4.0")

addSbtPlugin("ch.epfl.scala" % "sbt-scalafix"  % "0.10.4")
addSbtPlugin("org.scalameta" % "sbt-scalafmt"  % "2.4.6")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "2.4.0")
addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.3.2")
addSbtPlugin("org.scalameta" % "sbt-mdoc"      % "2.3.2")
addSbtPlugin("io.kevinlee"   % "sbt-docusaur"  % "0.18.0")

addSbtPlugin("org.scala-js"       % "sbt-scalajs"              % "1.18.2")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.3.2")

val sbtDevOopsVersion = "3.3.1"
addSbtPlugin("io.kevinlee" % "sbt-devoops-scala"     % sbtDevOopsVersion)
addSbtPlugin("io.kevinlee" % "sbt-devoops-sbt-extra" % sbtDevOopsVersion)
addSbtPlugin("io.kevinlee" % "sbt-devoops-github"    % sbtDevOopsVersion)

addSbtPlugin("io.kevinlee" % "sbt-devoops-starter"    % sbtDevOopsVersion)

addSbtPlugin("org.jetbrains.scala" % "sbt-idea-compiler-indices" % "1.0.13")
