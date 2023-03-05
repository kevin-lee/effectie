package effectie.syntax

import effectie.core.{ConsoleFx, FxCtor, YesNo}

trait console {

  @inline def readLn[F[*]: FxCtor]: F[String] = ConsoleFx[F].readLn

  @inline def readPassword[F[*]: FxCtor]: F[Array[Char]] = ConsoleFx[F].readPassword

  @inline def putStr[F[*]: FxCtor](value: String): F[Unit] = ConsoleFx[F].putStr(value)

  @inline def putStrLn[F[*]: FxCtor](value: String): F[Unit] = ConsoleFx[F].putStrLn(value)

  @inline def putErrStr[F[*]: FxCtor](value: String): F[Unit] = ConsoleFx[F].putErrStr(value)

  @inline def putErrStrLn[F[*]: FxCtor](value: String): F[Unit] = ConsoleFx[F].putErrStrLn(value)

  @inline def readYesNo[F[*]: FxCtor](prompt: String): F[YesNo] = ConsoleFx[F].readYesNo(prompt)

}

object console extends console
