(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[310],{3905:function(e,n,r){"use strict";r.d(n,{Zo:function(){return f},kt:function(){return d}});var t=r(7294);function a(e,n,r){return n in e?Object.defineProperty(e,n,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[n]=r,e}function o(e,n){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var t=Object.getOwnPropertySymbols(e);n&&(t=t.filter((function(n){return Object.getOwnPropertyDescriptor(e,n).enumerable}))),r.push.apply(r,t)}return r}function c(e){for(var n=1;n<arguments.length;n++){var r=null!=arguments[n]?arguments[n]:{};n%2?o(Object(r),!0).forEach((function(n){a(e,n,r[n])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):o(Object(r)).forEach((function(n){Object.defineProperty(e,n,Object.getOwnPropertyDescriptor(r,n))}))}return e}function i(e,n){if(null==e)return{};var r,t,a=function(e,n){if(null==e)return{};var r,t,a={},o=Object.keys(e);for(t=0;t<o.length;t++)r=o[t],n.indexOf(r)>=0||(a[r]=e[r]);return a}(e,n);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(t=0;t<o.length;t++)r=o[t],n.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(a[r]=e[r])}return a}var l=t.createContext({}),u=function(e){var n=t.useContext(l),r=n;return e&&(r="function"==typeof e?e(n):c(c({},n),e)),r},f=function(e){var n=u(e.components);return t.createElement(l.Provider,{value:n},e.children)},s={inlineCode:"code",wrapper:function(e){var n=e.children;return t.createElement(t.Fragment,{},n)}},p=t.forwardRef((function(e,n){var r=e.components,a=e.mdxType,o=e.originalType,l=e.parentName,f=i(e,["components","mdxType","originalType","parentName"]),p=u(r),d=a,m=p["".concat(l,".").concat(d)]||p[d]||s[d]||o;return r?t.createElement(m,c(c({ref:n},f),{},{components:r})):t.createElement(m,c({ref:n},f))}));function d(e,n){var r=arguments,a=n&&n.mdxType;if("string"==typeof e||a){var o=r.length,c=new Array(o);c[0]=p;var i={};for(var l in n)hasOwnProperty.call(n,l)&&(i[l]=n[l]);i.originalType=e,i.mdxType="string"==typeof e?e:a,c[1]=i;for(var u=2;u<o;u++)c[u]=r[u];return t.createElement.apply(null,c)}return t.createElement.apply(null,r)}p.displayName="MDXCreateElement"},8906:function(e,n,r){"use strict";r.r(n),r.d(n,{frontMatter:function(){return i},contentTitle:function(){return l},metadata:function(){return u},toc:function(){return f},default:function(){return p}});var t=r(2122),a=r(9756),o=(r(7294),r(3905)),c=["components"],i={id:"can-handle-error",title:"CanHandleError"},l=void 0,u={unversionedId:"cats-effect/can-handle-error",id:"cats-effect/can-handle-error",isDocsHomePage:!1,title:"CanHandleError",description:"CanHandleError",source:"@site/../generated-docs/target/mdoc/cats-effect/can-handle-error.md",sourceDirName:"cats-effect",slug:"/cats-effect/can-handle-error",permalink:"/docs/cats-effect/can-handle-error",version:"current",frontMatter:{id:"can-handle-error",title:"CanHandleError"},sidebar:"someSidebar",previous:{title:"CanCatch",permalink:"/docs/cats-effect/can-catch"},next:{title:"FromFuture",permalink:"/docs/cats-effect/from-future"}},f=[{value:"CanHandleError",id:"canhandleerror",children:[]},{value:"CanHandleError.handleNonFatal",id:"canhandleerrorhandlenonfatal",children:[]}],s={toc:f};function p(e){var n=e.components,r=(0,a.Z)(e,c);return(0,o.kt)("wrapper",(0,t.Z)({},s,r,{components:n,mdxType:"MDXLayout"}),(0,o.kt)("h2",{id:"canhandleerror"},"CanHandleError"),(0,o.kt)("p",null,(0,o.kt)("inlineCode",{parentName:"p"},"CanHandleError")," is a typeclass to handle ",(0,o.kt)("inlineCode",{parentName:"p"},"NonFatal")," ",(0,o.kt)("inlineCode",{parentName:"p"},"Throwable")," and to recover from it.\nIt looks like this."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-scala"},"trait CanHandleError[F[_]] {\n\n  def handleNonFatalWith[A, AA >: A](\n      fa: => F[A]\n    )(\n      handleError: Throwable => F[AA]\n    ): F[AA]\n\n  def handleEitherTNonFatalWith[A, AA >: A, B, BB >: B](\n      efab: => EitherT[F, A, B]\n    )(\n      handleError: Throwable => F[Either[AA, BB]]\n    ): EitherT[F, AA, BB]\n\n  def handleNonFatal[A, AA >: A](\n      fa: => F[A]\n    )(\n      handleError: Throwable => AA\n    ): F[AA]\n\n  def handleEitherTNonFatal[A, AA >: A, B, BB >: B](\n      efab: => EitherT[F, A, B]\n    )(\n      handleError: Throwable => Either[AA, BB]\n    ): EitherT[F, AA, BB]\n\n}\n")),(0,o.kt)("p",null,"There are instances available for ",(0,o.kt)("inlineCode",{parentName:"p"},"cats.effect.IO"),", ",(0,o.kt)("inlineCode",{parentName:"p"},"scala.concurrent.Future")," and ",(0,o.kt)("inlineCode",{parentName:"p"},"cats.Id"),"."),(0,o.kt)("h2",{id:"canhandleerrorhandlenonfatal"},"CanHandleError.handleNonFatal"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-scala"},'import cats._\nimport cats.effect._\n\nimport effectie.cats._\nimport effectie.cats.Effectful._\n\nimport scala.util.control.NonFatal\n\nclass InvalidNumberException(n: Int, message: String) extends RuntimeException(message)\n\ndef foo(n: Int): Int = \n  if (n < 0)\n    throw new InvalidNumberException(n, s"n cannot be a negative Int. n: $n") \n  else\n     n\n\ndef bar[F[_]: Fx: CanHandleError](n: Int): F[Int] =\n  CanHandleError[F].handleNonFatalWith(effectOf(foo(n))) {\n    case NonFatal(err) =>\n      pureOf(0)\n  }\n  \nprintln(bar[IO](1).unsafeRunSync())\n// 1\nprintln(bar[IO](-1).unsafeRunSync())\n// 0\n \nprintln(bar[Id](1))\n// 1\nprintln(bar[Id](-1))\n// 0\n')),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-scala"},'import effectie.cats._\nimport effectie.cats.Effectful._\n\nimport scala.util.control.NonFatal\n\nimport effectie.concurrent.ExecutorServiceOps\nimport java.util.concurrent.{ExecutorService, Executors}\nimport scala.concurrent.{ExecutionContext, Future, Await}\nimport scala.concurrent.duration._\n\nobject MyApp {\n    \n  class InvalidNumberException(n: Int, message: String) extends RuntimeException(message)\n  \n  def foo(n: Int): Int = \n    if (n < 0)\n      throw new InvalidNumberException(n, s"n cannot be a negative Int. n: $n") \n    else\n       n\n  \n  def bar[F[_]: Fx: CanHandleError](n: Int): F[Int] =\n    CanHandleError[F].handleNonFatalWith(effectOf(foo(n))) {\n      case NonFatal(err) =>\n        pureOf(0)\n    }\n\n  def main(args: Array[String]): Unit = {\n    \n    val executorService: ExecutorService =\n      Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors() >> 1)\n    implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)\n    \n    try {\n      println(Await.result(bar[Future](1), 1.second))\n      println(Await.result(bar[Future](-1), 1.second))\n    } finally {\n      ExecutorServiceOps.shutdownAndAwaitTermination(executorService, 1.second)\n    }\n  }\n}\n\nMyApp.main(Array.empty)\n// 1\n// 0\n')))}p.isMDXComponent=!0}}]);