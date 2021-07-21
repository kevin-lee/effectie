package effectie.scalaz

import scalaz._
import Scalaz._

trait OptionTSupport {

  import OptionTSupport._

  def optionTOf[F[_]]: PartiallyAppliedOptionTOf[F] =
    new PartiallyAppliedOptionTOf[F]

  def optionTOfPure[F[_]]: PartiallyAppliedOptionTOfPure[F] =
    new PartiallyAppliedOptionTOfPure[F]

  def optionTSome[F[_]]: PartiallyAppliedOptionTSome[F] =
    new PartiallyAppliedOptionTSome[F]

  def optionTSomePure[F[_]]: PartiallyAppliedOptionTSomePure[F] =
    new PartiallyAppliedOptionTSomePure[F]

  def optionTNone[F[_]: Applicative, A]: OptionT[F, A] =
    noneT[F, A]

  def noneT[F[_]: Applicative, A]: OptionT[F, A] =
    OptionT.none[F, A]

  def optionTSomeF[F[_]: Functor, A](fa: F[A]): OptionT[F, A] =
    OptionT[F, A](fa.map(_.some))

}

@SuppressWarnings(Array("org.wartremover.warts.DefaultArguments"))
object OptionTSupport extends OptionTSupport {

  private[OptionTSupport] final class PartiallyAppliedOptionTOf[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: => Option[A])(implicit EF: Eft[F]): OptionT[F, A] =
      OptionT(Eft[F].effectOf(a))
  }

  private[OptionTSupport] final class PartiallyAppliedOptionTOfPure[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: Option[A])(implicit EF: Eft[F]): OptionT[F, A] =
      OptionT(Eft[F].pureOf(a))
  }

  private[OptionTSupport] final class PartiallyAppliedOptionTSome[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: => A)(implicit EC: Eft[F], FT: Functor[F]): OptionT[F, A] =
      OptionT(EC.effectOf(a).map(_.some))
  }

  private[OptionTSupport] final class PartiallyAppliedOptionTSomePure[F[_]](
    private val dummy: Boolean = true
  ) extends AnyVal {
    def apply[A](a: A)(implicit EC: Eft[F], FT: Functor[F]): OptionT[F, A] =
      OptionT(EC.pureOf(a).map(_.some))
  }

  implicit final class OptionTFOptionOps[F[_], A](private val fOfOption: F[Option[A]]) extends AnyVal {
    def optionT: OptionT[F, A] = OptionT[F, A](fOfOption)
  }

  implicit final class OptionTOptionOps[A](private val option: Option[A]) extends AnyVal {
    def optionT[F[_]: Applicative]: OptionT[F, A] = OptionT(Applicative[F].pure(option))
  }

  implicit final class OptionTFAOps[F[_], A](private val fa: F[A]) extends AnyVal {
    def someT(implicit F: Functor[F]): OptionT[F, A] = OptionT(fa.map(Option(_)))
  }

  implicit final class OptionTAOps[A](private val a: A) extends AnyVal {
    def someTF[F[_]: Applicative]: OptionT[F, A] = OptionT.some(a)
  }

}
