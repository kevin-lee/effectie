"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[3678],{3905:(e,t,n)=>{n.d(t,{Zo:()=>f,kt:()=>h});var r=n(7294);function i(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function o(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function a(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?o(Object(n),!0).forEach((function(t){i(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):o(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function p(e,t){if(null==e)return{};var n,r,i=function(e,t){if(null==e)return{};var n,r,i={},o=Object.keys(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||(i[n]=e[n]);return i}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(i[n]=e[n])}return i}var s=r.createContext({}),c=function(e){var t=r.useContext(s),n=t;return e&&(n="function"==typeof e?e(t):a(a({},t),e)),n},f=function(e){var t=c(e.components);return r.createElement(s.Provider,{value:t},e.children)},l="mdxType",u={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},m=r.forwardRef((function(e,t){var n=e.components,i=e.mdxType,o=e.originalType,s=e.parentName,f=p(e,["components","mdxType","originalType","parentName"]),l=c(n),m=i,h=l["".concat(s,".").concat(m)]||l[m]||u[m]||o;return n?r.createElement(h,a(a({ref:t},f),{},{components:n})):r.createElement(h,a({ref:t},f))}));function h(e,t){var n=arguments,i=t&&t.mdxType;if("string"==typeof e||i){var o=n.length,a=new Array(o);a[0]=m;var p={};for(var s in t)hasOwnProperty.call(t,s)&&(p[s]=t[s]);p.originalType=e,p[l]="string"==typeof e?e:i,a[1]=p;for(var c=2;c<o;c++)a[c]=n[c];return r.createElement.apply(null,a)}return r.createElement.apply(null,n)}m.displayName="MDXCreateElement"},5736:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>s,contentTitle:()=>a,default:()=>u,frontMatter:()=>o,metadata:()=>p,toc:()=>c});var r=n(7462),i=(n(7294),n(3905));const o={sidebar_position:6,id:"eithert-support",title:"EitherTSupport"},a=void 0,p={unversionedId:"docs/monix/eithert-support",id:"version-v1/docs/monix/eithert-support",title:"EitherTSupport",description:"EitherTSupport",source:"@site/versioned_docs/version-v1/docs/monix/eithert-support.md",sourceDirName:"docs/monix",slug:"/docs/monix/eithert-support",permalink:"/docs/v1/docs/monix/eithert-support",draft:!1,tags:[],version:"v1",sidebarPosition:6,frontMatter:{sidebar_position:6,id:"eithert-support",title:"EitherTSupport"},sidebar:"version-v1/docs",previous:{title:"OptionTSupport",permalink:"/docs/v1/docs/monix/optiont-support"},next:{title:"ConsoleEffect",permalink:"/docs/v1/docs/monix/console-effect"}},s={},c=[{value:"EitherTSupport",id:"eithertsupport",level:2}],f={toc:c},l="wrapper";function u(e){let{components:t,...n}=e;return(0,i.kt)(l,(0,r.Z)({},f,n,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("h2",{id:"eithertsupport"},"EitherTSupport"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},'import cats._\nimport cats.syntax.all._\n\nimport effectie.monix.Effectful._\nimport effectie.monix._\nimport effectie.monix.EitherTSupport._\n\ntrait Something[F[_]] {\n  def foo(a: Int): F[Either[String, Int]]\n  def bar(a: Either[String, Int]): F[Either[String, Int]]\n}\n\nobject Something {\n  def apply[F[_]: Something]: Something[F] =\n    implicitly[Something[F]]\n\n  implicit def something[F[_]: Fx: Monad]: Something[F] =\n    new SomethingF[F]\n\n  final class SomethingF[F[_]: Fx: Monad]\n    extends Something[F] {\n\n    def foo(a: Int): F[Either[String, Int]] = (for {\n      x <- a.rightTF[F, String] // == EitherT.liftF(Applicative[F].pure(a))\n      y <- (x + 10).rightTF[F, String] // == EitherT.liftF(Applicative[F].pure(x + 10))\n      y2 <- if (y > 100)\n              "Error - Bigger than 100".leftTF[F, Int]\n            else\n              y.rightTF[F, String]\n       // \u2191 if (y > 100)\n       //     EitherT(pureOf("Error - Bigger than 100").map(_.asLeft[Int]))\n       //   else\n       //     EitherT(pureOf(y).map(_.asRight[String]))\n      z <- effectOf(y2 + 100).rightT[String] // == EitherT.lieftF(effectOf(y + 100))\n    } yield z).value\n\n    def bar(a: Either[String, Int]): F[Either[String, Int]] = (for {\n      x <- a.eitherT[F] // == EitherT(pureOf(a: Either[String, Int]))\n      y <- effectOf((x + 999).asRight[String]).eitherT  // == EitherT(effectOf((x + 999).asRight[String]))\n    } yield y).value\n  }\n\n}\n\nimport monix.eval._\nimport monix.execution.Scheduler.Implicits.global\n\nSomething[Task].foo(1).runSyncUnsafe()\n// res1: Either[String, Int] = Right(value = 111)\nSomething[Task].foo(10).runSyncUnsafe()\n// res2: Either[String, Int] = Right(value = 120)\n\nSomething[Task].bar(1.asRight[String]).runSyncUnsafe()\n// res3: Either[String, Int] = Right(value = 1000)\nSomething[Task].bar("No number".asLeft[Int]).runSyncUnsafe()\n// res4: Either[String, Int] = Left(value = "No number")\n')))}u.isMDXComponent=!0}}]);