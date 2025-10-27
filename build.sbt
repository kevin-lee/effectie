import ProjectInfo.*
import just.semver.{Anh, Dsv, SemVer}
import SemVer.{Major, Minor, Patch}
import just.semver.AdditionalInfo.PreRelease
import kevinlee.sbt.SbtCommon.crossVersionProps
import sbtcrossproject.CrossProject

ThisBuild / scalaVersion := props.ProjectScalaVersion
ThisBuild / organization := "io.kevinlee"
ThisBuild / organizationName := "Kevin's Code"
ThisBuild / crossScalaVersions := props.CrossScalaVersions

ThisBuild / testFrameworks ~=
  (frameworks => (TestFramework("hedgehog.sbt.Framework") +: frameworks).distinct)

ThisBuild / developers := List(
  Developer(
    props.GitHubUsername,
    "Kevin Lee",
    "kevin.code@kevinlee.io",
    url(s"https://github.com/${props.GitHubUsername}"),
  )
)

ThisBuild / homepage := Some(url(s"https://github.com/${props.GitHubUsername}/${props.RepoName}"))
ThisBuild / scmInfo :=
  Some(
    ScmInfo(
      url(s"https://github.com/${props.GitHubUsername}/${props.RepoName}"),
      s"git@github.com:${props.GitHubUsername}/${props.RepoName}.git",
    )
  )
ThisBuild / licenses := props.licenses

ThisBuild / scalafixConfig := (
  if (scalaVersion.value.startsWith("3"))
    ((ThisBuild / baseDirectory).value / ".scalafix-scala3.conf").some
  else
    ((ThisBuild / baseDirectory).value / ".scalafix-scala2.conf").some
)

lazy val effectie = (project in file("."))
  .enablePlugins(DevOopsGitHubReleasePlugin)
  .settings(
    name := prefixedProjectName(""),
    description := "Effect Utils",
    libraryDependencies :=
      libraryDependenciesPostProcess(isScala3(scalaVersion.value), libraryDependencies.value),
    devOopsPackagedArtifacts += s"*/*/*/target/scala-*/${devOopsArtifactNamePrefix.value}*.jar",
  )
  .settings(noPublish)
  .aggregate(
    testing4CatsJvm,
    testing4CatsJs,
    coreJvm,
    coreJs,
    syntaxJvm,
    syntaxJs,
    catsJvm,
    catsJs,
    timeJvm,
    timeJs,
    timeCatsEffect2Jvm,
    timeCatsEffect2Js,
    timeCatsEffect3Jvm,
    timeCatsEffect3Js,
    catsEffect2Jvm,
    catsEffect2Js,
    catsEffect3Jvm,
    catsEffect3Js,
    monix3Jvm,
    monix3Js,
  )

lazy val core = module(ProjectName("core"), crossProject(JVMPlatform, JSPlatform))
  .settings(
    description := "Effect Utils - Core",
    libraryDependencies ++= List(
      libs.extrasCore.value % Test,
      libs.libCatsCore(props.catsVersion).value % Test,
    ) ++ (
      if (scalaVersion.value.startsWith("2.12"))
        List("org.scala-lang.modules" %% "scala-collection-compat" % "2.8.1")
      else
        List.empty
    ),
    libraryDependencies :=
      libraryDependenciesPostProcess(isScala3(scalaVersion.value), libraryDependencies.value),
  )
  .dependsOn(testing4Cats % Test)

lazy val coreJvm = core
  .jvm
  .settings(
    libraryDependencies ++= List(
      libs.tests.extrasConcurrent.value,
      libs.tests.extrasConcurrentTesting.value,
    )
  )
lazy val coreJs  = core
  .js
  .settings(jsSettingsForFuture)
  .settings(jsSettings)
  .settings(
    libraryDependencies ++= List(
      libs.tests.munit.value
    )
  )

lazy val syntax    = module(ProjectName("syntax"), crossProject(JVMPlatform, JSPlatform))
  .settings(
    description := "Effect Utils - Syntax",
    libraryDependencies ++= List(
      libs.libCatsCore(props.catsVersion).value,
      libs.tests.extrasConcurrent.value,
      libs.tests.extrasConcurrentTesting.value,
    ),
    libraryDependencies :=
      libraryDependenciesPostProcess(isScala3(scalaVersion.value), libraryDependencies.value),
  )
  .dependsOn(core % props.IncludeTest)
