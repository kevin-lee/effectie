"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[27],{3905:(e,t,r)=>{r.d(t,{Zo:()=>l,kt:()=>m});var n=r(7294);function i(e,t,r){return t in e?Object.defineProperty(e,t,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[t]=r,e}function o(e,t){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),r.push.apply(r,n)}return r}function a(e){for(var t=1;t<arguments.length;t++){var r=null!=arguments[t]?arguments[t]:{};t%2?o(Object(r),!0).forEach((function(t){i(e,t,r[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):o(Object(r)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(r,t))}))}return e}function c(e,t){if(null==e)return{};var r,n,i=function(e,t){if(null==e)return{};var r,n,i={},o=Object.keys(e);for(n=0;n<o.length;n++)r=o[n],t.indexOf(r)>=0||(i[r]=e[r]);return i}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(n=0;n<o.length;n++)r=o[n],t.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(i[r]=e[r])}return i}var p=n.createContext({}),f=function(e){var t=n.useContext(p),r=t;return e&&(r="function"==typeof e?e(t):a(a({},t),e)),r},l=function(e){var t=f(e.components);return n.createElement(p.Provider,{value:t},e.children)},s="mdxType",u={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},g=n.forwardRef((function(e,t){var r=e.components,i=e.mdxType,o=e.originalType,p=e.parentName,l=c(e,["components","mdxType","originalType","parentName"]),s=f(r),g=i,m=s["".concat(p,".").concat(g)]||s[g]||u[g]||o;return r?n.createElement(m,a(a({ref:t},l),{},{components:r})):n.createElement(m,a({ref:t},l))}));function m(e,t){var r=arguments,i=t&&t.mdxType;if("string"==typeof e||i){var o=r.length,a=new Array(o);a[0]=g;var c={};for(var p in t)hasOwnProperty.call(t,p)&&(c[p]=t[p]);c.originalType=e,c[s]="string"==typeof e?e:i,a[1]=c;for(var f=2;f<o;f++)a[f]=r[f];return n.createElement.apply(null,a)}return n.createElement.apply(null,r)}g.displayName="MDXCreateElement"},612:(e,t,r)=>{r.r(t),r.d(t,{assets:()=>p,contentTitle:()=>a,default:()=>s,frontMatter:()=>o,metadata:()=>c,toc:()=>f});var n=r(7462),i=(r(7294),r(3905));const o={layout:"docs",title:"EitherTSupport"},a=void 0,c={unversionedId:"scalaz-effect/eithert-support",id:"scalaz-effect/eithert-support",title:"EitherTSupport",description:"EitherTSupport",source:"@site/../generated-docs/target/mdoc/scalaz-effect/eithert-support.md",sourceDirName:"scalaz-effect",slug:"/scalaz-effect/eithert-support",permalink:"/docs/scalaz-effect/eithert-support",draft:!1,tags:[],version:"current",frontMatter:{layout:"docs",title:"EitherTSupport"},sidebar:"someSidebar",previous:{title:"OptionTSupport",permalink:"/docs/scalaz-effect/optiont-support"},next:{title:"ConsoleEffect",permalink:"/docs/scalaz-effect/console-effect"}},p={},f=[{value:"EitherTSupport",id:"eithertsupport",level:2}],l={toc:f};function s(e){let{components:t,...r}=e;return(0,i.kt)("wrapper",(0,n.Z)({},l,r,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("h2",{id:"eithertsupport"},"EitherTSupport"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},'import scalaz._\nimport Scalaz._\n\nimport effectie.scalaz.Effectful._\nimport effectie.scalaz._\nimport effectie.scalaz.EitherTSupport._\n\ntrait Something[F[_]] {\n  def foo(a: Int): F[String \\/ Int]\n  def bar(a: String \\/ Int): F[String \\/ Int]\n}\n\nobject Something {\n  def apply[F[_]: Something]: Something[F] =\n    implicitly[Something[F]]\n\n  implicit def something[F[_]: Fx: Monad]: Something[F] =\n    new SomethingF[F]\n\n  final class SomethingF[F[_]: Fx: Monad]\n    extends Something[F] {\n\n    def foo(a: Int): F[String \\/ Int] = (for {\n      x <- a.rightTF[F, String] // == EitherT(Applicative[F].pure(a).map(_.right[String]))\n      y <- (x + 10).rightTF[F, String] // == EitherT(Applicative[F].pure(x + 10).map(_.right[String]))\n      y2 <- if (y > 100)\n              eitherTLeft[Int]("Error - Bigger than 100")\n            else\n              eitherTRightPure[String](y)\n       // \u2191 if (y > 100)\n       //     EitherT(pureOF("Error - Bigger than 100").map(_.left[Int]))\n       //   else\n       //     EitherT(pureOf(y).map(_.right[String]))\n      z <- effectOf(y2 + 100).rightT[String] // == EitherT(effectOf(y + 100).map(_.right))\n    } yield z).run\n\n    def bar(a: String \\/ Int): F[String \\/ Int] = (for {\n      x <- a.eitherT[F] // == EitherT(pureOf(a: String \\/ Int))\n      y <- effectOf((x + 999).right[String]).eitherT  // == EitherT(effectOf((x + 999).right[String]))\n    } yield y).run\n  }\n\n}\n\nimport scalaz.effect._\n\nSomething[IO].foo(1).unsafePerformIO()\n// res1: String \\/ Int = \\/-(b = 111)\nSomething[IO].foo(10).unsafePerformIO()\n// res2: String \\/ Int = \\/-(b = 120)\n\nSomething[IO].bar(1.right[String]).unsafePerformIO()\n// res3: String \\/ Int = \\/-(b = 1000)\nSomething[IO].bar("No number".left[Int]).unsafePerformIO()\n// res4: String \\/ Int = -\\/(a = "No number")\n')))}s.isMDXComponent=!0}}]);