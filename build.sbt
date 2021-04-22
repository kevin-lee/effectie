import ProjectInfo._
import kevinlee.sbt.SbtCommon.crossVersionProps
import just.semver.SemVer
import SemVer.{Major, Minor}

ThisBuild / scalaVersion       := props.ProjectScalaVersion
ThisBuild / version            := ProjectVersion
ThisBuild / organization       := "io.kevinlee"
ThisBuild / organizationName   := "Kevin's Code"
ThisBuild / crossScalaVersions := props.CrossScalaVersions

ThisBuild / testFrameworks ~=
  (frameworks => (TestFramework("hedgehog.sbt.Framework") +: frameworks).distinct)

ThisBuild / developers := List(
    Developer(
      props.GitHubUsername,
      "Kevin Lee", "kevin.code@kevinlee.io",
      url(s"https://github.com/${props.GitHubUsername}"),
    )
  )

ThisBuild / homepage := Some(url(s"https://github.com/${props.GitHubUsername}/${props.RepoName}"))
ThisBuild / scmInfo :=
  Some(ScmInfo(
    url(s"https://github.com/${props.GitHubUsername}/${props.RepoName}"),
    s"git@github.com:${props.GitHubUsername}/${props.RepoName}.git",
  ))

lazy val effectie = (project in file("."))
  .enablePlugins(DevOopsGitHubReleasePlugin)
  .settings(
    name := prefixedProjectName(""),
    description := "Effect Utils",
    libraryDependencies :=
      libraryDependenciesPostProcess(isDotty.value, libraryDependencies.value),
  /* GitHub Release { */
    devOopsPackagedArtifacts := List(
      s"*/target/scala-*/${name.value}*.jar",
    )
  /* } GitHub Release */
  )
  .settings(noPublish)
  .aggregate(core, catsEffect, scalazEffect, monix)

lazy val core = projectCommonSettings("core", ProjectName("core"), file("core"))
  .settings(
    description  := "Effect Utils - Core",
    libraryDependencies :=
      crossVersionProps(
        Seq.empty,
        SemVer.parseUnsafe(scalaVersion.value),
      ) {
          case (Major(2), Minor(10), _) =>
            libraryDependencies.value.filterNot(m => m.organization == "org.wartremover" && m.name == "wartremover")
          case x =>
            libraryDependencies.value
        },
    libraryDependencies :=
      libraryDependenciesPostProcess(isDotty.value, libraryDependencies.value),
    console / initialCommands :=
      """import effectie._""",

  )

lazy val catsEffect = projectCommonSettings("catsEffect", ProjectName("cats-effect"), file("cats-effect"))
  .settings(
      description := "Effect Utils - Cats Effect",
    libraryDependencies :=
      crossVersionProps(
        List.empty,
        SemVer.parseUnsafe(scalaVersion.value),
      ) {
          case (Major(2), Minor(10), _) =>
            libraryDependencies.value.filterNot(m => m.organization == "org.wartremover" && m.name == "wartremover") ++
              Seq(libs.libCatsCore_2_0_0, libs.libCatsEffect_2_0_0)
          case (Major(2), Minor(11), _) =>
            libraryDependencies.value ++ Seq(libs.libCatsCore_2_0_0, libs.libCatsEffect_2_0_0)
          case (Major(3), Minor(0), _) =>
            libraryDependencies.value ++ Seq(libs.libCatsCore, libs.libCatsEffect)
            // libraryDependencies.value ++ Seq(libs.libCatsCore, libs.libCatsEffect3)
//              .map(_.withDottyCompat(scalaVersion.value))
          case x =>
            libraryDependencies.value ++ Seq(libs.libCatsCore, libs.libCatsEffect)
        },
    libraryDependencies := libraryDependenciesPostProcess(isDotty.value, libraryDependencies.value),
    console / initialCommands :=
      """import effectie.cats._""",

  )
  .dependsOn(core % props.IncludeTest)

