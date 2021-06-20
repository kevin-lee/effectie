(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[899],{3905:function(e,t,r){"use strict";r.d(t,{Zo:function(){return l},kt:function(){return m}});var n=r(7294);function o(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function c(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function a(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?c(Object(r),!0).forEach((function(t){o(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):c(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function i(e,t){if(null==e)return{};var r,n,o=function(e,t){if(null==e)return{};var r,n,o={},c=Object.keys(e);for(n=0;n<c.length;n++)r=c[n],t.indexOf(r)>=0||(o[r]=e[r]);return o}(e,t);if(Object.getOwnPropertySymbols){var c=Object.getOwnPropertySymbols(e);for(n=0;n<c.length;n++)r=c[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(o[r]=e[r])}return o}var u=n.createContext({}),f=function(e){var t=n.useContext(u),r=t;return e&&(r="function"==typeof e?e(t):a(a({},t),e)),r},l=function(e){var t=f(e.components);return n.createElement(u.Provider,{value:t},e.children)},p={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},s=n.forwardRef((function(e,t){var r=e.components,o=e.mdxType,c=e.originalType,u=e.parentName,l=i(e,["components","mdxType","originalType","parentName"]),s=f(r),m=o,d=s["".concat(u,".").concat(m)]||s[m]||p[m]||c;return r?n.createElement(d,a(a({ref:t},l),{},{components:r})):n.createElement(d,a({ref:t},l))}));function m(e,t){var r=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var c=r.length,a=new Array(c);a[0]=s;var i={};for(var u in t)hasOwnProperty.call(t,u)&&(i[u]=t[u]);i.originalType=e,i.mdxType="string"==typeof e?e:o,a[1]=i;for(var f=2;f<c;f++)a[f]=r[f];return n.createElement.apply(null,a)}return n.createElement.apply(null,r)}s.displayName="MDXCreateElement"},1965:function(e,t,r){"use strict";r.r(t),r.d(t,{frontMatter:function(){return i},metadata:function(){return u},toc:function(){return f},default:function(){return p}});var n=r(2122),o=r(9756),c=(r(7294),r(3905)),a=["components"],i={id:"from-future",title:"FromFuture"},u={unversionedId:"cats-effect/from-future",id:"cats-effect/from-future",isDocsHomePage:!1,title:"FromFuture",description:"FromFuture",source:"@site/../generated-docs/target/mdoc/cats-effect/from-future.md",sourceDirName:"cats-effect",slug:"/cats-effect/from-future",permalink:"/docs/cats-effect/from-future",version:"current",frontMatter:{id:"from-future",title:"FromFuture"},sidebar:"someSidebar",previous:{title:"CanHandleError",permalink:"/docs/cats-effect/can-handle-error"},next:{title:"OptionTSupport",permalink:"/docs/cats-effect/optiont-support"}},f=[{value:"FromFuture",id:"fromfuture",children:[]},{value:"FromFuture.toEffect",id:"fromfuturetoeffect",children:[]}],l={toc:f};function p(e){var t=e.components,r=(0,o.Z)(e,a);return(0,c.kt)("wrapper",(0,n.Z)({},l,r,{components:t,mdxType:"MDXLayout"}),(0,c.kt)("h2",{id:"fromfuture"},"FromFuture"),(0,c.kt)("p",null,(0,c.kt)("inlineCode",{parentName:"p"},"FromFuture")," is a typeclass to convert ",(0,c.kt)("inlineCode",{parentName:"p"},"scala.concurrent.Future")," to an effect, ",(0,c.kt)("inlineCode",{parentName:"p"},"F[_]"),". So if there are some APIs returning ",(0,c.kt)("inlineCode",{parentName:"p"},"Future"),", it can be converted to ",(0,c.kt)("inlineCode",{parentName:"p"},"F[_]"),"."),(0,c.kt)("p",null,"There are three ",(0,c.kt)("inlineCode",{parentName:"p"},"FromFuture")," instances available."),(0,c.kt)("ul",null,(0,c.kt)("li",{parentName:"ul"},(0,c.kt)("inlineCode",{parentName:"li"},"FromFuture")," for ",(0,c.kt)("inlineCode",{parentName:"li"},"cats.effect.IO")),(0,c.kt)("li",{parentName:"ul"},(0,c.kt)("inlineCode",{parentName:"li"},"FromFuture")," for ",(0,c.kt)("inlineCode",{parentName:"li"},"scala.concurrent.Future")),(0,c.kt)("li",{parentName:"ul"},(0,c.kt)("inlineCode",{parentName:"li"},"FromFuture")," for ",(0,c.kt)("inlineCode",{parentName:"li"},"cats.Id"))),(0,c.kt)("pre",null,(0,c.kt)("code",{parentName:"pre",className:"language-scala"},"trait FromFuture[F[_]] {\n  def toEffect[A](future: => Future[A]): F[A]\n}\n")),(0,c.kt)("h2",{id:"fromfuturetoeffect"},"FromFuture.toEffect"),(0,c.kt)("pre",null,(0,c.kt)("code",{parentName:"pre",className:"language-scala"},"import cats._\nimport cats.syntax.all._\nimport cats.effect._\n\nimport effectie.cats._\nimport effectie.cats.Effectful._\n\nimport effectie.concurrent.ExecutorServiceOps\n\nimport java.util.concurrent.{ExecutorService, Executors}\nimport scala.concurrent.{ExecutionContext, Future}\nimport scala.concurrent.duration._\n\nobject MyApp {\n\n  def foo(n: Int)(implicit ec: ExecutionContext): Future[Int] =\n    Future(n + 100)\n\n  def bar[F[_]: EffectConstructor](n: Int): F[Int] =\n    pureOf(n * 2)\n\n  def baz[F[_]: Monad: EffectConstructor: FromFuture](n: Int)(implicit ec: ExecutionContext): F[Int] =\n    for {\n      a <- FromFuture[F].toEffect(foo(n))\n      b <- bar[F](a)\n    } yield b\n\n}\n\nval executorService: ExecutorService =\n  Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors() >> 1)\n// executorService: ExecutorService = java.util.concurrent.ForkJoinPool@5f1fca71[Terminated, parallelism = 1, size = 0, active = 0, running = 0, steals = 1, tasks = 0, submissions = 0]\nimplicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)\n// ec: ExecutionContext = scala.concurrent.impl.ExecutionContextImpl$$anon$4@70cb2928\nimplicit val cs: ContextShift[IO] = IO.contextShift(ec)\n// cs: ContextShift[IO] = cats.effect.internals.IOContextShift@beae096\n\ntry { \n  println(MyApp.baz[IO](1).unsafeRunSync())\n} finally {\n  ExecutorServiceOps.shutdownAndAwaitTermination(executorService, 1.second)\n}\n// 202\n")))}p.isMDXComponent=!0}}]);