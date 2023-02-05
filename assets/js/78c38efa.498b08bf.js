"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[4201],{3905:(e,t,n)=>{n.d(t,{Zo:()=>m,kt:()=>d});var r=n(7294);function o(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function i(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function a(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?i(Object(n),!0).forEach((function(t){o(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function u(e,t){if(null==e)return{};var n,r,o=function(e,t){if(null==e)return{};var n,r,o={},i=Object.keys(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||(o[n]=e[n]);return o}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(o[n]=e[n])}return o}var c=r.createContext({}),l=function(e){var t=r.useContext(c),n=t;return e&&(n="function"==typeof e?e(t):a(a({},t),e)),n},m=function(e){var t=l(e.components);return r.createElement(c.Provider,{value:t},e.children)},p="mdxType",f={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},s=r.forwardRef((function(e,t){var n=e.components,o=e.mdxType,i=e.originalType,c=e.parentName,m=u(e,["components","mdxType","originalType","parentName"]),p=l(n),s=o,d=p["".concat(c,".").concat(s)]||p[s]||f[s]||i;return n?r.createElement(d,a(a({ref:t},m),{},{components:n})):r.createElement(d,a({ref:t},m))}));function d(e,t){var n=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var i=n.length,a=new Array(i);a[0]=s;var u={};for(var c in t)hasOwnProperty.call(t,c)&&(u[c]=t[c]);u.originalType=e,u[p]="string"==typeof e?e:o,a[1]=u;for(var l=2;l<i;l++)a[l]=n[l];return r.createElement.apply(null,a)}return r.createElement.apply(null,n)}s.displayName="MDXCreateElement"},6303:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>c,contentTitle:()=>a,default:()=>f,frontMatter:()=>i,metadata:()=>u,toc:()=>l});var r=n(7462),o=(n(7294),n(3905));const i={sidebar_position:2,id:"from-future",title:"FromFuture"},a=void 0,u={unversionedId:"monix3/from-future",id:"monix3/from-future",title:"FromFuture",description:"FromFuture",source:"@site/../generated-docs/docs/monix3/from-future.md",sourceDirName:"monix3",slug:"/monix3/from-future",permalink:"/docs/monix3/from-future",draft:!1,tags:[],version:"current",sidebarPosition:2,frontMatter:{sidebar_position:2,id:"from-future",title:"FromFuture"},sidebar:"latestSidebar",previous:{title:"Fx",permalink:"/docs/monix3/fx"}},c={},l=[{value:"FromFuture",id:"fromfuture",level:2},{value:"FromFuture.toEffect",id:"fromfuturetoeffect",level:2}],m={toc:l},p="wrapper";function f(e){let{components:t,...n}=e;return(0,o.kt)(p,(0,r.Z)({},m,n,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("h2",{id:"fromfuture"},"FromFuture"),(0,o.kt)("p",null,(0,o.kt)("inlineCode",{parentName:"p"},"FromFuture")," is a typeclass to convert ",(0,o.kt)("inlineCode",{parentName:"p"},"scala.concurrent.Future")," to an effect, ",(0,o.kt)("inlineCode",{parentName:"p"},"F[_]"),". So if there are some APIs returning ",(0,o.kt)("inlineCode",{parentName:"p"},"Future"),", it can be converted to ",(0,o.kt)("inlineCode",{parentName:"p"},"F[_]"),"."),(0,o.kt)("p",null,"There are three ",(0,o.kt)("inlineCode",{parentName:"p"},"FromFuture")," instances available."),(0,o.kt)("ul",null,(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("inlineCode",{parentName:"li"},"FromFuture")," for ",(0,o.kt)("inlineCode",{parentName:"li"},"monix.eval.Task")),(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("inlineCode",{parentName:"li"},"FromFuture")," for ",(0,o.kt)("inlineCode",{parentName:"li"},"scala.concurrent.Future")),(0,o.kt)("li",{parentName:"ul"},(0,o.kt)("inlineCode",{parentName:"li"},"FromFuture")," for ",(0,o.kt)("inlineCode",{parentName:"li"},"cats.Id"))),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-scala"},"trait FromFuture[F[_]] {\n  def toEffect[A](future: => Future[A]): F[A]\n}\n")),(0,o.kt)("h2",{id:"fromfuturetoeffect"},"FromFuture.toEffect"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-scala"},"import cats._\nimport cats.syntax.all._\nimport monix.eval._\n\nimport effectie.core._\nimport effectie.syntax.all._\n\nimport extras.concurrent.ExecutorServiceOps\n\nimport java.util.concurrent.{ExecutorService, Executors}\nimport scala.concurrent.{ExecutionContext, Future}\nimport scala.concurrent.duration._\n\nobject MyApp {\n\n  def foo(n: Int)(implicit ec: ExecutionContext): Future[Int] =\n    Future(n + 100)\n\n  def bar[F[_]: Fx](n: Int): F[Int] =\n    pureOf(n * 2)\n\n  def baz[F[_]: Monad: Fx: FromFuture](n: Int)(implicit ec: ExecutionContext): F[Int] =\n    for {\n      a <- FromFuture[F].toEffect(foo(n))\n      b <- bar[F](a)\n    } yield b\n\n}\n\nval executorService: ExecutorService =\n  Executors.newWorkStealingPool(Runtime.getRuntime.availableProcessors() >> 1)\n// executorService: ExecutorService = java.util.concurrent.ForkJoinPool@61e6dfeb[Terminated, parallelism = 1, size = 0, active = 0, running = 0, steals = 0, tasks = 0, submissions = 0]\nimplicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(executorService)\n// ec: ExecutionContext = scala.concurrent.impl.ExecutionContextImpl$$anon$4@588b671e\n\nimport monix.execution.Scheduler.Implicits.global\ntry {\n  import effectie.instances.monix3.fx._\n  import effectie.instances.monix3.fromFuture._\n  println(MyApp.baz[Task](1).runSyncUnsafe())\n} finally {\n  ExecutorServiceOps.shutdownAndAwaitTermination(executorService, 1.second)\n}\n// 202\n")))}f.isMDXComponent=!0}}]);