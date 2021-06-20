(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[100],{3905:function(e,t,n){"use strict";n.d(t,{Zo:function(){return u},kt:function(){return h}});var r=n(7294);function i(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function o(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function a(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?o(Object(n),!0).forEach((function(t){i(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):o(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function p(e,t){if(null==e)return{};var n,r,i=function(e,t){if(null==e)return{};var n,r,i={},o=Object.keys(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||(i[n]=e[n]);return i}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(i[n]=e[n])}return i}var c=r.createContext({}),f=function(e){var t=r.useContext(c),n=t;return e&&(n="function"==typeof e?e(t):a(a({},t),e)),n},u=function(e){var t=f(e.components);return r.createElement(c.Provider,{value:t},e.children)},s={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},l=r.forwardRef((function(e,t){var n=e.components,i=e.mdxType,o=e.originalType,c=e.parentName,u=p(e,["components","mdxType","originalType","parentName"]),l=f(n),h=i,m=l["".concat(c,".").concat(h)]||l[h]||s[h]||o;return n?r.createElement(m,a(a({ref:t},u),{},{components:n})):r.createElement(m,a({ref:t},u))}));function h(e,t){var n=arguments,i=t&&t.mdxType;if("string"==typeof e||i){var o=n.length,a=new Array(o);a[0]=l;var p={};for(var c in t)hasOwnProperty.call(t,c)&&(p[c]=t[c]);p.originalType=e,p.mdxType="string"==typeof e?e:i,a[1]=p;for(var f=2;f<o;f++)a[f]=n[f];return r.createElement.apply(null,a)}return r.createElement.apply(null,n)}l.displayName="MDXCreateElement"},9485:function(e,t,n){"use strict";n.r(t),n.d(t,{frontMatter:function(){return p},metadata:function(){return c},toc:function(){return f},default:function(){return s}});var r=n(2122),i=n(9756),o=(n(7294),n(3905)),a=["components"],p={id:"eithert-support",title:"EitherTSupport"},c={unversionedId:"monix/eithert-support",id:"monix/eithert-support",isDocsHomePage:!1,title:"EitherTSupport",description:"EitherTSupport",source:"@site/../generated-docs/target/mdoc/monix/eithert-support.md",sourceDirName:"monix",slug:"/monix/eithert-support",permalink:"/docs/monix/eithert-support",version:"current",frontMatter:{id:"eithert-support",title:"EitherTSupport"},sidebar:"someSidebar",previous:{title:"OptionTSupport",permalink:"/docs/monix/optiont-support"},next:{title:"ConsoleEffect",permalink:"/docs/monix/console-effect"}},f=[{value:"EitherTSupport",id:"eithertsupport",children:[]}],u={toc:f};function s(e){var t=e.components,n=(0,i.Z)(e,a);return(0,o.kt)("wrapper",(0,r.Z)({},u,n,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("h2",{id:"eithertsupport"},"EitherTSupport"),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-scala"},'import cats._\nimport cats.syntax.all._\n\nimport effectie.monix.Effectful._\nimport effectie.monix._\nimport effectie.monix.EitherTSupport._\n\ntrait Something[F[_]] {\n  def foo(a: Int): F[Either[String, Int]]\n  def bar(a: Either[String, Int]): F[Either[String, Int]]\n}\n\nobject Something {\n  def apply[F[_]: Something]: Something[F] =\n    implicitly[Something[F]]\n\n  implicit def something[F[_]: EffectConstructor: Monad]: Something[F] =\n    new SomethingF[F]\n\n  final class SomethingF[F[_]: EffectConstructor: Monad]\n    extends Something[F] {\n\n    def foo(a: Int): F[Either[String, Int]] = (for {\n      x <- eitherTRightPure(a) // == EitherT.liftF(pureOf(a))\n      y <- eitherTRight(x + 10) // == EitherT.liftF(effectOf(x + 10))\n      y2 <- if (y > 100)\n          eitherTLeft("Error - Bigger than 100")\n        else\n          eitherTRightPure(y)\n        // \u2191 if (y > 100)\n        //     EitherT(effectOf("Error - Bigger than 100").map(_.asLeft[Int]))\n        //   else\n        //     EitherT(pureOf(y).map(_.asRight[String]))\n      z <- eitherTRightF[String](effectOf(y2 + 100)) // == EitherT.lieftF(effectOf(y + 100))\n    } yield z).value\n\n    def bar(a: Either[String, Int]): F[Either[String, Int]] = (for {\n      x <- eitherTOfPure(a) // == EitherT(pureOf(a: Either[String, Int]))\n      y <- eitherTOf((x + 999).asRight[String])  // == EitherT(effectOf((x + 999).asRight[String]))\n    } yield y).value\n  }\n\n}\n\nimport monix.eval._\nimport monix.execution.Scheduler.Implicits.global\n\nSomething[Task].foo(1).runSyncUnsafe()\n// res1: Either[String, Int] = Right(value = 111)\nSomething[Task].foo(10).runSyncUnsafe()\n// res2: Either[String, Int] = Right(value = 120)\n\nSomething[Task].bar(1.asRight[String]).runSyncUnsafe()\n// res3: Either[String, Int] = Right(value = 1000)\nSomething[Task].bar("No number".asLeft[Int]).runSyncUnsafe()\n// res4: Either[String, Int] = Left(value = "No number")\n')))}s.isMDXComponent=!0}}]);