lazy val syntaxJvm = syntax.jvm
lazy val syntaxJs  = syntax
  .js
  .settings(jsSettings)

lazy val cats = module(ProjectName("cats"), crossProject(JVMPlatform, JSPlatform))
  .settings(
    description := "Effect Utils - Cats",
    libraryDependencies ++= List(
      libs.libCatsCore(props.catsVersion).value,
      libs.tests.extrasConcurrent.value,
      libs.tests.extrasConcurrentTesting.value,
    ),
    libraryDependencies :=
      libraryDependenciesPostProcess(isScala3(scalaVersion.value), libraryDependencies.value),
  )
  .dependsOn(
    core         % props.IncludeTest,
    syntax,
    testing4Cats % Test,
  )

lazy val catsJvm = cats.jvm
lazy val catsJs  = cats
  .js
  .settings(jsSettingsForFuture)
  .settings(jsSettings)

lazy val testing4Cats    = module(ProjectName("test4cats"), crossProject(JVMPlatform, JSPlatform))
  .settings(
    description := "Effect's test utils for Cats",
    libraryDependencies :=
      libraryDependencies.value ++ List(
        libs.libCatsCore(props.catsVersion).value
      ) ++ List(libs.hedgehogCore.value, libs.hedgehogRunner.value),
    libraryDependencies := libraryDependenciesPostProcess(isScala3(scalaVersion.value), libraryDependencies.value),
    console / initialCommands :=
      """import effectie.testing.cats._""",
  )
lazy val testing4CatsJvm = testing4Cats.jvm
lazy val testing4CatsJs  = testing4Cats
  .js
  .settings(jsSettings)
  .settings(
    libraryDependencies ++= List(
      libs.munit.value
    )
  )

lazy val time = module(ProjectName("time"), crossProject(JVMPlatform, JSPlatform))
  .settings(
    description := "Effect Utils - Time",
    libraryDependencies ++= List(
      libs.libCatsCore(props.catsVersion).value,
      libs.tests.extrasConcurrent.value,
      libs.tests.extrasConcurrentTesting.value,
    ) ++ (
      if (scalaVersion.value.startsWith("2.12"))
        List(libs.libCatsEffect(props.catsEffect2Version).value       % Test)
      else
        List(libs.libCatsEffect(props.catsEffect2LatestVersion).value % Test)
    ),
    libraryDependencies :=
      libraryDependenciesPostProcess(isScala3(scalaVersion.value), libraryDependencies.value),
  )
  .dependsOn(
    core         % props.IncludeTest,
    syntax,
    catsEffect2  % Test,
    testing4Cats % Test,
  )

lazy val timeJvm = time.jvm
lazy val timeJs  = time
  .js
  .settings(jsSettingsForFuture)
  .settings(jsSettings)
  .settings(
    libraryDependencies ++= List(
      libs.scalaJavaTime.value
    )
  )

lazy val timeCatsEffect2    = module(ProjectName("time-cats-effect2"), crossProject(JVMPlatform, JSPlatform))
  .settings(
    description := "Effect Utils - Time with Cats Effect 2",
    libraryDependencies :=
      (SemVer.parseUnsafe(scalaVersion.value) match {
        case SemVer(Major(2), Minor(11), _, _, _) =>
          libraryDependencies.value ++ Seq(libs.libCatsCore_2_0_0.value, libs.libCatsEffect_2_0_0.value)
        case SemVer(
              Major(3),
              Minor(0),
              Patch(0),
              Some(PreRelease(List(Dsv(List(Anh.Alphabet("RC"), Anh.Num("1")))))),
              _,
            ) =>
          libraryDependencies.value ++ Seq(
            libs.libCatsCore(props.catsVersion).value,
            libs.libCatsEffect(props.catsEffect2Version).value,
          )
        case x =>
          libraryDependencies.value ++ Seq(
            libs.libCatsCore(props.catsVersion).value,
            libs.libCatsEffect(props.catsEffect2LatestVersion).value,
          )
      }),
    libraryDependencies := libraryDependenciesPostProcess(isScala3(scalaVersion.value), libraryDependencies.value),
  )
  .dependsOn(
    core   % props.IncludeTest,
    syntax % props.IncludeTest,
    time,
  )
