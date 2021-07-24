import ProjectInfo._
import kevinlee.sbt.SbtCommon.crossVersionProps
import just.semver.{Anh, Dsv, SemVer}
import SemVer.{Major, Minor, Patch}
import just.semver.AdditionalInfo.PreRelease

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

lazy val effectie = (project in file("."))
  .enablePlugins(DevOopsGitHubReleasePlugin)
  .settings(
    name := prefixedProjectName(""),
    description := "Effect Utils",
    libraryDependencies :=
      libraryDependenciesPostProcess(isScala3_0(scalaVersion.value), libraryDependencies.value),
  )
  .settings(noPublish)
  .aggregate(core, catsEffect, catsEffect3, monix, scalazEffect)

lazy val core = projectCommonSettings("core", ProjectName("core"), file("core"))
  .settings(
    description := "Effect Utils - Core",
    libraryDependencies :=
      libraryDependenciesPostProcess(isScala3_0(scalaVersion.value), libraryDependencies.value),
    console / initialCommands :=
      """import effectie._""",
  )

lazy val catsEffect  = projectCommonSettings("catsEffect", ProjectName("cats-effect"), file("cats-effect"))
  .settings(
    description := "Effect Utils - Cats Effect",
    libraryDependencies :=
      (SemVer.parseUnsafe(scalaVersion.value) match {
        case SemVer(Major(2), Minor(11), _, _, _) =>
          libraryDependencies.value ++ Seq(libs.libCatsCore_2_0_0, libs.libCatsEffect_2_0_0)
        case SemVer(
              Major(3),
              Minor(0),
              Patch(0),
              Some(PreRelease(List(Dsv(List(Anh.Alphabet("RC"), Anh.Num("1")))))),
              _
            ) =>
          libraryDependencies.value ++ Seq(
            libs.libCatsCore(props.catsVersion),
            libs.libCatsEffect(props.catsEffect2Version)
          )
        case x                                    =>
          libraryDependencies.value ++ Seq(
            libs.libCatsCore(props.catsLatestVersion),
            libs.libCatsEffect(props.catsEffect2LatestVersion)
          )
      }),
    libraryDependencies := libraryDependenciesPostProcess(isScala3_0(scalaVersion.value), libraryDependencies.value),
    console / initialCommands :=
      """import effectie.cats._""",
  )
  .dependsOn(core % props.IncludeTest)

lazy val catsEffect3 = projectCommonSettings("catsEffect3", ProjectName("cats-effect3"), file("cats-effect3"))
  .settings(
    description := "Effect Utils - Cats Effect 3",
    libraryDependencies ++= List(
      libs.libCatsCore(props.catsLatestVersion),
      libs.libCatsEffect(props.catsEffect3Version)
    ),
    libraryDependencies := libraryDependenciesPostProcess(isScala3_0(scalaVersion.value), libraryDependencies.value),
    console / initialCommands :=
      """import effectie.cats._""",
  )
  .dependsOn(
    core % props.IncludeTest,
  )

lazy val monix        = projectCommonSettings("monix", ProjectName("monix"), file(s"${props.RepoName}-monix"))
  .settings(
    description := "Effect Utils - Monix",
    libraryDependencies :=
      crossVersionProps(
        List.empty,
        SemVer.parseUnsafe(scalaVersion.value),
      ) {
        case (Major(2), Minor(11), _) =>
          libraryDependencies.value ++ List(libs.libMonix3_3_0)
        case x                        =>
          libraryDependencies.value ++ List(libs.libMonix)
      },
    libraryDependencies := libraryDependenciesPostProcess(isScala3_0(scalaVersion.value), libraryDependencies.value),
    console / initialCommands :=
      """import effectie.monix._""",
  )
  .dependsOn(core % props.IncludeTest)

lazy val scalazEffect = projectCommonSettings("scalazEffect", ProjectName("scalaz-effect"), file("scalaz-effect"))
  .settings(
    description := "Effect Utils for Scalaz Effect",
    libraryDependencies ++= List(libs.libScalazCore, libs.libScalazEffect).map(_.cross(CrossVersion.for3Use2_13)),
    libraryDependencies := libraryDependenciesPostProcess(isScala3_0(scalaVersion.value), libraryDependencies.value),
    console / initialCommands :=
      """import effectie.scalaz._""",
  )
  .dependsOn(core % props.IncludeTest)

