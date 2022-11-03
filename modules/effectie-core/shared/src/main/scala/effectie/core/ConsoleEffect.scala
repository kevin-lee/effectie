package effectie.core

import scala.annotation.implicitNotFound

@implicitNotFound(
  """
  Could not find an implicit ConsoleEffect[${F}]. You can probably find it from the effectie.instance package.
  ---
  You can simply,
    import effectie.instances.console._
    // for Scala 3
    import effectie.instances.console.*

    // then probably need to import Fx or FxCtor instances from one of
    - effectie.instance.ce2
    - effectie.instance.ce3
    - effectie.instance.monix3
    - effectie.instance.future
    - effectie.instance.id
    depending on what effect you want to use

    e.g.)
    If you want to use IO from cats-effect 2, try effectie-cats-effect2.
    import effectie.instances.ce2.fx._
    // for Scala 3
    import effectie.instances.ce2.fx.*

    For cats-effect 3, try effectie-cats-effect3.
      import effectie.instances.ce3.fx._
      // for Scala 3
      import effectie.instances.ce3.fx.*

    If you want to use Task from Monix 3, try effectie-monix3.
      import effectie.instances.monix3.fx._
      // for Scala 3
      import effectie.instances.monix3.fx.*

    For Scala's Future, It is just
      import effectie.instances.future.fx._
      // for Scala 3
      import effectie.instances.future.fx.*

    If you don't want to use any effect but the raw data, you can use the instance for cats.Id
      import effectie.instances.id.fx._
      // for Scala 3
      import effectie.instances.id.fx.*
  ---
  """
)
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
