package effectie.core

trait ConsoleEffect[F[*]] {
  def readLn: F[String]

  def readPassword: F[Array[Char]]

  def putStr(value: String): F[Unit]

  def putStrLn(value: String): F[Unit]

  def putErrStr(value: String): F[Unit]

  def putErrStrLn(value: String): F[Unit]

  def readYesNo(prompt: String): F[YesNo]
}

object ConsoleEffect {
  def apply[F[*]: ConsoleEffect]: ConsoleEffect[F] = implicitly[ConsoleEffect[F]]

  abstract class ConsoleEffectWithoutFlatMap[F[*]: FxCtor] extends ConsoleEffect[F] {

    override def readLn: F[String] =
      implicitly[FxCtor[F]].effectOf(scala.io.StdIn.readLine())

    override def readPassword: F[Array[Char]] =
      implicitly[FxCtor[F]].effectOf(System.console().readPassword())

    override def putStr(value: String): F[Unit] =
      implicitly[FxCtor[F]].effectOf(Console.out.print(value))

    override def putStrLn(value: String): F[Unit] =
      implicitly[FxCtor[F]].effectOf(Console.out.println(value))

    override def putErrStr(value: String): F[Unit] =
      implicitly[FxCtor[F]].effectOf(Console.err.print(value))

    override def putErrStrLn(value: String): F[Unit] =
      implicitly[FxCtor[F]].effectOf(Console.err.println(value))
  }
}
