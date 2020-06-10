package effectie

trait ConsoleEffect[F[_]] {
  def readLn: F[String]

  def putStr(value: String): F[Unit]

  def putStrLn(value: String): F[Unit]

  def putErrStr(value: String): F[Unit]

  def putErrStrLn(value: String): F[Unit]

  def readYesNo(prompt: String): F[YesNo]
}

object ConsoleEffect {
  def apply[F[_]: ConsoleEffect]: ConsoleEffect[F] = implicitly[ConsoleEffect[F]]

  abstract class ConsoleEffectWithoutBind[F[_]: EffectConstructor] extends ConsoleEffect[F] {

    override def readLn: F[String] =
      EffectConstructor[F].effectOf(scala.io.StdIn.readLine)

    override def putStr(value: String): F[Unit] =
      EffectConstructor[F].effectOf(Console.out.print(value))

    override def putStrLn(value: String): F[Unit] =
      EffectConstructor[F].effectOf(Console.out.println(value))

    override def putErrStr(value: String): F[Unit] =
      EffectConstructor[F].effectOf(Console.err.print(value))

    override def putErrStrLn(value: String): F[Unit] =
      EffectConstructor[F].effectOf(Console.err.println(value))

  }
}