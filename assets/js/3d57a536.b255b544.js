"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[7325],{3905:(e,t,r)=>{r.d(t,{Zo:()=>f,kt:()=>d});var n=r(7294);function o(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function a(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function i(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?a(Object(r),!0).forEach((function(t){o(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):a(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function c(e,t){if(null==e)return{};var r,n,o=function(e,t){if(null==e)return{};var r,n,o={},a=Object.keys(e);for(n=0;n<a.length;n++)r=a[n],t.indexOf(r)>=0||(o[r]=e[r]);return o}(e,t);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(n=0;n<a.length;n++)r=a[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(o[r]=e[r])}return o}var u=n.createContext({}),l=function(e){var t=n.useContext(u),r=t;return e&&(r="function"==typeof e?e(t):i(i({},t),e)),r},f=function(e){var t=l(e.components);return n.createElement(u.Provider,{value:t},e.children)},s="mdxType",p={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},m=n.forwardRef((function(e,t){var r=e.components,o=e.mdxType,a=e.originalType,u=e.parentName,f=c(e,["components","mdxType","originalType","parentName"]),s=l(r),m=o,d=s["".concat(u,".").concat(m)]||s[m]||p[m]||a;return r?n.createElement(d,i(i({ref:t},f),{},{components:r})):n.createElement(d,i({ref:t},f))}));function d(e,t){var r=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var a=r.length,i=new Array(a);i[0]=m;var c={};for(var u in t)hasOwnProperty.call(t,u)&&(c[u]=t[u]);c.originalType=e,c[s]="string"==typeof e?e:o,i[1]=c;for(var l=2;l<a;l++)i[l]=r[l];return n.createElement.apply(null,i)}return n.createElement.apply(null,r)}m.displayName="MDXCreateElement"},5950:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>u,contentTitle:()=>i,default:()=>p,frontMatter:()=>a,metadata:()=>c,toc:()=>l});var n=r(7462),o=(r(7294),r(3905));const a={sidebar_position:4,id:"from-future",title:"FromFuture"},i=void 0,c={unversionedId:"cats-effect2/from-future",id:"cats-effect2/from-future",title:"FromFuture",description:"FromFuture",source:"@site/../generated-docs/docs/cats-effect2/from-future.md",sourceDirName:"cats-effect2",slug:"/cats-effect2/from-future",permalink:"/docs/cats-effect2/from-future",draft:!1,tags:[],version:"current",sidebarPosition:4,frontMatter:{sidebar_position:4,id:"from-future",title:"FromFuture"},sidebar:"latestSidebar",previous:{title:"Fx Cheat sheet",permalink:"/docs/cats-effect2/fx/fx-cheat-sheet"},next:{title:"ConsoleFx",permalink:"/docs/cats-effect2/console-effect"}},u={},l=[{value:"FromFuture",id:"fromfuture",level:2},{value:"FromFuture.toEffect",id:"fromfuturetoeffect",level:2}],f={toc:l},s="wrapper";function p(e){let{components:t,...r}=e;return(0,o.kt)(s,(0,n.Z)({},f,r,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("h2",{id:"fromfuture"},"FromFuture"),(0,o.kt)("p",null,(0,o.kt)("inlineCode",{parentName:"p"},"FromFuture")," is a typeclass to convert ",(0,o.kt)("inlineCode",{parentName:"p"},"scala.concurrent.Future")," to an effect, ",(0,o.kt)("inlineCode",{parentName:"p"},"F[_]"),". So if there are some APIs returning ",(0,o.kt)("inlineCode",{parentName:"p"},"Future"),", it can be converted to ",(0,o.kt)("inlineCode",{parentName:"p"},"F[_]"),"."),(0,o.kt)("p",null,"There are three ",(0,o.kt)("inlineCode",{parentName:"p"},"FromFuture")," instances available."),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("inlineCode",{parentName:"li"},"FromFuture")," for ",(0,o.kt)("inlineCode",{parentName:"li"},"cats.effect.IO")),(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("inlineCode",{parentName:"li"},"FromFuture")," for ",(0,o.kt)("inlineCode",{parentName:"li"},"scala.concurrent.Future")),(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("inlineCode",{parentName:"li"},"FromFuture")," for ",(0,o.kt)("inlineCode",{parentName:"li"},"cats.Id"))),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-scala"},"trait FromFuture[F[_]] {\n  def toEffect[A](future: => Future[A]): F[A]\n}\n")),(0,o.kt)("h2",{id:"fromfuturetoeffect"},"FromFuture.toEffect"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-scala"},"import cats._\nimport cats.syntax.all._\nimport cats.effect._\n\nimport effectie.core._\nimport effectie.syntax.all._\n\nimport extras.concurrent.ExecutorServiceOps\n\nimport java.util.concurrent.{ExecutorService, Executors}\nimport scala.concurrent.{ExecutionContext, Future}\nimport scala.concurrent.duration._\n\nobject MyApp {\n\n  def foo(n: Int)(implicit ec: ExecutionContext): Future[Int] =\n    Future(n + 100)\n\n  def bar[F[_]: Fx](n: Int): F[Int] =\n    pureOf(n * 2)\n\n  def baz[F[_]: Monad: Fx: FromFuture](n: Int)(implicit ec: ExecutionContext): F[Int] =\n    for {\n      a <- FromFuture[F].toEffect(foo(n))\n      b <- bar[F](a)\n    } yield b\n\n}\n\nval executorService: ExecutorService =\n  Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors() >> 1)\n// executorService: ExecutorService = java.util.concurrent.ForkJoinPool@6f010d93[Terminated, parallelism = 1, size = 0, active = 0, running = 0, steals = 1, tasks = 0, submissions = 0]\nimplicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)\n// ec: ExecutionContext = scala.concurrent.impl.ExecutionContextImpl$$anon$4@15918823\nimplicit val cs: ContextShift[IO] = IO.contextShift(ec)\n// cs: ContextShift[IO] = cats.effect.internals.IOContextShift@9060f54\n\ntry {\n  import effectie.instances.ce2.fx._\n  import effectie.instances.ce2.fromFuture._\n  println(MyApp.baz[IO](1).unsafeRunSync())\n} finally {\n  ExecutorServiceOps.shutdownAndAwaitTermination(executorService, 1.second)\n}\n// 202\n")))}p.isMDXComponent=!0}}]);