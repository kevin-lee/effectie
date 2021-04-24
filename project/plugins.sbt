import sbt.Defaults.sbtPluginExtra

logLevel := sbt.Level.Warn

addSbtPlugin("com.geirsson" % "sbt-ci-release" % "1.5.7")

libraryDependencies ++= {
  if (scalaVersion.value.startsWith("3.0")) {
    List.empty[ModuleID]
  } else {
    val sbtV   = (pluginCrossBuild / sbtBinaryVersion).value
    val scalaV = (update / scalaBinaryVersion).value
    List(sbtPluginExtra("org.wartremover" % "sbt-wartremover" % "2.4.13", sbtV, scalaV))
  }
}

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.0")
addSbtPlugin("org.scoverage" % "sbt-coveralls" % "1.2.7")
addSbtPlugin("io.kevinlee"   % "sbt-devoops"   % "2.2.0")
addSbtPlugin("org.scalameta" % "sbt-mdoc"      % "2.2.13")
addSbtPlugin("io.kevinlee"   % "sbt-docusaur"  % "0.4.0")
