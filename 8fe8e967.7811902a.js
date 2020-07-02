(window.webpackJsonp=window.webpackJsonp||[]).push([[10],{108:function(e,t,n){"use strict";n.r(t),n.d(t,"frontMatter",(function(){return a})),n.d(t,"metadata",(function(){return c})),n.d(t,"rightToc",(function(){return p})),n.d(t,"default",(function(){return u}));var o=n(2),r=n(6),i=(n(0),n(118)),a={layout:"docs",title:"OptionTSupport - Scalaz"},c={id:"scalaz-effect/optiont-support",isDocsHomePage:!1,title:"OptionTSupport - Scalaz",description:"OptionTSupport",source:"@site/../generated-docs/target/mdoc/scalaz-effect/optiont-support.md",permalink:"/docs/scalaz-effect/optiont-support",sidebar:"someSidebar",previous:{title:"ConsoleEffect - Scalaz",permalink:"/docs/scalaz-effect/console-effect"},next:{title:"EitherTSupport - Scalaz",permalink:"/docs/scalaz-effect/eithert-support"}},p=[{value:"OptionTSupport",id:"optiontsupport",children:[]}],f={rightToc:p};function u(e){var t=e.components,n=Object(r.a)(e,["components"]);return Object(i.b)("wrapper",Object(o.a)({},f,n,{components:t,mdxType:"MDXLayout"}),Object(i.b)("h2",{id:"optiontsupport"},"OptionTSupport"),Object(i.b)("pre",null,Object(i.b)("code",Object(o.a)({parentName:"pre"},{className:"language-scala"}),"import scalaz._\nimport Scalaz._\n\nimport effectie.Effectful._\nimport effectie.scalaz._\nimport effectie.scalaz.OptionTSupport._\n\ntrait Something[F[_]] {\n  def foo(a: Int): F[Option[Int]]\n  def bar(a: Option[Int]): F[Option[Int]]\n}\n\nobject Something {\n  def apply[F[_]: Something]: Something[F] =\n    implicitly[Something[F]]\n\n  implicit def something[F[_]: EffectConstructor: Monad]: Something[F] =\n    new SomethingF[F]\n\n  final class SomethingF[F[_]: EffectConstructor: Monad]\n    extends Something[F] {\n\n    def foo(a: Int): F[Option[Int]] = (for {\n      x <- optionTSomePure(a) // == OptionT.liftF(effectOfPure(a))\n      y <- optionTSome(x + 10) // == OptionT.liftF(effectOf(x + 10))\n      z <- optionTSomeF(effectOf(y + 100)) // == OptionT.lieftF(effectOf(y + 100))\n    } yield z).run\n\n    def bar(a: Option[Int]): F[Option[Int]] = (for {\n      x <- optionTOfPure(a) // == OptionT(effectOfPure(a: Option[Int]))\n      y <- optionTOf((x + 999).some)  // == OptionT(effectOf((x + 999).some))\n    } yield y).run\n  }\n\n}\n\nimport scalaz.effect._\n\nSomething[IO].foo(1).unsafePerformIO()\n// res1: Option[Int] = Some(111)\nSomething[IO].foo(10).unsafePerformIO()\n// res2: Option[Int] = Some(120)\n\nSomething[IO].bar(1.some).unsafePerformIO()\n// res3: Option[Int] = Some(1000)\nSomething[IO].bar(none[Int]).unsafePerformIO()\n// res4: Option[Int] = None\n")))}u.isMDXComponent=!0},118:function(e,t,n){"use strict";n.d(t,"a",(function(){return l})),n.d(t,"b",(function(){return O}));var o=n(0),r=n.n(o);function i(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function a(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);t&&(o=o.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,o)}return n}function c(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?a(Object(n),!0).forEach((function(t){i(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):a(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function p(e,t){if(null==e)return{};var n,o,r=function(e,t){if(null==e)return{};var n,o,r={},i=Object.keys(e);for(o=0;o<i.length;o++)n=i[o],t.indexOf(n)>=0||(r[n]=e[n]);return r}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(o=0;o<i.length;o++)n=i[o],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(r[n]=e[n])}return r}var f=r.a.createContext({}),u=function(e){var t=r.a.useContext(f),n=t;return e&&(n="function"==typeof e?e(t):c(c({},t),e)),n},l=function(e){var t=u(e.components);return r.a.createElement(f.Provider,{value:t},e.children)},s={inlineCode:"code",wrapper:function(e){var t=e.children;return r.a.createElement(r.a.Fragment,{},t)}},m=r.a.forwardRef((function(e,t){var n=e.components,o=e.mdxType,i=e.originalType,a=e.parentName,f=p(e,["components","mdxType","originalType","parentName"]),l=u(n),m=o,O=l["".concat(a,".").concat(m)]||l[m]||s[m]||i;return n?r.a.createElement(O,c(c({ref:t},f),{},{components:n})):r.a.createElement(O,c({ref:t},f))}));function O(e,t){var n=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var i=n.length,a=new Array(i);a[0]=m;var c={};for(var p in t)hasOwnProperty.call(t,p)&&(c[p]=t[p]);c.originalType=e,c.mdxType="string"==typeof e?e:o,a[1]=c;for(var f=2;f<i;f++)a[f]=n[f];return r.a.createElement.apply(null,a)}return r.a.createElement.apply(null,n)}m.displayName="MDXCreateElement"}}]);