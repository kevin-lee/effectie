import ProjectInfo._
import kevinlee.sbt.SbtCommon.crossVersionProps
import just.semver.SemVer
import SemVer.{Major, Minor}

val DottyVersion = "3.0.0-RC1"
val ProjectScalaVersion = "2.13.3"

val removeDottyIncompatible: ModuleID => Boolean =
  m =>
    m.name == "wartremover" ||
      m.name == "ammonite" ||
      m.name == "kind-projector" ||
      m.name == "mdoc"

val CrossScalaVersions: Seq[String] = Seq(
  "2.11.12", "2.12.12", ProjectScalaVersion, DottyVersion
).distinct
val IncludeTest: String = "compile->compile;test->test"

lazy val scala3cLanguageOptions = "-language:" + List(
  "dynamics",
  "existentials",
  "higherKinds",
  "reflectiveCalls",
  "experimental.macros",
  "implicitConversions"
).mkString(",")

lazy val hedgehogVersion = "0.6.1"
lazy val hedgehogRepo: MavenRepository =
  "bintray-scala-hedgehog" at "https://dl.bintray.com/hedgehogqa/scala-hedgehog"

lazy val hedgehogLibs: Seq[ModuleID] = Seq(
    "qa.hedgehog" %% "hedgehog-core" % hedgehogVersion % Test
  , "qa.hedgehog" %% "hedgehog-runner" % hedgehogVersion % Test
  , "qa.hedgehog" %% "hedgehog-sbt" % hedgehogVersion % Test
)

lazy val libScalazCore: ModuleID = "org.scalaz" %% "scalaz-core" % "7.2.30"
lazy val libScalazEffect: ModuleID = "org.scalaz" %% "scalaz-effect" % "7.2.30"

lazy val libCatsCore: ModuleID = "org.typelevel" %% "cats-core" % "2.1.1"
lazy val libCatsEffect: ModuleID = "org.typelevel" %% "cats-effect" % "2.1.2"

lazy val libCatsCore_2_0_0: ModuleID = "org.typelevel" %% "cats-core" % "2.0.0"
lazy val libCatsEffect_2_0_0: ModuleID = "org.typelevel" %% "cats-effect" % "2.0.0"

lazy val libMonix: ModuleID = "io.monix" %% "monix" % "3.3.0"

val GitHubUsername = "Kevin-Lee"
val RepoName = "effectie"

ThisBuild / scalaVersion     := ProjectScalaVersion
ThisBuild / version          := ProjectVersion
ThisBuild / organization     := "io.kevinlee"
ThisBuild / organizationName := "Kevin's Code"
ThisBuild / crossScalaVersions := CrossScalaVersions

ThisBuild / developers   := List(
    Developer(GitHubUsername, "Kevin Lee", "kevin.code@kevinlee.io", url(s"https://github.com/$GitHubUsername"))
  )

ThisBuild / homepage := Some(url(s"https://github.com/$GitHubUsername/$RepoName"))
ThisBuild / scmInfo :=
  Some(ScmInfo(
    url(s"https://github.com/$GitHubUsername/$RepoName")
  , s"git@github.com:$GitHubUsername/$RepoName.git"
  ))

def prefixedProjectName(name: String) = s"$RepoName${if (name.isEmpty) "" else s"-$name"}"

lazy val noPublish: SettingsDefinition = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false,
  skip in sbt.Keys.`package` := true,
  skip in packagedArtifacts := true,
  skip in publish := true
)

def scalacOptionsPostProcess(scalaSemVer: SemVer, isDotty: Boolean, options: Seq[String]): Seq[String] =
  if (isDotty || (scalaSemVer.major, scalaSemVer.minor) == (SemVer.Major(3), SemVer.Minor(0))) {
    Seq(
      "-source:3.0-migration",
      scala3cLanguageOptions,
      "-Ykind-projector",
      "-siteroot", "./dotty-docs",
    )
  } else {
    options
  }

def libraryDependenciesPostProcess(
  scalaVersion: String,
  isDotty: Boolean,
  libraries: Seq[ModuleID]
): Seq[ModuleID] = (
  if (isDotty) {
    libraries
      .filterNot(removeDottyIncompatible)
      .map(_.withDottyCompat(scalaVersion))
  } else
    libraries
)

