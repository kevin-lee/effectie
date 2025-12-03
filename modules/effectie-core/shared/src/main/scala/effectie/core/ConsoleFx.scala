package effectie.core

trait ConsoleFx[F[*]] {
  def readLn(implicit fxCtor: FxCtor[F]): F[String]

  def readPassword(implicit fxCtor: FxCtor[F]): F[Array[Char]]

  def putStr(value: String)(implicit fxCtor: FxCtor[F]): F[Unit]

  def putStrLn(value: String)(implicit fxCtor: FxCtor[F]): F[Unit]

  def putErrStr(value: String)(implicit fxCtor: FxCtor[F]): F[Unit]

  def putErrStrLn(value: String)(implicit fxCtor: FxCtor[F]): F[Unit]

  def readYesNo(prompt: String)(implicit fxCtor: FxCtor[F]): F[YesNo]
}

object ConsoleFx {
  def apply[F[*]]: ConsoleFx[F] = consoleFx.asInstanceOf[ConsoleFx[F]] // scalafix:ok DisableSyntax.asInstanceOf

  private val consoleFx = new ConsoleFxF // scalafix:ok DisableSyntax.noFinalVal

  private final class ConsoleFxF[F[*]] extends ConsoleFx[F] {

    override def readLn(implicit fxCtor: FxCtor[F]): F[String] =
      fxCtor.effectOf(scala.io.StdIn.readLine())

    override def readPassword(implicit fxCtor: FxCtor[F]): F[Array[Char]] =
      fxCtor.effectOf(System.console().readPassword())

    override def putStr(value: String)(implicit fxCtor: FxCtor[F]): F[Unit] =
      fxCtor.effectOf(Console.out.print(value))

    override def putStrLn(value: String)(implicit fxCtor: FxCtor[F]): F[Unit] =
      fxCtor.effectOf(Console.out.println(value))

    override def putErrStr(value: String)(implicit fxCtor: FxCtor[F]): F[Unit] =
      fxCtor.effectOf(Console.err.print(value))

    override def putErrStrLn(value: String)(implicit fxCtor: FxCtor[F]): F[Unit] =
      fxCtor.effectOf(Console.err.println(value))

    @SuppressWarnings(Array("org.wartremover.warts.Recursion"))
    override def readYesNo(prompt: String)(implicit fxCtor: FxCtor[F]): F[YesNo] =
      fxCtor.flatMapFa(putStrLn(prompt)) { _ =>
        fxCtor.flatMapFa(readLn) {
          case "y" | "Y" =>
            FxCtor[F].pureOf(YesNo.yes)
          case "n" | "N" =>
            FxCtor[F].pureOf(YesNo.no)
          case _ =>
            readYesNo(prompt)
        }
      }
  }

}
