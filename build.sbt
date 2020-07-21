import ProjectInfo._
import kevinlee.sbt.SbtCommon.crossVersionProps
import just.semver.SemVer
import SemVer.{Major, Minor}

val ProjectScalaVersion: String = "2.13.2"
val CrossScalaVersions: Seq[String] = Seq("2.11.12", "2.12.11", ProjectScalaVersion)
val IncludeTest: String = "compile->compile;test->test"

lazy val hedgehogVersion = "97854199ef795a5dfba15478fd9abe66035ddea2"
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

def projectCommonSettings(id: String, projectName: ProjectName, file: File): Project =
  Project(id, file)
    .settings(
        name := prefixedProjectName(projectName.projectName)
      , addCompilerPlugin("org.typelevel" % "kind-projector" % "0.11.0" cross CrossVersion.full)
      , addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
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
          case "2.12" =>
            Seq.empty[ModuleID] // TODO: add ammonite when it supports Scala 2.12.11
          case _ =>
            Seq("com.lihaoyi" % "ammonite" % "2.1.4" % Test cross CrossVersion.full)
        })
      , sourceGenerators in Test +=
        (scalaBinaryVersion.value match {
          case "2.10" =>
            task(Seq.empty[File])
          case "2.12" =>
            task(Seq.empty[File]) // TODO: add ammonite when it supports Scala 2.12.11
          case _ =>
            task {
              val file = (sourceManaged in Test).value / "amm.scala"
              IO.write(file, """object amm extends App { ammonite.Main.main(args) }""")
              Seq(file)
            }
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
  .enablePlugins(DevOopsGitReleasePlugin)
  .settings(
    name := prefixedProjectName("")
  , description := "Effect Utils"
  /* GitHub Release { */
  , gitTagFrom := "main"
  , devOopsPackagedArtifacts := List(
      s"core/target/scala-*/${name.value}*.jar"
    , s"cats-effect/target/scala-*/${name.value}*.jar"
    , s"scalaz-effect/target/scala-*/${name.value}*.jar"
    )
  /* } GitHub Release */
  )
  .settings(noPublish)
  .aggregate(core, catsEffect, scalazEffect)

lazy val core = projectCommonSettings("core", ProjectName("core"), file("core"))
  .settings(
      description  := "Effect Utils - Core"
    , unmanagedSourceDirectories in Compile ++= {
        val sharedSourceDir = baseDirectory.value / "src/main"
        if (scalaVersion.value.startsWith("2.13") || scalaVersion.value.startsWith("2.12"))
          Seq(sharedSourceDir / "scala-2.12_2.13")
        else
          Seq.empty
      }
    , libraryDependencies :=
      crossVersionProps(
          Seq.empty
        , SemVer.parseUnsafe(scalaVersion.value)
      ) {
          case (Major(2), Minor(10)) =>
            libraryDependencies.value.filterNot(m => m.organization == "org.wartremover" && m.name == "wartremover")
          case x =>
            libraryDependencies.value
        }
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
          case (Major(2), Minor(10)) =>
            libraryDependencies.value.filterNot(m => m.organization == "org.wartremover" && m.name == "wartremover") ++
              Seq(libCatsCore_2_0_0, libCatsEffect_2_0_0)
          case (Major(2), Minor(11)) =>
            libraryDependencies.value ++ Seq(libCatsCore_2_0_0, libCatsEffect_2_0_0)
          case x =>
            libraryDependencies.value ++ Seq(libCatsCore, libCatsEffect)
        }
    , initialCommands in console :=
      """import effectie.cats._"""

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
          case (Major(2), Minor(10)) =>
            libraryDependencies.value.filterNot(m => m.organization == "org.wartremover" && m.name == "wartremover") ++
              Seq(libScalazCore, libScalazEffect)
          case x =>
            libraryDependencies.value ++ Seq(libScalazCore, libScalazEffect)
        }
    , initialCommands in console :=
      """import effectie.scalaz._"""

  )
  .dependsOn(core % IncludeTest)


lazy val docs = (project in file("generated-docs"))
  .enablePlugins(MdocPlugin, DocusaurPlugin)
  .settings(
      name := prefixedProjectName("docs")
    , skip in publish := true

    , docusaurDir := (ThisBuild / baseDirectory).value / "website"
    , docusaurBuildDir := docusaurDir.value / "build"

    , gitHubPagesOrgName := GitHubUsername
    , gitHubPagesRepoName := RepoName
  )
  .settings(noPublish)
  .dependsOn(core, catsEffect, scalazEffect)