lazy val timeCatsEffect2Jvm = timeCatsEffect2.jvm
lazy val timeCatsEffect2Js  = timeCatsEffect2
  .js
  .settings(jsSettingsForFuture)
  .settings(jsSettings)
  .settings(
    libraryDependencies ++= List(
      libs.scalaJavaTime.value
    )
  )

lazy val timeCatsEffect3    = module(ProjectName("time-cats-effect3"), crossProject(JVMPlatform, JSPlatform))
  .settings(
    description := "Effect Utils - Time with Cats Effect 3",
    libraryDependencies ++= List(
      libs.libCatsCore(props.catsVersion).value,
      libs.libCatsEffect(props.catsEffect3Version).value,
      libs.libCatsEffectTestKit.value % Test excludeAll ("org.scalacheck"),
      libs.tests.extrasHedgehogCatsEffect3.value,
    ),
    libraryDependencies := libraryDependenciesPostProcess(isScala3(scalaVersion.value), libraryDependencies.value),
  )
  .dependsOn(
    core   % props.IncludeTest,
    syntax % props.IncludeTest,
    time,
  )
lazy val timeCatsEffect3Jvm = timeCatsEffect3.jvm
lazy val timeCatsEffect3Js  = timeCatsEffect3
  .js
  .settings(jsSettingsForFuture)
  .settings(jsSettings)
  .settings(
    libraryDependencies ++= List(
      libs.scalaJavaTime.value
    )
  )

lazy val catsEffect2    = module(ProjectName("cats-effect2"), crossProject(JVMPlatform, JSPlatform))
  .settings(
    description := "Effect Utils - Cats Effect 2",
    libraryDependencies :=
      (SemVer.parseUnsafe(scalaVersion.value) match {
        case SemVer(Major(2), Minor(11), _, _, _) =>
          libraryDependencies.value ++ Seq(libs.libCatsCore_2_0_0.value, libs.libCatsEffect_2_0_0.value)
        case SemVer(
              Major(3),
              Minor(0),
              Patch(0),
              Some(PreRelease(List(Dsv(List(Anh.Alphabet("RC"), Anh.Num("1")))))),
              _,
            ) =>
          libraryDependencies.value ++ Seq(
            libs.libCatsCore(props.catsVersion).value,
            libs.libCatsEffect(props.catsEffect2Version).value,
          )
        case x =>
          libraryDependencies.value ++ Seq(
            libs.libCatsCore(props.catsVersion).value,
            libs.libCatsEffect(props.catsEffect2LatestVersion).value,
          )
      }),
    libraryDependencies := libraryDependenciesPostProcess(isScala3(scalaVersion.value), libraryDependencies.value),
  )
  .dependsOn(
    core         % props.IncludeTest,
    syntax       % props.IncludeTest,
    cats         % props.IncludeTest,
    testing4Cats % Test,
  )
lazy val catsEffect2Jvm = catsEffect2.jvm
lazy val catsEffect2Js  = catsEffect2
  .js
  .settings(jsSettingsForFuture)
  .settings(jsSettings)

lazy val catsEffect3    = module(ProjectName("cats-effect3"), crossProject(JVMPlatform, JSPlatform))
  .settings(
    description := "Effect Utils - Cats Effect 3",
    libraryDependencies ++= List(
      libs.libCatsCore(props.catsVersion).value,
      libs.libCatsEffect(props.catsEffect3Version).value,
      libs.libCatsEffectTestKit.value % Test excludeAll ("org.scalacheck"),
      libs.tests.extrasHedgehogCatsEffect3.value,
    ),
    libraryDependencies := libraryDependenciesPostProcess(isScala3(scalaVersion.value), libraryDependencies.value),
    console / initialCommands :=
      """import effectie.cats._""",
  )
  .dependsOn(
    core         % props.IncludeTest,
    syntax       % props.IncludeTest,
    cats         % props.IncludeTest,
    testing4Cats % Test,
  )
lazy val catsEffect3Jvm = catsEffect3.jvm
lazy val catsEffect3Js  = catsEffect3
  .js
  .settings(jsSettingsForFuture)
  .settings(jsSettings)
  .settings(
    libraryDependencies ++= List(libs.tests.munitCatsEffect3.value)
  )

