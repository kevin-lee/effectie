(window.webpackJsonp=window.webpackJsonp||[]).push([[11],{147:function(e,t,n){"use strict";n.r(t),n.d(t,"frontMatter",(function(){return i})),n.d(t,"metadata",(function(){return c})),n.d(t,"rightToc",(function(){return f})),n.d(t,"default",(function(){return p}));var r=n(2),a=n(9),o=(n(0),n(158)),i={id:"cats-effect",title:"For Cats Effect"},c={id:"cats-effect/cats-effect",isDocsHomePage:!1,title:"For Cats Effect",description:"Effectie for Cats Effect",source:"@site/../generated-docs/target/mdoc/cats-effect/cats-effect.md",permalink:"/docs/cats-effect/cats-effect",sidebar:"someSidebar",previous:{title:"Getting Started",permalink:"/docs/"},next:{title:"EffectConstructor - Cats",permalink:"/docs/cats-effect/effect-constructor"}},f=[{value:"Effectie for Cats Effect",id:"effectie-for-cats-effect",children:[]},{value:"All in One Example",id:"all-in-one-example",children:[]}],l={rightToc:f};function p(e){var t=e.components,n=Object(a.a)(e,["components"]);return Object(o.b)("wrapper",Object(r.a)({},l,n,{components:t,mdxType:"MDXLayout"}),Object(o.b)("h2",{id:"effectie-for-cats-effect"},"Effectie for Cats Effect"),Object(o.b)("ul",null,Object(o.b)("li",{parentName:"ul"},Object(o.b)("a",Object(r.a)({parentName:"li"},{href:"effect-constructor"}),"EffectConstructor")),Object(o.b)("li",{parentName:"ul"},Object(o.b)("a",Object(r.a)({parentName:"li"},{href:"console-effect"}),"ConsoleEffect")),Object(o.b)("li",{parentName:"ul"},Object(o.b)("a",Object(r.a)({parentName:"li"},{href:"can-catch"}),"CanCatch")),Object(o.b)("li",{parentName:"ul"},Object(o.b)("a",Object(r.a)({parentName:"li"},{href:"optiont-support"}),"OptionTSupport")),Object(o.b)("li",{parentName:"ul"},Object(o.b)("a",Object(r.a)({parentName:"li"},{href:"eithert-support"}),"EitherTSupport"))),Object(o.b)("h2",{id:"all-in-one-example"},"All in One Example"),Object(o.b)("pre",null,Object(o.b)("code",Object(r.a)({parentName:"pre"},{className:"language-scala"}),'import cats._\nimport cats.implicits._\nimport cats.effect._\n\nimport effectie.ConsoleEffectful._\nimport effectie.Effectful._\n\nimport effectie.cats.EitherTSupport._\nimport effectie.cats.OptionTSupport._\nimport effectie.cats._\n\ntrait Something[F[_]] {\n  def foo[A: Semigroup](a: A): F[A]\n  def bar[A: Semigroup](a: Option[A]): F[Option[A]]\n  def baz[A, B: Semigroup](a: Either[A, B]): F[Either[A, B]]\n}\n\nobject Something {\n\n  def apply[F[_]: Something]: Something[F] =\n    implicitly[Something[F]]\n\n  implicit def something[F[_]: EffectConstructor: ConsoleEffect: Monad]: Something[F] =\n    new SomethingF[F]\n\n  final class SomethingF[F[_]: EffectConstructor: ConsoleEffect: Monad]\n    extends Something[F] {\n\n    override def foo[A: Semigroup](a: A): F[A] =\n      for {\n        n <- effectOf(a)\n        blah <- effectOfPure("blah blah")\n        _ <- effectOf(println(s"n: $n / BLAH: $blah"))\n        x <- effectOf(n |+| n)\n        _ <- putStrLn(s"x: $x")\n      } yield x\n\n    override def bar[A: Semigroup](a: Option[A]): F[Option[A]] =\n      (for {\n        a <- optionTOfPure(a)\n        blah <- optionTOfPure("blah blah".some)\n        _ <- optionTSome(println(s"a: $a / BLAH: $blah"))\n        x <- optionTSomeF(effectOf(a |+| a))\n        _ <- optionTSomeF(putStrLn(s"x: $x"))\n      } yield x).value\n\n    override def baz[A, B: Semigroup](ab: Either[A, B]): F[Either[A, B]] =\n      (for {\n        b <- eitherTOf(ab)\n        blah <- eitherTOfPure("blah blah".asRight[A])\n        _ <- eitherTRight(println(s"b: $b / BLAH: $blah"))\n        x <- eitherTRightF(effectOf(b |+| b))\n        _ <- eitherTRightF[A](putStrLn(s"x: $x"))\n      } yield x).value\n  }\n}\n\nprintln(Something[IO].foo(1).unsafeRunSync())\n// n: 1 / BLAH: blah blah\n// x: 2\n// 2\n\nprintln(Something[IO].bar(2.some).unsafeRunSync())\n// a: 2 / BLAH: blah blah\n// x: 4\n// Some(4)\nprintln(Something[IO].bar(none[String]).unsafeRunSync())\n// None\n\nprintln(Something[IO].baz(2.asRight[String]).unsafeRunSync())\n// b: 2 / BLAH: blah blah\n// x: 4\n// Right(4)\nprintln(Something[IO].baz("ERROR!!!".asLeft[Int]).unsafeRunSync())\n// Left(ERROR!!!)\n')))}p.isMDXComponent=!0},158:function(e,t,n){"use strict";n.d(t,"a",(function(){return u})),n.d(t,"b",(function(){return m}));var r=n(0),a=n.n(r);function o(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function i(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function c(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?i(Object(n),!0).forEach((function(t){o(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function f(e,t){if(null==e)return{};var n,r,a=function(e,t){if(null==e)return{};var n,r,a={},o=Object.keys(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||(a[n]=e[n]);return a}(e,t);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);for(r=0;r<o.length;r++)n=o[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(a[n]=e[n])}return a}var l=a.a.createContext({}),p=function(e){var t=a.a.useContext(l),n=t;return e&&(n="function"==typeof e?e(t):c(c({},t),e)),n},u=function(e){var t=p(e.components);return a.a.createElement(l.Provider,{value:t},e.children)},s={inlineCode:"code",wrapper:function(e){var t=e.children;return a.a.createElement(a.a.Fragment,{},t)}},b=a.a.forwardRef((function(e,t){var n=e.components,r=e.mdxType,o=e.originalType,i=e.parentName,l=f(e,["components","mdxType","originalType","parentName"]),u=p(n),b=r,m=u["".concat(i,".").concat(b)]||u[b]||s[b]||o;return n?a.a.createElement(m,c(c({ref:t},l),{},{components:n})):a.a.createElement(m,c({ref:t},l))}));function m(e,t){var n=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var o=n.length,i=new Array(o);i[0]=b;var c={};for(var f in t)hasOwnProperty.call(t,f)&&(c[f]=t[f]);c.originalType=e,c.mdxType="string"==typeof e?e:r,i[1]=c;for(var l=2;l<o;l++)i[l]=n[l];return a.a.createElement.apply(null,i)}return a.a.createElement.apply(null,n)}b.displayName="MDXCreateElement"}}]);