lazy val monix = projectCommonSettings("monix", ProjectName("monix"), file(s"${props.RepoName}-monix"))
  .settings(
    description  := "Effect Utils - Monix",
    libraryDependencies :=
      crossVersionProps(
        List.empty,
        SemVer.parseUnsafe(scalaVersion.value),
      ) {
          case (Major(2), Minor(10), _) =>
            libraryDependencies.value.filterNot(m => m.organization == "org.wartremover" && m.name == "wartremover")
          case x =>
            libraryDependencies.value ++ Seq(libs.libMonix).map(_.withDottyCompat(scalaVersion.value))
        },
    libraryDependencies := libraryDependenciesPostProcess(isDotty.value, libraryDependencies.value),
    console / initialCommands :=
      """import effectie.monix._""",

  )
  .dependsOn(core % props.IncludeTest)

lazy val scalazEffect = projectCommonSettings("scalazEffect", ProjectName("scalaz-effect"), file("scalaz-effect"))
  .settings(
      description  := "Effect Utils for Scalaz Effect"
    , libraryDependencies :=
      crossVersionProps(
          List.empty
        , SemVer.parseUnsafe(scalaVersion.value),
      ) {
          case (Major(2), Minor(10), _) =>
            libraryDependencies.value.filterNot(m => m.organization == "org.wartremover" && m.name == "wartremover") ++
              Seq(libs.libScalazCore, libs.libScalazEffect)
          case x =>
            libraryDependencies.value ++
              (Seq(libs.libScalazCore, libs.libScalazEffect).map(_.withDottyCompat(scalaVersion.value)))
        },
    libraryDependencies := libraryDependenciesPostProcess(isDotty.value, libraryDependencies.value),
    console / initialCommands :=
      """import effectie.scalaz._""",

  )
  .dependsOn(core % props.IncludeTest)


lazy val docs = (project in file("generated-docs"))
  .enablePlugins(MdocPlugin, DocusaurPlugin)
  .settings(
    name := prefixedProjectName("docs"),
    scalacOptions := scalacOptionsPostProcess(
        SemVer.parseUnsafe(scalaVersion.value),
        isDotty.value,
        scalacOptions.value,
      ),
    libraryDependencies := libraryDependenciesPostProcess(
        isDotty.value, libraryDependencies.value
      ),
    mdocVariables := Map(
        "VERSION" -> (ThisBuild / version).value,
        "SUPPORTED_SCALA_VERSIONS" -> {
            val versions = props.CrossScalaVersions.map(v => s"`$v`")
            if (versions.length > 1)
              s"${versions.init.mkString(", ")} and ${versions.last}"
            else
              versions.mkString
          },
      ),
    docusaurDir := (ThisBuild / baseDirectory).value / "website",
    docusaurBuildDir := docusaurDir.value / "build",

    gitHubPagesOrgName := props.GitHubUsername,
    gitHubPagesRepoName := props.RepoName,
  )
  .settings(noPublish)
  .dependsOn(core, catsEffect, scalazEffect, monix)


lazy val props = new {

  val GitHubUsername = "Kevin-Lee"
  val RepoName = "effectie"

  val DottyVersions = List("3.0.0-RC1", "3.0.0-RC2")
  val DottyVersion = DottyVersions.last

//    val ProjectScalaVersion = DottyVersion
  val ProjectScalaVersion = "2.13.5"

  val removeDottyIncompatible: ModuleID => Boolean =
    m =>
      m.name == "wartremover" ||
        m.name == "ammonite" ||
        m.name == "kind-projector" ||
        m.name == "better-monadic-for" ||
        m.name == "mdoc"

  val CrossScalaVersions: Seq[String] = (
    List(
      "2.11.12", "2.12.13", "2.13.5", ProjectScalaVersion
    ) ++ DottyVersions).distinct
  val IncludeTest: String = "compile->compile;test->test"

  lazy val scala3cLanguageOptions = "-language:" + List(
    "dynamics",
    "existentials",
    "higherKinds",
    "reflectiveCalls",
    "experimental.macros",
    "implicitConversions",
  ).mkString(",")

  val hedgehogVersion = "0.6.5"

  val catsVersion       = "2.5.0"
  val catsEffectVersion = "2.4.1"
  val catsEffect3Version = "3.0.2"

  val cats2_0_0Version       = "2.0.0"
  val catsEffect2_0_0Version = "2.0.0"

  val monixVersion = "3.3.0"

  val scalazVersion = "7.2.30"

}