lazy val monix3    = module(ProjectName("monix3"), crossProject(JVMPlatform, JSPlatform))
  .settings(
    description := "Effect Utils - Monix 3",
    libraryDependencies :=
      crossVersionProps(
        List.empty,
        SemVer.parseUnsafe(scalaVersion.value),
      ) {
        case (Major(2), Minor(11), _) =>
          libraryDependencies.value ++ List(libs.libMonix3_3_0.value)
        case x =>
          libraryDependencies.value ++ List(libs.libMonix.value)
      },
    libraryDependencies := libraryDependenciesPostProcess(isScala3(scalaVersion.value), libraryDependencies.value),
  )
  .dependsOn(
    core         % props.IncludeTest,
    syntax       % props.IncludeTest,
    cats         % props.IncludeTest,
    catsEffect2  % props.IncludeTest,
    testing4Cats % Test,
  )
lazy val monix3Jvm = monix3.jvm
lazy val monix3Js  = monix3
  .js
  .settings(jsSettingsForFuture)
  .settings(jsSettings)

lazy val docs = (project in file("docs-gen-tmp/docs"))
  .enablePlugins(MdocPlugin, DocusaurPlugin)
  .settings(
    name := "docs",
    mdocIn := file("docs/latest"),
    mdocOut := file("generated-docs/docs"),
    cleanFiles += ((ThisBuild / baseDirectory).value / "generated-docs" / "docs"),
    scalacOptions ~= (_.filterNot(props.isScala3IncompatibleScalacOption).filter(opt => opt != "-Xfatal-warnings")),
    libraryDependencies ++= {
      val latestTag = getTheLatestTaggedVersion()
      List(
        "io.kevinlee" %% "effectie-cats-effect2" % latestTag,
        "io.kevinlee" %% "effectie-monix3"       % latestTag,
        libs.extrasCats.value,
        libs.extrasConcurrent.value,
      )
    },
    libraryDependencies := libraryDependenciesPostProcess(
      isScala3(scalaVersion.value),
      libraryDependencies.value,
    ),
    mdocVariables := {
      val latestVersion = getTheLatestTaggedVersion()

      val websiteDir        = docusaurDir.value
      val latestVersionFile = websiteDir / "latestVersion.json"
      val latestVersionJson = s"""{"version":"$latestVersion"}"""
      IO.write(latestVersionFile, latestVersionJson)

      createMdocVariables(latestVersion.some)
    },
    docusaurDir := (ThisBuild / baseDirectory).value / "website",
    docusaurBuildDir := docusaurDir.value / "build",
  )
  .settings(noPublish)

lazy val docsCe3 = (project in file("docs-gen-tmp/docs-ce3"))
  .enablePlugins(MdocPlugin)
  .settings(
    name := "docsCe3",
    mdocIn := file("docs/latest-ce3"),
    mdocOut := file("generated-docs/docs"),
    cleanFiles += ((ThisBuild / baseDirectory).value / "generated-docs" / "docs" / "cats-effect3"),
    scalacOptions ~= (_.filterNot(props.isScala3IncompatibleScalacOption).filter(opt => opt != "-Xfatal-warnings")),
    libraryDependencies ++= {
      val latestTag = getTheLatestTaggedVersion()
      List(
        "org.typelevel" %% "cats-effect" % "3.6.3",
        "io.kevinlee" %% "effectie-cats-effect3" % latestTag,
        libs.extrasCats.value,
        libs.extrasConcurrent.value,
      )
    },
    libraryDependencies := libraryDependenciesPostProcess(
      isScala3(scalaVersion.value),
      libraryDependencies.value,
    ),
    mdocVariables := {
      val latestVersion = getTheLatestTaggedVersion()
      createMdocVariables(latestVersion.some)
    },
  )
  .settings(noPublish)

lazy val docsV1 = (project in file("docs-gen-tmp/docs-v1"))
  .enablePlugins(MdocPlugin)
  .settings(
    name := "docsV1",
    mdocIn := file("docs/v1"),
    mdocOut := file("website/versioned_docs/version-v1/docs"),
    cleanFiles += ((ThisBuild / baseDirectory).value / "website" / "versioned_docs" / "version-v1"),
    scalacOptions ~= (_.filterNot(props.isScala3IncompatibleScalacOption).filter(opt => opt != "-Xfatal-warnings")),
    libraryDependencies ++= List(
      "io.kevinlee" %% "effectie-cats-effect"   % "1.16.0",
      "io.kevinlee" %% "effectie-monix"         % "1.16.0",
      "io.kevinlee" %% "effectie-scalaz-effect" % "1.16.0",
    ),
    libraryDependencies := libraryDependenciesPostProcess(
      isScala3(scalaVersion.value),
      libraryDependencies.value,
    ),
    mdocVariables := createMdocVariables("1.16.0".some),
  )
  .settings(noPublish)

