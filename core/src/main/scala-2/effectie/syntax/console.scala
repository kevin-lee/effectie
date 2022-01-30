package effectie.syntax

import effectie.core.{ConsoleEffect, YesNo}

trait console {

  @inline def readLn[F[_]: ConsoleEffect]: F[String] = ConsoleEffect[F].readLn

  @inline def readPassword[F[_]: ConsoleEffect]: F[Array[Char]] = ConsoleEffect[F].readPassword

  @inline def putStr[F[_]: ConsoleEffect](value: String): F[Unit] = ConsoleEffect[F].putStr(value)

  @inline def putStrLn[F[_]: ConsoleEffect](value: String): F[Unit] = ConsoleEffect[F].putStrLn(value)

  @inline def putErrStr[F[_]: ConsoleEffect](value: String): F[Unit] = ConsoleEffect[F].putErrStr(value)

  @inline def putErrStrLn[F[_]: ConsoleEffect](value: String): F[Unit] = ConsoleEffect[F].putErrStrLn(value)

  @inline def readYesNo[F[_]: ConsoleEffect](prompt: String): F[YesNo] = ConsoleEffect[F].readYesNo(prompt)

}

object console extends console
