"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[289],{3905:(e,t,n)=>{n.d(t,{Zo:()=>p,kt:()=>m});var r=n(7294);function a(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function f(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function i(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?f(Object(n),!0).forEach((function(t){a(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):f(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function o(e,t){if(null==e)return{};var n,r,a=function(e,t){if(null==e)return{};var n,r,a={},f=Object.keys(e);for(r=0;r<f.length;r++)n=f[r],t.indexOf(n)>=0||(a[n]=e[n]);return a}(e,t);if(Object.getOwnPropertySymbols){var f=Object.getOwnPropertySymbols(e);for(r=0;r<f.length;r++)n=f[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(a[n]=e[n])}return a}var c=r.createContext({}),l=function(e){var t=r.useContext(c),n=t;return e&&(n="function"==typeof e?e(t):i(i({},t),e)),n},p=function(e){var t=l(e.components);return r.createElement(c.Provider,{value:t},e.children)},s={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},u=r.forwardRef((function(e,t){var n=e.components,a=e.mdxType,f=e.originalType,c=e.parentName,p=o(e,["components","mdxType","originalType","parentName"]),u=l(n),m=a,h=u["".concat(c,".").concat(m)]||u[m]||s[m]||f;return n?r.createElement(h,i(i({ref:t},p),{},{components:n})):r.createElement(h,i({ref:t},p))}));function m(e,t){var n=arguments,a=t&&t.mdxType;if("string"==typeof e||a){var f=n.length,i=new Array(f);i[0]=u;var o={};for(var c in t)hasOwnProperty.call(t,c)&&(o[c]=t[c]);o.originalType=e,o.mdxType="string"==typeof e?e:a,i[1]=o;for(var l=2;l<f;l++)i[l]=n[l];return r.createElement.apply(null,i)}return r.createElement.apply(null,n)}u.displayName="MDXCreateElement"},836:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>c,contentTitle:()=>i,default:()=>s,frontMatter:()=>f,metadata:()=>o,toc:()=>l});var r=n(7462),a=(n(7294),n(3905));const f={id:"cats-effect",title:"For Cats Effect"},i=void 0,o={unversionedId:"cats-effect/cats-effect",id:"cats-effect/cats-effect",title:"For Cats Effect",description:"Effectie for Cats Effect",source:"@site/../generated-docs/target/mdoc/cats-effect/cats-effect.md",sourceDirName:"cats-effect",slug:"/cats-effect/",permalink:"/docs/cats-effect/",draft:!1,tags:[],version:"current",frontMatter:{id:"cats-effect",title:"For Cats Effect"},sidebar:"someSidebar",previous:{title:"Getting Started",permalink:"/docs/"},next:{title:"Fx",permalink:"/docs/cats-effect/fx"}},c={},l=[{value:"Effectie for Cats Effect",id:"effectie-for-cats-effect",level:2},{value:"All in One Example",id:"all-in-one-example",level:2}],p={toc:l};function s(e){let{components:t,...n}=e;return(0,a.kt)("wrapper",(0,r.Z)({},p,n,{components:t,mdxType:"MDXLayout"}),(0,a.kt)("h2",{id:"effectie-for-cats-effect"},"Effectie for Cats Effect"),(0,a.kt)("ul",null,(0,a.kt)("li",{parentName:"ul"},(0,a.kt)("a",{parentName:"li",href:"/docs/cats-effect/fx"},"Fx")),(0,a.kt)("li",{parentName:"ul"},(0,a.kt)("a",{parentName:"li",href:"/docs/cats-effect/console-effect"},"ConsoleEffect")),(0,a.kt)("li",{parentName:"ul"},(0,a.kt)("a",{parentName:"li",href:"/docs/cats-effect/can-catch"},"CanCatch")),(0,a.kt)("li",{parentName:"ul"},(0,a.kt)("a",{parentName:"li",href:"/docs/cats-effect/can-handle-error"},"CanHandleError")),(0,a.kt)("li",{parentName:"ul"},(0,a.kt)("a",{parentName:"li",href:"/docs/cats-effect/from-future"},"FromFuture")),(0,a.kt)("li",{parentName:"ul"},(0,a.kt)("a",{parentName:"li",href:"/docs/cats-effect/optiont-support"},"OptionTSupport")),(0,a.kt)("li",{parentName:"ul"},(0,a.kt)("a",{parentName:"li",href:"/docs/cats-effect/eithert-support"},"EitherTSupport"))),(0,a.kt)("h2",{id:"all-in-one-example"},"All in One Example"),(0,a.kt)("pre",null,(0,a.kt)("code",{parentName:"pre",className:"language-scala"},'import cats._\nimport cats.syntax.all._\nimport cats.effect._\n\nimport effectie.cats.ConsoleEffectful._\nimport effectie.cats.Effectful._\n\nimport effectie.cats.EitherTSupport._\nimport effectie.cats.OptionTSupport._\nimport effectie.cats._\n\ntrait Something[F[_]] {\n  def foo[A: Semigroup](a: A): F[A]\n  def bar[A: Semigroup](a: Option[A]): F[Option[A]]\n  def baz[A, B: Semigroup](a: Either[A, B]): F[Either[A, B]]\n}\n\nobject Something {\n\n  def apply[F[_]: Something]: Something[F] =\n    implicitly[Something[F]]\n\n  implicit def something[F[_]: Fx: ConsoleEffect: Monad]: Something[F] =\n    new SomethingF[F]\n\n  final class SomethingF[F[_]: Fx: ConsoleEffect: Monad]\n    extends Something[F] {\n\n    override def foo[A: Semigroup](a: A): F[A] =\n      for {\n        n    <- effectOf(a)\n        blah <- pureOf("blah blah")\n        _    <- effectOf(println(s"n: $n / BLAH: $blah"))\n        x    <- effectOf(n |+| n)\n        _    <- putStrLn(s"x: $x")\n      } yield x\n\n    override def bar[A: Semigroup](a: Option[A]): F[Option[A]] =\n      (for {\n        aa   <- a.optionT[F] // OptionT(Applicative[F].pure(a))\n        blah <- "blah blah".someTF[F] // OptionT(Applicative[F].pure(Some("blah blah")))\n        _    <- effectOf(\n                  println(s"a: $a / BLAH: $blah")\n                ).someT // OptionT(effectOf(Some(println(s"a: $a / BLAH: $blah"))))\n        x    <- effectOf(a |+| a).optionT // OptionT(effectOf(a |+| a))\n        _    <- effectOf(putStrLn(s"x: $x")).someT // OptionT(effectOf(Some(putStrLn(s"x: $x"))))\n      } yield x).value\n\n    override def baz[A, B: Semigroup](ab: Either[A, B]): F[Either[A, B]] =\n      (for {\n        b    <- ab.eitherT[F] // EitherT(Applicative[F].pure(ab))\n        blah <- "blah blah"\n                  .asRight[A]\n                  .eitherT[F] // EitherT(Applicative[F].pure("blah blah".asRight[A]))\n        _    <- effectOf(\n                  println(s"b: $b / BLAH: $blah")\n                ).rightT[A] // EitherT(effectOf(Right(println(s"b: $b / BLAH: $blah"))))\n        x    <- effectOf(ab |+| ab).eitherT // EitherT(effectOf(ab |+| ab))\n        _    <- effectOf(\n                  putStrLn(s"x: $x")\n                ).rightT[A] // EitherT(effectOf(putStrLn(s"x: $x").asRight[A]))\n      } yield x).value\n  }\n}\n\nprintln(Something[IO].foo(1).unsafeRunSync())\n// n: 1 / BLAH: blah blah\n// x: 2\n// 2\n\nprintln(Something[IO].bar(2.some).unsafeRunSync())\n// a: Some(2) / BLAH: blah blah\n// Some(4)\nprintln(Something[IO].bar(none[String]).unsafeRunSync())\n// None\n\nprintln(Something[IO].baz(2.asRight[String]).unsafeRunSync())\n// b: 2 / BLAH: blah blah\n// Right(4)\nprintln(Something[IO].baz("ERROR!!!".asLeft[Int]).unsafeRunSync())\n// Left(ERROR!!!)\n')))}s.isMDXComponent=!0}}]);