addCommandAlias(
  "docsCleanAll",
  "; docs/clean; docsV1/clean",
)
addCommandAlias(
  "docsMdocAll",
  "; docs/mdoc; docsV1/mdoc; docsCe3/mdoc",
)

def getTheLatestTaggedVersion(): String = {
  import sys.process.*
  "git fetch --tags".!
  val tag = "git rev-list --tags --max-count=1".!!.trim
  s"git describe --tags $tag".!!.trim.stripPrefix("v")
}

def createMdocVariables(version: Option[String]): Map[String, String] = {
  val versionForDoc = version match {
    case Some(version) => version
    case None => getTheLatestTaggedVersion()
  }

  Map(
    "VERSION"                               -> versionForDoc,
    "SUPPORTED_SCALA_VERSIONS"              -> {
      val versions = props
        .CrossScalaVersions
        .map(CrossVersion.binaryScalaVersion)
        .map(binVer => s"`$binVer`")
      if (versions.length > 1)
        s"${versions.init.mkString(", ")} and ${versions.last}"
      else
        versions.mkString
    },
    "SUPPORTED_SCALA_VERSIONS_FOR_SCALA_JS" -> {
      val versions = props
        .CrossScalaVersionsForScalaJs
        .map(CrossVersion.binaryScalaVersion)
        .map(binVer => s"`$binVer`")
      if (versions.length > 1)
        s"${versions.init.mkString(", ")} and ${versions.last}"
      else
        versions.mkString
    },
  )
}

lazy val props =
  new {

    final val GitHubUsername = "Kevin-Lee"
    final val RepoName       = "effectie"

    final val Scala2Versions = List(
      "2.13.16",
      "2.12.18",
    )
    final val Scala2Version  = Scala2Versions.head
//    final val Scala2Version  = Scala2Versions.last

    final val Scala3Version = "3.3.3"

//    final val ProjectScalaVersion = "2.12.13"
    final val ProjectScalaVersion = Scala2Version
//    final val ProjectScalaVersion = Scala3Version

    lazy val licenses = List("MIT" -> url("http://opensource.org/licenses/MIT"))

    val removeDottyIncompatible: ModuleID => Boolean =
      m =>
        m.name == "wartremover" ||
          m.name == "ammonite" ||
          m.name == "kind-projector" ||
          m.name == "better-monadic-for" ||
          m.name == "mdoc"

    val isScala3IncompatibleScalacOption: String => Boolean =
      _.startsWith("-P:wartremover")

    val CrossScalaVersions = (Scala3Version :: Scala2Versions).distinct

    val CrossScalaVersionsForScalaJs = CrossScalaVersions.filterNot(_.startsWith("2.12"))

    final val IncludeTest = "compile->compile;test->test"

    final val hedgehogLatestVersion = "0.13.0"

    val MunitVersion = "0.7.29"

    val MunitCatsEffectVersion = "1.0.7"

    final val catsVersion = "2.7.0"

    final val catsEffect2Version       = "2.4.1"
    final val catsEffect2LatestVersion = "2.5.4"
    final val catsEffect3Version       = "3.3.14"

    final val cats2_0_0Version       = "2.0.0"
    final val catsEffect2_0_0Version = "2.0.0"

    final val monixVersion3_3_0 = "3.3.0"
    final val monixVersion      = "3.4.0"

    final val ExtrasVersion = "0.25.0"

    val ScalaJsMacrotaskExecutorVersion = "1.1.1"

    val ScalaJavaTimeVersion = "2.6.0"

  }