def projectCommonSettings(id: String, projectName: ProjectName, file: File): Project =
  Project(id, file)
    .settings(
        name := prefixedProjectName(projectName.projectName)
      , addCompilerPlugin("org.typelevel" % "kind-projector" % "0.11.3" cross CrossVersion.full)
      , addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")

      , scalacOptions := scalacOptionsPostProcess(
          SemVer.parseUnsafe(scalaVersion.value),
          isDotty.value,
          scalacOptions.value
        )
      , Compile / doc / scalacOptions := ((Compile / doc / scalacOptions).value.filterNot(
          if (isDotty.value) {
            Set(
              "-source:3.0-migration",
              "-scalajs",
              "-deprecation",
              "-explain-types",
              "-explain",
              "-feature",
              scala3cLanguageOptions,
              "-unchecked",
              "-Xfatal-warnings",
              "-Ykind-projector",
              "-from-tasty",
              "-encoding",
              "utf8",
            )
          } else {
            Set.empty[String]
          }
        ))
      , resolvers ++= Seq(
          Resolver.sonatypeRepo("releases")
        , hedgehogRepo
        )
      , libraryDependencies ++= hedgehogLibs
      /* WartRemover and scalacOptions { */
//      , wartremoverErrors in (Compile, compile) ++= commonWarts((scalaBinaryVersion in update).value)
//      , wartremoverErrors in (Test, compile) ++= commonWarts((scalaBinaryVersion in update).value)
      , wartremoverErrors ++= commonWarts((scalaBinaryVersion in update).value)
//      , wartremoverErrors ++= Warts.all
      , Compile / console / wartremoverErrors := List.empty
      , Compile / console / wartremoverWarnings := List.empty
      , Compile / console / scalacOptions :=
          (console / scalacOptions).value
            .filterNot(option =>
              option.contains("wartremover") || option.contains("import")
            )
      , Test / console / wartremoverErrors := List.empty
      , Test / console / wartremoverWarnings := List.empty
      , Test / console / scalacOptions :=
          (console / scalacOptions).value
            .filterNot( option =>
              option.contains("wartremover") || option.contains("import")
            )
      /* } WartRemover and scalacOptions */
      , testFrameworks ++= Seq(TestFramework("hedgehog.sbt.Framework"))

      /* Ammonite-REPL { */
      , libraryDependencies ++=
        (scalaBinaryVersion.value match {
          case "2.10" =>
            Seq.empty[ModuleID]
          case "2.11" =>
            Seq("com.lihaoyi" % "ammonite" % "1.6.7" % Test cross CrossVersion.full)
          case "2.12" | "2.13" =>
            Seq("com.lihaoyi" % "ammonite" % "2.3.8-36-1cce53f3" % Test cross CrossVersion.full)
          case _ =>
            Seq.empty[ModuleID]
        })
      , unmanagedSourceDirectories in Compile ++= {
        val sharedSourceDir = baseDirectory.value / "src/main"
        if (scalaVersion.value.startsWith("3.0"))
          Seq(
            sharedSourceDir / "scala-2.12_3.0",
            sharedSourceDir / "scala-2.13_3.0",
          )
        else if (scalaVersion.value.startsWith("2.13"))
          Seq(
            sharedSourceDir / "scala-2.12_2.13",
            sharedSourceDir / "scala-2.12_3.0",
            sharedSourceDir / "scala-2.13_3.0",
          )
        else if (scalaVersion.value.startsWith("2.12"))
          Seq(
            sharedSourceDir / "scala-2.12_2.13",
            sharedSourceDir / "scala-2.12_3.0",
            sharedSourceDir / "scala-2.11_2.12",
          )
        else if (scalaVersion.value.startsWith("2.11"))
          Seq(sharedSourceDir / "scala-2.11_2.12")
        else
          Seq.empty
      }
      , unmanagedSourceDirectories in Test ++= {
        val sharedSourceDir = baseDirectory.value / "src/test"
        if (scalaVersion.value.startsWith("3.0"))
          Seq(
            sharedSourceDir / "scala-2.12_3.0",
            sharedSourceDir / "scala-2.13_3.0",
          )
        else if (scalaVersion.value.startsWith("2.13"))
          Seq(
            sharedSourceDir / "scala-2.12_2.13",
            sharedSourceDir / "scala-2.13_3.0",
          )
        else if (scalaVersion.value.startsWith("2.12"))
          Seq(
            sharedSourceDir / "scala-2.12_2.13",
            sharedSourceDir / "scala-2.12_3.0",
            sharedSourceDir / "scala-2.11_2.12",
          )
        else if (scalaVersion.value.startsWith("2.11"))
          Seq(sharedSourceDir / "scala-2.11_2.12")
        else
          Seq.empty
      }
      , sourceGenerators in Test +=
        (scalaBinaryVersion.value match {
          case "2.10" =>
            task(Seq.empty[File])
          case "2.12" | "2.13" =>
            task {
              val file = (sourceManaged in Test).value / "amm.scala"
              IO.write(file, """object amm extends App { ammonite.Main.main(args) }""")
              Seq(file)
            }
          case _ =>
            task(Seq.empty[File])
        })
      /* } Ammonite-REPL */
      /* Bintray { */
      , bintrayPackageLabels := Seq("Scala", "Effect", "Referential Transparency", "Tagless Final", "Finally Tagless", "Functional Programming", "FP")
      , bintrayVcsUrl := Some(s"""https://github.com/$GitHubUsername/$RepoName""")
      , licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
      /* } Bintray */

      /* Coveralls { */
      , coverageHighlighting := (CrossVersion.partialVersion(scalaVersion.value) match {
          case Some((2, 10)) =>
            false
          case _ =>
            true
        })
      /* } Coveralls */
    )

