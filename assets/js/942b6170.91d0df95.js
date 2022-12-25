"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[683],{3905:(e,t,r)=>{r.d(t,{Zo:()=>m,kt:()=>d});var n=r(7294);function o(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function i(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function a(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?i(Object(r),!0).forEach((function(t){o(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):i(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function u(e,t){if(null==e)return{};var r,n,o=function(e,t){if(null==e)return{};var r,n,o={},i=Object.keys(e);for(n=0;n<i.length;n++)r=i[n],t.indexOf(r)>=0||(o[r]=e[r]);return o}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(n=0;n<i.length;n++)r=i[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(o[r]=e[r])}return o}var c=n.createContext({}),l=function(e){var t=n.useContext(c),r=t;return e&&(r="function"==typeof e?e(t):a(a({},t),e)),r},m=function(e){var t=l(e.components);return n.createElement(c.Provider,{value:t},e.children)},p="mdxType",f={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},s=n.forwardRef((function(e,t){var r=e.components,o=e.mdxType,i=e.originalType,c=e.parentName,m=u(e,["components","mdxType","originalType","parentName"]),p=l(r),s=o,d=p["".concat(c,".").concat(s)]||p[s]||f[s]||i;return r?n.createElement(d,a(a({ref:t},m),{},{components:r})):n.createElement(d,a({ref:t},m))}));function d(e,t){var r=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var i=r.length,a=new Array(i);a[0]=s;var u={};for(var c in t)hasOwnProperty.call(t,c)&&(u[c]=t[c]);u.originalType=e,u[p]="string"==typeof e?e:o,a[1]=u;for(var l=2;l<i;l++)a[l]=r[l];return n.createElement.apply(null,a)}return n.createElement.apply(null,r)}s.displayName="MDXCreateElement"},3540:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>c,contentTitle:()=>a,default:()=>p,frontMatter:()=>i,metadata:()=>u,toc:()=>l});var n=r(7462),o=(r(7294),r(3905));const i={id:"from-future",title:"FromFuture"},a=void 0,u={unversionedId:"monix/from-future",id:"monix/from-future",title:"FromFuture",description:"FromFuture",source:"@site/../generated-docs/target/mdoc/monix/from-future.md",sourceDirName:"monix",slug:"/monix/from-future",permalink:"/docs/monix/from-future",draft:!1,tags:[],version:"current",frontMatter:{id:"from-future",title:"FromFuture"},sidebar:"someSidebar",previous:{title:"CanHandleError",permalink:"/docs/monix/can-handle-error"},next:{title:"OptionTSupport",permalink:"/docs/monix/optiont-support"}},c={},l=[{value:"FromFuture",id:"fromfuture",level:2},{value:"FromFuture.toEffect",id:"fromfuturetoeffect",level:2}],m={toc:l};function p(e){let{components:t,...r}=e;return(0,o.kt)("wrapper",(0,n.Z)({},m,r,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("h2",{id:"fromfuture"},"FromFuture"),(0,o.kt)("p",null,(0,o.kt)("inlineCode",{parentName:"p"},"FromFuture")," is a typeclass to convert ",(0,o.kt)("inlineCode",{parentName:"p"},"scala.concurrent.Future")," to an effect, ",(0,o.kt)("inlineCode",{parentName:"p"},"F[_]"),". So if there are some APIs returning ",(0,o.kt)("inlineCode",{parentName:"p"},"Future"),", it can be converted to ",(0,o.kt)("inlineCode",{parentName:"p"},"F[_]"),"."),(0,o.kt)("p",null,"There are three ",(0,o.kt)("inlineCode",{parentName:"p"},"FromFuture")," instances available."),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("inlineCode",{parentName:"li"},"FromFuture")," for ",(0,o.kt)("inlineCode",{parentName:"li"},"monix.eval.Task")),(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("inlineCode",{parentName:"li"},"FromFuture")," for ",(0,o.kt)("inlineCode",{parentName:"li"},"scala.concurrent.Future")),(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("inlineCode",{parentName:"li"},"FromFuture")," for ",(0,o.kt)("inlineCode",{parentName:"li"},"cats.Id"))),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-scala"},"trait FromFuture[F[_]] {\n  def toEffect[A](future: => Future[A]): F[A]\n}\n")),(0,o.kt)("h2",{id:"fromfuturetoeffect"},"FromFuture.toEffect"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-scala"},"import cats._\nimport cats.syntax.all._\nimport monix.eval._\n\nimport effectie.monix._\nimport effectie.monix.Effectful._\n\nimport effectie.concurrent.ExecutorServiceOps\n\nimport java.util.concurrent.{ExecutorService, Executors}\nimport scala.concurrent.{ExecutionContext, Future}\nimport scala.concurrent.duration._\n\nobject MyApp {\n\n  def foo(n: Int)(implicit ec: ExecutionContext): Future[Int] =\n    Future(n + 100)\n\n  def bar[F[_]: Fx](n: Int): F[Int] =\n    pureOf(n * 2)\n\n  def baz[F[_]: Monad: Fx: FromFuture](n: Int)(implicit ec: ExecutionContext): F[Int] =\n    for {\n      a <- FromFuture[F].toEffect(foo(n))\n      b <- bar[F](a)\n    } yield b\n\n}\n\nval executorService: ExecutorService =\n  Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors() >> 1)\n// executorService: ExecutorService = java.util.concurrent.ForkJoinPool@13f74ff[Terminated, parallelism = 1, size = 0, active = 0, running = 0, steals = 0, tasks = 0, submissions = 0]\nimplicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)\n// ec: ExecutionContext = scala.concurrent.impl.ExecutionContextImpl$$anon$4@5d541fc3\n\nimport monix.execution.Scheduler.Implicits.global\ntry {\n  println(MyApp.baz[Task](1).runSyncUnsafe())\n} finally {\n  ExecutorServiceOps.shutdownAndAwaitTermination(executorService, 1.second)\n}\n// 202\n")))}p.isMDXComponent=!0}}]);