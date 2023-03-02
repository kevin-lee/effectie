package effectie.syntax

import cats.*
import cats.syntax.all.*
import effectie.core.{ConsoleFx, FxCtor, YesNo}

trait console {

  inline def readLn[F[*]: ConsoleFx]: F[String] = ConsoleFx[F].readLn

  inline def readPassword[F[*]: ConsoleFx]: F[Array[Char]] = ConsoleFx[F].readPassword

  inline def putStr[F[*]: ConsoleFx](value: String): F[Unit] = ConsoleFx[F].putStr(value)

  inline def putStrLn[F[*]: ConsoleFx](value: String): F[Unit] = ConsoleFx[F].putStrLn(value)

  inline def putErrStr[F[*]: ConsoleFx](value: String): F[Unit] = ConsoleFx[F].putErrStr(value)

  inline def putErrStrLn[F[*]: ConsoleFx](value: String): F[Unit] = ConsoleFx[F].putErrStrLn(value)

  inline def readYesNo[F[*]: ConsoleFx](prompt: String): F[YesNo] = ConsoleFx[F].readYesNo(prompt)

}

object console extends console