lazy val libs =
  new {
    val hedgehogVersion     = props.hedgehogLatestVersion
    lazy val hedgehogCore   = Def.setting("qa.hedgehog" %%% "hedgehog-core" % hedgehogVersion)
    lazy val hedgehogRunner = Def.setting("qa.hedgehog" %%% "hedgehog-runner" % hedgehogVersion)
    lazy val hedgehogSbt    = Def.setting("qa.hedgehog" %%% "hedgehog-sbt" % hedgehogVersion)

    lazy val hedgehogLibs =
      Def.setting(
        List(
          hedgehogCore.value,
          hedgehogRunner.value,
          hedgehogSbt.value,
        )
      )

    def libCatsCore(catsVersion: String)   = Def.setting("org.typelevel" %%% "cats-core" % catsVersion)
    def libCatsKernel(catsVersion: String) = Def.setting("org.typelevel" %%% "cats-kernel" % catsVersion)

    def libCatsEffect(catsEffectVersion: String) = Def.setting("org.typelevel" %%% "cats-effect" % catsEffectVersion)

    lazy val libCatsEffectTestKit =
      Def.setting("org.typelevel" %%% "cats-effect-kernel-testkit" % props.catsEffect3Version)

    lazy val libCatsCore_2_0_0   = Def.setting("org.typelevel" %%% "cats-core" % props.cats2_0_0Version)
    lazy val libCatsEffect_2_0_0 = Def.setting("org.typelevel" %%% "cats-effect" % props.catsEffect2_0_0Version)

    lazy val libMonix3_3_0 = Def.setting("io.monix" %%% "monix" % props.monixVersion3_3_0)
    lazy val libMonix      = Def.setting("io.monix" %%% "monix" % props.monixVersion)

    lazy val extrasCore = Def.setting("io.kevinlee" %%% "extras-core" % props.ExtrasVersion)
    lazy val extrasCats = Def.setting("io.kevinlee" %%% "extras-cats" % props.ExtrasVersion)

    lazy val extrasConcurrent        = Def.setting("io.kevinlee" %%% "extras-concurrent" % props.ExtrasVersion)
    lazy val extrasConcurrentTesting = Def.setting("io.kevinlee" %%% "extras-concurrent-testing" % props.ExtrasVersion)

    lazy val scalaJavaTime = Def.setting("io.github.cquiroz" %%% "scala-java-time" % props.ScalaJavaTimeVersion)

    lazy val munit = Def.setting("org.scalameta" %%% "munit" % props.MunitVersion)

    lazy val tests = new {
      lazy val extrasHedgehogCatsEffect3 =
        Def.setting("io.kevinlee" %%% "extras-hedgehog-ce3" % props.ExtrasVersion % Test)
      lazy val extrasConcurrent        = Def.setting("io.kevinlee" %%% "extras-concurrent" % props.ExtrasVersion % Test)
      lazy val extrasConcurrentTesting =
        Def.setting("io.kevinlee" %%% "extras-concurrent-testing" % props.ExtrasVersion % Test)

      lazy val scalaJsMacrotaskExecutor =
        Def.setting("org.scala-js" %%% "scala-js-macrotask-executor" % props.ScalaJsMacrotaskExecutorVersion % Test)

      lazy val munit = Def.setting("org.scalameta" %%% "munit" % props.MunitVersion % Test)

      lazy val munitCatsEffect3 =
        Def.setting("org.typelevel" %%% "munit-cats-effect-3" % props.MunitCatsEffectVersion % Test)
    }
  }

// scalafmt: off
def prefixedProjectName(name: String) = s"${props.RepoName}${if (name.isEmpty) "" else s"-$name"}"
// scalafmt: on

def isScala3(scalaVersion: String): Boolean = scalaVersion.startsWith("3")

def libraryDependenciesPostProcess(
  isDotty: Boolean,
  libraries: Seq[ModuleID],
): Seq[ModuleID] =
  if (isDotty)
    libraries.filterNot(props.removeDottyIncompatible)
  else
    libraries

