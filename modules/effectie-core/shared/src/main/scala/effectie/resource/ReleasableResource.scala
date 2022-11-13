package effectie.resource

/** @author Kevin Lee
  * @since 2022-10-30
  */
trait ReleasableResource[F[*], A, EH[*[*]]] {

  def use[B](f: A => F[B])(implicit handleError: EH[F]): F[B]
}

object ReleasableResource {

  trait UnusedHandleError[F[*]]

  object UnusedHandleError {
    private val unusedHandleErrorSingleton: UnusedHandleError[Nothing] = new UnusedHandleError[Nothing] {}

    implicit def unusedHandleErrorF[F[*]]: UnusedHandleError[F] =
      unusedHandleErrorSingleton.asInstanceOf[UnusedHandleError[F]]
  }

}