lazy val libs = new {
  lazy val hedgehogLibs: Seq[ModuleID] = Seq(
    "qa.hedgehog" %% "hedgehog-core" % props.hedgehogVersion % Test,
    "qa.hedgehog" %% "hedgehog-runner" % props.hedgehogVersion % Test,
    "qa.hedgehog" %% "hedgehog-sbt" % props.hedgehogVersion % Test,
  )

  lazy val libScalazCore: ModuleID = "org.scalaz" %% "scalaz-core" % props.scalazVersion
  lazy val libScalazEffect: ModuleID = "org.scalaz" %% "scalaz-effect" % props.scalazVersion

  lazy val libCatsCore: ModuleID = "org.typelevel" %% "cats-core" % props.catsVersion
  lazy val libCatsEffect: ModuleID = "org.typelevel" %% "cats-effect" % props.catsEffectVersion
  lazy val libCatsEffect3: ModuleID = "org.typelevel" %% "cats-effect" % props.catsEffect3Version

  lazy val libCatsCore_2_0_0: ModuleID = "org.typelevel" %% "cats-core" %props.cats2_0_0Version
  lazy val libCatsEffect_2_0_0: ModuleID = "org.typelevel" %% "cats-effect" % props.catsEffect2_0_0Version

  lazy val libMonix: ModuleID = "io.monix" %% "monix" % props.monixVersion
}

def prefixedProjectName(name: String) = s"${props.RepoName}${if (name.isEmpty) "" else s"-$name"}"

def scalacOptionsPostProcess(scalaSemVer: SemVer, isDotty: Boolean, options: Seq[String]): Seq[String] =
  if (isDotty || (scalaSemVer.major, scalaSemVer.minor) == (SemVer.Major(3), SemVer.Minor(0))) {
    Seq(
      "-source:3.0-migration",
      props.scala3cLanguageOptions,
      "-Ykind-projector",
      "-siteroot", "./dotty-docs",
    )
  } else {
    options
  }

def libraryDependenciesPostProcess(
  isDotty: Boolean,
  libraries: Seq[ModuleID]
): Seq[ModuleID] = (
  if (isDotty) {
    libraries
      .filterNot(props.removeDottyIncompatible)
//      .map(_.withDottyCompat(scalaVersion))
  } else
    libraries
  )

