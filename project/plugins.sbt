import sbt.Defaults.sbtPluginExtra

logLevel := sbt.Level.Warn

addSbtPlugin("com.geirsson" % "sbt-ci-release" % "1.5.7")

libraryDependencies ++= {
  if (scalaVersion.value.startsWith("3.")) {
    List.empty[ModuleID]
  } else {
    val sbtV   = (pluginCrossBuild / sbtBinaryVersion).value
    val scalaV = (update / scalaBinaryVersion).value
    List(sbtPluginExtra("org.wartremover" % "sbt-wartremover" % "2.4.13", sbtV, scalaV))
  }
}

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.9.2")
addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.3.1")
addSbtPlugin("org.scalameta" % "sbt-mdoc"      % "2.2.22")
addSbtPlugin("io.kevinlee"   % "sbt-docusaur"  % "0.8.0")

val sbtDevOopsVersion = "2.11.0"
addSbtPlugin("io.kevinlee" % "sbt-devoops-scala"     % sbtDevOopsVersion)
addSbtPlugin("io.kevinlee" % "sbt-devoops-sbt-extra" % sbtDevOopsVersion)
addSbtPlugin("io.kevinlee" % "sbt-devoops-github"    % sbtDevOopsVersion)
