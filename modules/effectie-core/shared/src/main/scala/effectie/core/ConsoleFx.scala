package effectie.core

import scala.annotation.implicitNotFound

@implicitNotFound(
  """
  Could not find an implicit ConsoleEffect[${F}]. You can probably find it from the effectie.instances package.
  ---
  You can simply,
    import effectie.instances.console._
    // for Scala 3
    import effectie.instances.console.*

    // then probably need to import Fx or FxCtor instances from one of
    - effectie.instances.ce2
    - effectie.instances.ce3
    - effectie.instances.monix3
    - effectie.instances.future
    - effectie.instances.id
    depending on what effect you want to use

    e.g.)
    If you want to use IO from cats-effect 2, try effectie-cats-effect2.
    import effectie.instances.ce2.fx._
    // for Scala 3
    import effectie.instances.ce2.fx.given

    For cats-effect 3, try effectie-cats-effect3.
      import effectie.instances.ce3.fx._
      // for Scala 3
      import effectie.instances.ce3.fx.given

    If you want to use Task from Monix 3, try effectie-monix3.
      import effectie.instances.monix3.fx._
      // for Scala 3
      import effectie.instances.monix3.fx.given

    For Scala's Future, It is just
      import effectie.instances.future.fx._
      // for Scala 3
      import effectie.instances.future.fx.given

    If you don't want to use any effect but the raw data, you can use the instance for cats.Id
      import effectie.instances.id.fx._
      // for Scala 3
      import effectie.instances.id.fx.given
  ---
  """
)
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
  def apply[F[*]: ConsoleFx]: ConsoleFx[F] = implicitly[ConsoleFx[F]]

  abstract class ConsoleFxWithoutFlatMap[F[*]: FxCtor] extends ConsoleFx[F] {

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
  }
}