def module(projectName: ProjectName, crossProject: CrossProject.Builder): CrossProject = {
  val prefixedName = prefixedProjectName(projectName.projectName)
  crossProject
    .in(file(s"modules/$prefixedName"))
    .settings(
      name := prefixedName,
      fork := true,
      scalacOptions := scalacOptions.value.filterNot(props.isScala3IncompatibleScalacOption),
      scalafixConfig := (
        if (scalaVersion.value.startsWith("3"))
          ((ThisBuild / baseDirectory).value / ".scalafix-scala3.conf").some
        else
          ((ThisBuild / baseDirectory).value / ".scalafix-scala2.conf").some
      ),
      libraryDependencies ++= libs.hedgehogLibs.value.map(_ % Test) ++ List(libs.extrasCats.value % Test),
      /* WartRemover and scalacOptions { */
      //      Compile / compile / wartremoverErrors ++= commonWarts((update / scalaBinaryVersion).value),
      //      Test / compile / wartremoverErrors ++= commonWarts((update / scalaBinaryVersion).value),
      wartremoverErrors ++= commonWarts((update / scalaBinaryVersion).value),
      //      , wartremoverErrors ++= Warts.all
//      Compile / console / wartremoverErrors   := List.empty,
//      Compile / console / wartremoverWarnings := List.empty,
      Compile / console / scalacOptions :=
        (console / scalacOptions)
          .value
          .filterNot(option => option.contains("wartremover") || option.contains("import")),
//      Test / console / wartremoverErrors      := List.empty,
//      Test / console / wartremoverWarnings    := List.empty,
      Test / console / scalacOptions :=
        (console / scalacOptions)
          .value
          .filterNot(option => option.contains("wartremover") || option.contains("import")),
      /* } WartRemover and scalacOptions */
      testFrameworks ++= (testFrameworks.value ++ Seq(TestFramework("hedgehog.sbt.Framework"))).distinct,
      Compile / unmanagedSourceDirectories ++= {
        val sharedSourceDir = (baseDirectory.value / ".." / "shared").getCanonicalFile / "src" / "main"
        if (isScala3(scalaVersion.value))
          Seq(
            sharedSourceDir / "scala-2.12_3",
            sharedSourceDir / "scala-2.13_3",
//            sharedSourceDir / "scala-3",
          )
        else if (scalaVersion.value.startsWith("2.13"))
          Seq(
            sharedSourceDir / "scala-2.12_2.13",
            sharedSourceDir / "scala-2.12_3",
            sharedSourceDir / "scala-2.13_3",
//            sharedSourceDir / "scala-2",
          )
        else if (scalaVersion.value.startsWith("2.12"))
          Seq(
            sharedSourceDir / "scala-2.12_2.13",
            sharedSourceDir / "scala-2.12_3",
            sharedSourceDir / "scala-2.12",
//            sharedSourceDir / "scala-2",
          )
        else
          Seq.empty
      },
      Test / unmanagedSourceDirectories ++= {
        val sharedSourceDir = (baseDirectory.value / ".." / "shared").getCanonicalFile / "src" / "test"
        if (isScala3(scalaVersion.value) || scalaVersion.value.startsWith("3."))
          Seq(
            sharedSourceDir / "scala-2.12_3",
            sharedSourceDir / "scala-2.13_3",
//            sharedSourceDir / "scala-3",
          )
        else if (scalaVersion.value.startsWith("2.13"))
          Seq(
            sharedSourceDir / "scala-2.12_2.13",
            sharedSourceDir / "scala-2.13_3",
//            sharedSourceDir / "scala-2",
          )
        else if (scalaVersion.value.startsWith("2.12"))
          Seq(
            sharedSourceDir / "scala-2.12_2.13",
            sharedSourceDir / "scala-2.12_3",
            sharedSourceDir / "scala-2.12",
//            sharedSourceDir / "scala-2",
          )
        else
          Seq.empty
      },
      licenses := props.licenses,
      /* Coveralls { */
      coverageHighlighting := (CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 10)) | Some((2, 11)) =>
          false
        case _ =>
          true
      }),
      /* } Coveralls */
    )
}

lazy val jsSettingsForFuture: SettingsDefinition = List(
  libraryDependencies ++= List(
    libs.tests.scalaJsMacrotaskExecutor.value
  )
)
//lazy val jsSettingsForFuture: SettingsDefinition = List(
//  Test / scalacOptions ++= (if (scalaVersion.value.startsWith("3")) List.empty
//                            else List("-P:scalajs:nowarnGlobalExecutionContext")),
//  Test / compile / scalacOptions ++= (if (scalaVersion.value.startsWith("3")) List.empty
//                                      else List("-P:scalajs:nowarnGlobalExecutionContext")),
//)

lazy val jsSettings: SettingsDefinition = List(
  crossScalaVersions := props.CrossScalaVersionsForScalaJs,
  Test / fork := false,
  coverageEnabled := false,
)
