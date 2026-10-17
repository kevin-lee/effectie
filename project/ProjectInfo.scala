import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport.*
import sbt.*
import wartremover.WartRemover.autoImport.{Wart, Warts}

object ProjectInfo {
  final case class ProjectName(projectName: String) extends AnyVal

  def commonWarts(scalaBinaryVersion: String): Seq[wartremover.Wart] = scalaBinaryVersion match {
    case "2.10" =>
      Seq.empty
    case _ =>
      Warts.allBut(Wart.Any, Wart.Nothing)
  }

  object props {

      final val GitHubUsername = "kevin-lee"
      final val RepoName       = "effectie"

      final val Scala2Versions = List(
        "2.13.18",
        "2.12.20",
      )
      final val Scala2Version  = Scala2Versions.head
  //    final val Scala2Version  = Scala2Versions.last

      val Scala3Version = "3.3.5"

  //    final val ProjectScalaVersion = "2.12.13"
      val ProjectScalaVersion = Scala2Version
  //    val ProjectScalaVersion = Scala3Version

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

      val CrossScalaVersionsForScalaJsAndNative = CrossScalaVersions.filterNot(_.startsWith("2.12"))

      final val IncludeTest = "compile->compile;test->test"

      val hedgehogLatestVersion = "0.13.1"

      val MunitVersion = "0.7.29"

      val MunitCatsEffectVersion = "1.0.7"

      val catsVersion = "2.12.0"

      val catsEffect2Version       = "2.4.1"
      val catsEffect2LatestVersion = "2.5.4"

      val catsEffect3Version          = "3.7.0"
      val catsEffect3ForNativeVersion = "3.7.0"

      final val cats2_0_0Version       = "2.0.0"
      final val catsEffect2_0_0Version = "2.0.0"

      final val monixVersion3_3_0 = "3.3.0"
      final val monixVersion      = "3.4.1"

      val ExtrasVersion = "0.53.0"

      val ScalaJsMacrotaskExecutorVersion = "1.1.1"

      val ScalaJavaTimeVersion = "2.6.0"

    }

  object libs {
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

      def libCatsEffectTestKit(catsEffect3Version: String) =
        Def.setting("org.typelevel" %%% "cats-effect-kernel-testkit" % catsEffect3Version)

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

      object tests {
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
}