def projectCommonSettings(id: String, projectName: ProjectName, file: File): Project =
  Project(id, file)
    .settings(
      name := prefixedProjectName(projectName.projectName),
      libraryDependencies ++= (if (isDotty.value || scalaVersion.value.startsWith("3.0")) {
        List.empty[ModuleID]
      } else {
        List(
          compilerPlugin("org.typelevel" % "kind-projector" % "0.11.3" cross CrossVersion.full),
          compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
        )
      }),
      scalacOptions := scalacOptionsPostProcess(
        SemVer.parseUnsafe(scalaVersion.value),
        isDotty.value,
        scalacOptions.value,
      ),
      Compile / doc / scalacOptions := ((Compile / doc / scalacOptions).value.filterNot(
        if (isDotty.value) {
          Set(
            "-source:3.0-migration",
            "-scalajs",
            "-deprecation",
            "-explain-types",
            "-explain",
            "-feature",
            props.scala3cLanguageOptions,
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
      )),
      libraryDependencies ++= libs.hedgehogLibs.map(_.withDottyCompat(scalaVersion.value)),
      /* WartRemover and scalacOptions { */
      //      , wartremoverErrors in (Compile, compile) ++= commonWarts((scalaBinaryVersion in update).value)
      //      , wartremoverErrors in (Test, compile) ++= commonWarts((scalaBinaryVersion in update).value)
      wartremoverErrors ++= commonWarts((update / scalaBinaryVersion).value),
      //      , wartremoverErrors ++= Warts.all
      Compile / console / wartremoverErrors := List.empty,
      Compile / console / wartremoverWarnings := List.empty,
      Compile / console / scalacOptions :=
        (console / scalacOptions).value
          .filterNot(option =>
            option.contains("wartremover") || option.contains("import")
          ),
      Test / console / wartremoverErrors := List.empty,
      Test / console / wartremoverWarnings := List.empty,
      Test / console / scalacOptions :=
        (console / scalacOptions).value
          .filterNot( option =>
            option.contains("wartremover") || option.contains("import")
          ),
      /* } WartRemover and scalacOptions */
      testFrameworks ++= (testFrameworks.value ++ Seq(TestFramework("hedgehog.sbt.Framework"))).distinct,

      /* Ammonite-REPL { */
      libraryDependencies ++=
        (scalaBinaryVersion.value match {
          case "2.10" =>
            Seq.empty[ModuleID]
          case "2.11" =>
            Seq("com.lihaoyi" % "ammonite" % "1.6.7" % Test cross CrossVersion.full)
          case "2.12" | "2.13" =>
            Seq("com.lihaoyi" % "ammonite" % "2.3.8-58-aa8b2ab1" % Test cross CrossVersion.full)
          case _ =>
            Seq.empty[ModuleID]
        }),
      Compile / unmanagedSourceDirectories ++= {
        val sharedSourceDir = baseDirectory.value / "src" / "main"
        if (isDotty.value || scalaVersion.value.startsWith("3.0"))
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
            sharedSourceDir / "scala-2.11_2.12",
            sharedSourceDir / "scala-2",
          )
        else if (scalaVersion.value.startsWith("2.11"))
          Seq(
            sharedSourceDir / "scala-2.11_2.12",
            sharedSourceDir / "scala-2",
          )
        else
          Seq.empty
      },
      Test / unmanagedSourceDirectories ++= {
        val sharedSourceDir = baseDirectory.value / "src" / "test"
        if (isDotty.value || scalaVersion.value.startsWith("3.0"))
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
            sharedSourceDir / "scala-2.11_2.12",
            sharedSourceDir / "scala-2",
          )
        else if (scalaVersion.value.startsWith("2.11"))
          Seq(
            sharedSourceDir / "scala-2.11_2.12",
            sharedSourceDir / "scala-2",
          )
        else
          Seq.empty
      },
      Test / sourceGenerators +=
        (scalaBinaryVersion.value match {
          case "2.10" =>
            task(Seq.empty[File])
          case "2.12" | "2.13" =>
            task {
              val file = (Test / sourceManaged).value / "amm.scala"
              IO.write(file, """object amm extends App { ammonite.Main.main(args) }""")
              Seq(file)
            }
          case _ =>
            task(Seq.empty[File])
        }),
      /* } Ammonite-REPL */
      /* Bintray { */
      bintrayPackageLabels := Seq(
        "Scala",
        "Effect",
        "Referential Transparency",
        "Tagless Final",
        "Finally Tagless",
        "Functional Programming",
        "FP"
      ),
      bintrayVcsUrl := Some(s"""https://github.com/${props.GitHubUsername}/${props.RepoName}"""),
      licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
      /* } Bintray */

      /* Coveralls { */
      coverageHighlighting := (CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, 10)) =>
          false
        case _ =>
          true
      })
      /* } Coveralls */
    )
