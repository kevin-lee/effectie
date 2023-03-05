package effectie.core

trait ConsoleFx[F[*]] {
  def readLn: F[String]

  def readPassword: F[Array[Char]]

  def putStr(value: String): F[Unit]

  def putStrLn(value: String): F[Unit]

  def putErrStr(value: String): F[Unit]

  def putErrStrLn(value: String): F[Unit]

  def readYesNo(prompt: String): F[YesNo]
}

object ConsoleFx {
  def apply[F[*]: FxCtor]: ConsoleFx[F] = new ConsoleFxF[F]

  private class ConsoleFxF[F[*]](implicit fxCtor: FxCtor[F]) extends ConsoleFx[F] {

    override def readLn: F[String] =
      FxCtor[F].effectOf(scala.io.StdIn.readLine())

    override def readPassword: F[Array[Char]] =
      FxCtor[F].effectOf(System.console().readPassword())

    override def putStr(value: String): F[Unit] =
      FxCtor[F].effectOf(Console.out.print(value))

    override def putStrLn(value: String): F[Unit] =
      FxCtor[F].effectOf(Console.out.println(value))

    override def putErrStr(value: String): F[Unit] =
      FxCtor[F].effectOf(Console.err.print(value))

    override def putErrStrLn(value: String): F[Unit] =
      FxCtor[F].effectOf(Console.err.println(value))

    @SuppressWarnings(Array("org.wartremover.warts.Recursion"))
    override def readYesNo(prompt: String): F[YesNo] =
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
