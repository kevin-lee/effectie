package effectie.core

/** @author Kevin Lee
  * @since 2023-02-23
  */
private[core] object compileTimeMessages {
  // $COVERAGE-OFF$
  final val ListOfFxInstances = // scalafix:ok DisableSyntax.noFinalVal
    """
  ---
  If you want to use IO from cats-effect 2, try effectie-cats-effect2.
    import effectie.instances.ce2.fx._

    // for Scala 3
    import effectie.instances.ce2.fx.given
    // or
    import effectie.instances.ce2.fx.ioFx

  For cats-effect 3, try effectie-cats-effect3.
    import effectie.instances.ce3.fx._

    // for Scala 3
    import effectie.instances.ce3.fx.given
    // or
    import effectie.instances.ce3.fx.ioFx

  If you want to use Task from Monix 3, try effectie-monix3.
    import effectie.instances.monix3.fx._

    // for Scala 3
    import effectie.instances.monix3.fx.given
    // or
    import effectie.instances.monix3.fx.taskFx

  For Scala's Future, It is just
    import effectie.instances.future.fx._

    // for Scala 3
    import effectie.instances.future.fx.given
    // or
    import effectie.instances.future.fx.futureFx

  If you don't want to use any effect but the raw data, you can use the instance for cats.Id
    import effectie.instances.id.fx._

    // for Scala 3
    import effectie.instances.id.fx.given
    // or
    import effectie.instances.id.fx.idFx
  ---
  """
  // $COVERAGE-ON$
}