lazy val docs         = (project in file("generated-docs"))
  .enablePlugins(MdocPlugin, DocusaurPlugin)
  .settings(
    name := prefixedProjectName("docs"),
    scalacOptions ~= (_.filterNot(props.isScala3IncompatibleScalacOption)),
    libraryDependencies := libraryDependenciesPostProcess(
      isScala3_0(scalaVersion.value),
      libraryDependencies.value
    ),
    mdocVariables := Map(
      "VERSION"                  -> {
        import sys.process._
        "git fetch --tags".!
        val tag = "git rev-list --tags --max-count=1".!!.trim
        s"git describe --tags $tag".!!.trim.stripPrefix("v")
      },
      "SUPPORTED_SCALA_VERSIONS" -> {
        val versions = props
          .CrossScalaVersions
          .map(CrossVersion.binaryScalaVersion)
          .map(binVer => s"`$binVer`")
        if (versions.length > 1)
          s"${versions.init.mkString(", ")} and ${versions.last}"
        else
          versions.mkString
      },
    ),
    docusaurDir := (ThisBuild / baseDirectory).value / "website",
    docusaurBuildDir := docusaurDir.value / "build",
  )
  .settings(noPublish)
  .dependsOn(core, catsEffect, scalazEffect, monix)

lazy val props =
  new {

    final val GitHubUsername = "Kevin-Lee"
    final val RepoName       = "effectie"

    final val DottyVersions = List("3.0.0")
    final val DottyVersion  = DottyVersions.head

    final val ProjectScalaVersion = "2.13.5"

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

    final val CrossScalaVersions =
      (DottyVersions ++ List(
        ProjectScalaVersion,
        "2.13.5",
        "2.12.13",
      )).distinct

    final val IncludeTest = "compile->compile;test->test"

    final val hedgehogLatestVersion = "0.7.0"

    final val catsVersion       = "2.5.0"
    final val catsLatestVersion = "2.6.1"

    final val catsEffect2Version       = "2.4.1"
    final val catsEffect2LatestVersion = "2.5.1"
    final val catsEffect3Version       = "3.1.1"

    final val cats2_0_0Version       = "2.0.0"
    final val catsEffect2_0_0Version = "2.0.0"

    final val monixVersion3_3_0 = "3.3.0"
    final val monixVersion      = "3.4.0"

    final val scalazVersion = "7.2.31"

  }

lazy val libs =
  new {
    def hedgehogLibs(scalaVersion: String): List[ModuleID] = {
      val hedgehogVersion = props.hedgehogLatestVersion
      List(
        "qa.hedgehog" %% "hedgehog-core"   % hedgehogVersion % Test,
        "qa.hedgehog" %% "hedgehog-runner" % hedgehogVersion % Test,
        "qa.hedgehog" %% "hedgehog-sbt"    % hedgehogVersion % Test,
      )
    }

    lazy val libScalazCore: ModuleID   = "org.scalaz" %% "scalaz-core"   % props.scalazVersion
    lazy val libScalazEffect: ModuleID = "org.scalaz" %% "scalaz-effect" % props.scalazVersion

    def libCatsCore(catsVersion: String): ModuleID = "org.typelevel" %% "cats-core" % catsVersion

    def libCatsEffect(catsEffectVersion: String): ModuleID = "org.typelevel" %% "cats-effect" % catsEffectVersion

    lazy val libCatsCore_2_0_0: ModuleID   = "org.typelevel" %% "cats-core"   % props.cats2_0_0Version
    lazy val libCatsEffect_2_0_0: ModuleID = "org.typelevel" %% "cats-effect" % props.catsEffect2_0_0Version

    lazy val libMonix3_3_0: ModuleID = "io.monix" %% "monix" % props.monixVersion3_3_0
    lazy val libMonix: ModuleID      = "io.monix" %% "monix" % props.monixVersion
  }

