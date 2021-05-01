(window.webpackJsonp=window.webpackJsonp||[]).push([[8],{102:function(e,t,n){"use strict";n.d(t,"a",(function(){return s})),n.d(t,"b",(function(){return g}));var r=n(0),i=n.n(r);function o(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function a(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function c(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?a(Object(n),!0).forEach((function(t){o(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):a(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function f(e,t){if(null==e)return{};var n,r,i=function(e,t){if(null==e)return{};var n,r,i={},o=Object.keys(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||(i[n]=e[n]);return i}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(i[n]=e[n])}return i}var p=i.a.createContext({}),u=function(e){var t=i.a.useContext(p),n=t;return e&&(n="function"==typeof e?e(t):c(c({},t),e)),n},s=function(e){var t=u(e.components);return i.a.createElement(p.Provider,{value:t},e.children)},l={inlineCode:"code",wrapper:function(e){var t=e.children;return i.a.createElement(i.a.Fragment,{},t)}},h=i.a.forwardRef((function(e,t){var n=e.components,r=e.mdxType,o=e.originalType,a=e.parentName,p=f(e,["components","mdxType","originalType","parentName"]),s=u(n),h=r,g=s["".concat(a,".").concat(h)]||s[h]||l[h]||o;return n?i.a.createElement(g,c(c({ref:t},p),{},{components:n})):i.a.createElement(g,c({ref:t},p))}));function g(e,t){var n=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var o=n.length,a=new Array(o);a[0]=h;var c={};for(var f in t)hasOwnProperty.call(t,f)&&(c[f]=t[f]);c.originalType=e,c.mdxType="string"==typeof e?e:r,a[1]=c;for(var p=2;p<o;p++)a[p]=n[p];return i.a.createElement.apply(null,a)}return i.a.createElement.apply(null,n)}h.displayName="MDXCreateElement"},78:function(e,t,n){"use strict";n.r(t),n.d(t,"frontMatter",(function(){return a})),n.d(t,"metadata",(function(){return c})),n.d(t,"toc",(function(){return f})),n.d(t,"default",(function(){return u}));var r=n(3),i=n(8),o=(n(0),n(102)),a={id:"eithert-support",title:"EitherTSupport"},c={unversionedId:"cats-effect/eithert-support",id:"cats-effect/eithert-support",isDocsHomePage:!1,title:"EitherTSupport",description:"EitherTSupport",source:"@site/../generated-docs/target/mdoc/cats-effect/eithert-support.md",sourceDirName:"cats-effect",slug:"/cats-effect/eithert-support",permalink:"/docs/cats-effect/eithert-support",version:"current",frontMatter:{id:"eithert-support",title:"EitherTSupport"},sidebar:"someSidebar",previous:{title:"OptionTSupport",permalink:"/docs/cats-effect/optiont-support"},next:{title:"ConsoleEffect",permalink:"/docs/cats-effect/console-effect"}},f=[{value:"EitherTSupport",id:"eithertsupport",children:[]}],p={toc:f};function u(e){var t=e.components,n=Object(i.a)(e,["components"]);return Object(o.b)("wrapper",Object(r.a)({},p,n,{components:t,mdxType:"MDXLayout"}),Object(o.b)("h2",{id:"eithertsupport"},"EitherTSupport"),Object(o.b)("pre",null,Object(o.b)("code",{parentName:"pre",className:"language-scala"},'import cats._\nimport cats.syntax.all._\n\nimport effectie.cats.Effectful._\nimport effectie.cats._\nimport effectie.cats.EitherTSupport._\n\ntrait Something[F[_]] {\n  def foo(a: Int): F[Either[String, Int]]\n  def bar(a: Either[String, Int]): F[Either[String, Int]]\n}\n\nobject Something {\n  def apply[F[_]: Something]: Something[F] =\n    implicitly[Something[F]]\n\n  implicit def something[F[_]: EffectConstructor: Monad]: Something[F] =\n    new SomethingF[F]\n\n  final class SomethingF[F[_]: EffectConstructor: Monad]\n    extends Something[F] {\n\n    def foo(a: Int): F[Either[String, Int]] = (for {\n      x <- eitherTRightPure(a) // == EitherT.liftF(pureOf(a))\n      y <- eitherTRight(x + 10) // == EitherT.liftF(effectOf(x + 10))\n      y2 <- if (y > 100)\n          eitherTLeft("Error - Bigger than 100")\n        else\n          eitherTRightPure(y)\n        // \u2191 if (y > 100)\n        //     EitherT(effectOf("Error - Bigger than 100").map(_.asLeft[Int]))\n        //   else\n        //     EitherT(pureOf(y).map(_.asRight[String]))\n      z <- eitherTRightF[String](effectOf(y2 + 100)) // == EitherT.lieftF(effectOf(y + 100))\n    } yield z).value\n\n    def bar(a: Either[String, Int]): F[Either[String, Int]] = (for {\n      x <- eitherTOfPure(a) // == EitherT(pureOf(a: Either[String, Int]))\n      y <- eitherTOf((x + 999).asRight[String])  // == EitherT(effectOf((x + 999).asRight[String]))\n    } yield y).value\n  }\n\n}\n\nimport cats.effect._\n\nSomething[IO].foo(1).unsafeRunSync()\n// res1: Either[String, Int] = Right(value = 111)\nSomething[IO].foo(10).unsafeRunSync()\n// res2: Either[String, Int] = Right(value = 120)\n\nSomething[IO].bar(1.asRight[String]).unsafeRunSync()\n// res3: Either[String, Int] = Right(value = 1000)\nSomething[IO].bar("No number".asLeft[Int]).unsafeRunSync()\n// res4: Either[String, Int] = Left(value = "No number")\n')))}u.isMDXComponent=!0}}]);