lazy val effectie = (project in file("."))
  .enablePlugins(DevOopsGitHubReleasePlugin)
  .settings(
    name := prefixedProjectName("")
  , description := "Effect Utils"
  , libraryDependencies := libraryDependenciesPostProcess(scalaVersion.value, isDotty.value, libraryDependencies.value)
  /* GitHub Release { */
  , devOopsPackagedArtifacts := List(
      s"*/target/scala-*/${name.value}*.jar"
    )
  /* } GitHub Release */
  )
  .settings(noPublish)
  .aggregate(core, catsEffect, scalazEffect, monix)

lazy val core = projectCommonSettings("core", ProjectName("core"), file("core"))
  .settings(
      description  := "Effect Utils - Core"
    , libraryDependencies :=
      crossVersionProps(
          Seq.empty
        , SemVer.parseUnsafe(scalaVersion.value)
      ) {
          case (Major(2), Minor(10), _) =>
            libraryDependencies.value.filterNot(m => m.organization == "org.wartremover" && m.name == "wartremover")
          case x =>
            libraryDependencies.value
        }
    , libraryDependencies := libraryDependenciesPostProcess(scalaVersion.value, isDotty.value, libraryDependencies.value)
    , initialCommands in console :=
      """import effectie._"""

  )

lazy val catsEffect = projectCommonSettings("catsEffect", ProjectName("cats-effect"), file("cats-effect"))
  .settings(
      description  := "Effect Utils - Cats Effect"
    , libraryDependencies :=
      crossVersionProps(
          List.empty
        , SemVer.parseUnsafe(scalaVersion.value)
      ) {
          case (Major(2), Minor(10), _) =>
            libraryDependencies.value.filterNot(m => m.organization == "org.wartremover" && m.name == "wartremover") ++
              Seq(libCatsCore_2_0_0, libCatsEffect_2_0_0)
          case (Major(2), Minor(11), _) =>
            libraryDependencies.value ++ Seq(libCatsCore_2_0_0, libCatsEffect_2_0_0)
          case x =>
            libraryDependencies.value ++ Seq(libCatsCore, libCatsEffect)
        }
    , libraryDependencies := libraryDependenciesPostProcess(scalaVersion.value, isDotty.value, libraryDependencies.value)
    , initialCommands in console :=
      """import effectie.cats._"""

  )
  .dependsOn(core % IncludeTest)

lazy val monix = projectCommonSettings("monix", ProjectName("monix"), file(s"$RepoName-monix"))
  .settings(
      description  := "Effect Utils - Monix"
    , libraryDependencies :=
      crossVersionProps(
          List.empty
        , SemVer.parseUnsafe(scalaVersion.value)
      ) {
          case (Major(2), Minor(10), _) =>
            libraryDependencies.value.filterNot(m => m.organization == "org.wartremover" && m.name == "wartremover")
          case x =>
            libraryDependencies.value ++ Seq(libMonix)
        }
    , libraryDependencies := libraryDependenciesPostProcess(scalaVersion.value, isDotty.value, libraryDependencies.value)
    , initialCommands in console :=
      """import effectie.monix._"""

  )
  .dependsOn(core % IncludeTest)

lazy val scalazEffect = projectCommonSettings("scalazEffect", ProjectName("scalaz-effect"), file("scalaz-effect"))
  .settings(
      description  := "Effect Utils for Scalaz Effect"
    , libraryDependencies :=
      crossVersionProps(
          List.empty
        , SemVer.parseUnsafe(scalaVersion.value)
      ) {
          case (Major(2), Minor(10), _) =>
            libraryDependencies.value.filterNot(m => m.organization == "org.wartremover" && m.name == "wartremover") ++
              Seq(libScalazCore, libScalazEffect)
          case x =>
            libraryDependencies.value ++ Seq(libScalazCore, libScalazEffect)
        }
    , libraryDependencies := libraryDependenciesPostProcess(scalaVersion.value, isDotty.value, libraryDependencies.value)
    , initialCommands in console :=
      """import effectie.scalaz._"""

  )
  .dependsOn(core % IncludeTest)


lazy val docs = (project in file("generated-docs"))
  .enablePlugins(MdocPlugin, DocusaurPlugin)
  .settings(
      name := prefixedProjectName("docs")
    , scalacOptions := scalacOptionsPostProcess(
        SemVer.parseUnsafe(scalaVersion.value),
        isDotty.value,
        scalacOptions.value
      )
    , libraryDependencies := libraryDependenciesPostProcess(
        scalaVersion.value, isDotty.value, libraryDependencies.value
      )
    , mdocVariables := Map(
        "VERSION" -> (ThisBuild / version).value,
        "SUPPORTED_SCALA_VERSIONS" -> {
            val versions = CrossScalaVersions.map(v => s"`$v`")
            if (versions.length > 1)
              s"${versions.init.mkString(", ")} and ${versions.last}"
            else
              versions.mkString
          }
      )

    , docusaurDir := (ThisBuild / baseDirectory).value / "website"
    , docusaurBuildDir := docusaurDir.value / "build"

    , gitHubPagesOrgName := GitHubUsername
    , gitHubPagesRepoName := RepoName
  )
  .settings(noPublish)
  .dependsOn(core, catsEffect, scalazEffect, monix)