// scalafmt: off
def prefixedProjectName(name: String) = s"${props.RepoName}${if (name.isEmpty) "" else s"-$name"}"
// scalafmt: on

def isScala3_0(scalaVersion: String): Boolean = scalaVersion.startsWith("3.0")

def libraryDependenciesPostProcess(
  isDotty: Boolean,
  libraries: Seq[ModuleID]
): Seq[ModuleID] =
  if (isDotty)
    libraries.filterNot(props.removeDottyIncompatible)
  else
    libraries

def projectCommonSettings(id: String, projectName: ProjectName, file: File): Project =
  Project(id, file)
    .settings(
      name := prefixedProjectName(projectName.projectName),
      scalacOptions ~= (_.filterNot(props.isScala3IncompatibleScalacOption)),
      libraryDependencies ++= libs.hedgehogLibs(scalaVersion.value),
      /* WartRemover and scalacOptions { */
//      Compile / compile / wartremoverErrors ++= commonWarts((update / scalaBinaryVersion).value),
//      Test / compile / wartremoverErrors ++= commonWarts((update / scalaBinaryVersion).value),
      wartremoverErrors ++= commonWarts((update / scalaBinaryVersion).value),
      //      , wartremoverErrors ++= Warts.all
      Compile / console / wartremoverErrors := List.empty,
      Compile / console / wartremoverWarnings := List.empty,
      Compile / console / scalacOptions :=
        (console / scalacOptions)
          .value
          .filterNot(option => option.contains("wartremover") || option.contains("import")),
      Test / console / wartremoverErrors := List.empty,
      Test / console / wartremoverWarnings := List.empty,
      Test / console / scalacOptions :=
        (console / scalacOptions)
          .value
          .filterNot(option => option.contains("wartremover") || option.contains("import")),
      /* } WartRemover and scalacOptions */
      testFrameworks ++= (testFrameworks.value ++ Seq(TestFramework("hedgehog.sbt.Framework"))).distinct,
      Compile / unmanagedSourceDirectories ++= {
        val sharedSourceDir = baseDirectory.value / "src" / "main"
        if (isScala3_0(scalaVersion.value))
          Seq(
            sharedSourceDir / "scala-2.12_3.0",
            sharedSourceDir / "scala-2.13_3.0",
            sharedSourceDir / "scala-3",
          )
        else if (scalaVersion.value.startsWith("2.13"))
          Seq(
            sharedSourceDir / "scala-2.12_2.13",
            sharedSourceDir / "scala-2.12_3.0",
            sharedSourceDir / "scala-2.13_3.0",
            sharedSourceDir / "scala-2",
          )
        else if (scalaVersion.value.startsWith("2.12"))
          Seq(
            sharedSourceDir / "scala-2.12_2.13",
            sharedSourceDir / "scala-2.12_3.0",
            sharedSourceDir / "scala-2.12",
            sharedSourceDir / "scala-2",
          )
        else
          Seq.empty
      },
      Test / unmanagedSourceDirectories ++= {
        val sharedSourceDir = baseDirectory.value / "src" / "test"
        if (isScala3_0(scalaVersion.value) || scalaVersion.value.startsWith("3.0"))
          Seq(
            sharedSourceDir / "scala-2.12_3.0",
            sharedSourceDir / "scala-2.13_3.0",
            sharedSourceDir / "scala-3",
          )
        else if (scalaVersion.value.startsWith("2.13"))
          Seq(
            sharedSourceDir / "scala-2.12_2.13",
            sharedSourceDir / "scala-2.13_3.0",
            sharedSourceDir / "scala-2",
          )
        else if (scalaVersion.value.startsWith("2.12"))
          Seq(
            sharedSourceDir / "scala-2.12_2.13",
            sharedSourceDir / "scala-2.12_3.0",
            sharedSourceDir / "scala-2.12",
            sharedSourceDir / "scala-2",
          )
        else
          Seq.empty
      },
      licenses := props.licenses,
      /* Coveralls { */
      coverageHighlighting := (CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 10)) | Some((2, 11)) =>
          false
        case _                             =>
          true
      })
      /* } Coveralls */
    )
