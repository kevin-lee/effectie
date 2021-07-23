(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[717],{3905:function(e,t,n){"use strict";n.d(t,{Zo:function(){return u},kt:function(){return m}});var r=n(7294);function o(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function i(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function a(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?i(Object(n),!0).forEach((function(t){o(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):i(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function p(e,t){if(null==e)return{};var n,r,o=function(e,t){if(null==e)return{};var n,r,o={},i=Object.keys(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||(o[n]=e[n]);return o}(e,t);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(r=0;r<i.length;r++)n=i[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(o[n]=e[n])}return o}var c=r.createContext({}),f=function(e){var t=r.useContext(c),n=t;return e&&(n="function"==typeof e?e(t):a(a({},t),e)),n},u=function(e){var t=f(e.components);return r.createElement(c.Provider,{value:t},e.children)},l={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},s=r.forwardRef((function(e,t){var n=e.components,o=e.mdxType,i=e.originalType,c=e.parentName,u=p(e,["components","mdxType","originalType","parentName"]),s=f(n),m=o,O=s["".concat(c,".").concat(m)]||s[m]||l[m]||i;return n?r.createElement(O,a(a({ref:t},u),{},{components:n})):r.createElement(O,a({ref:t},u))}));function m(e,t){var n=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var i=n.length,a=new Array(i);a[0]=s;var p={};for(var c in t)hasOwnProperty.call(t,c)&&(p[c]=t[c]);p.originalType=e,p.mdxType="string"==typeof e?e:o,a[1]=p;for(var f=2;f<i;f++)a[f]=n[f];return r.createElement.apply(null,a)}return r.createElement.apply(null,n)}s.displayName="MDXCreateElement"},6009:function(e,t,n){"use strict";n.r(t),n.d(t,{frontMatter:function(){return p},metadata:function(){return c},toc:function(){return f},default:function(){return l}});var r=n(2122),o=n(9756),i=(n(7294),n(3905)),a=["components"],p={layout:"docs",title:"OptionTSupport"},c={unversionedId:"scalaz-effect/optiont-support",id:"scalaz-effect/optiont-support",isDocsHomePage:!1,title:"OptionTSupport",description:"OptionTSupport",source:"@site/../generated-docs/target/mdoc/scalaz-effect/optiont-support.md",sourceDirName:"scalaz-effect",slug:"/scalaz-effect/optiont-support",permalink:"/docs/scalaz-effect/optiont-support",version:"current",frontMatter:{layout:"docs",title:"OptionTSupport"},sidebar:"someSidebar",previous:{title:"CanCatch",permalink:"/docs/scalaz-effect/can-catch"},next:{title:"EitherTSupport",permalink:"/docs/scalaz-effect/eithert-support"}},f=[{value:"OptionTSupport",id:"optiontsupport",children:[]}],u={toc:f};function l(e){var t=e.components,n=(0,o.Z)(e,a);return(0,i.kt)("wrapper",(0,r.Z)({},u,n,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("h2",{id:"optiontsupport"},"OptionTSupport"),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},"import scalaz._\nimport Scalaz._\n\nimport effectie.scalaz.Effectful._\nimport effectie.scalaz._\nimport effectie.scalaz.OptionTSupport._\n\ntrait Something[F[_]] {\n  def foo(a: Int): F[Option[Int]]\n  def bar(a: Option[Int]): F[Option[Int]]\n}\n\nobject Something {\n  def apply[F[_]: Something]: Something[F] =\n    implicitly[Something[F]]\n\n  implicit def something[F[_]: Fx: Monad]: Something[F] =\n    new SomethingF[F]\n\n  final class SomethingF[F[_]: Fx: Monad]\n    extends Something[F] {\n\n    def foo(a: Int): F[Option[Int]] = (for {\n      x <- a.someTF[F] // == OptionT.liftF(Applicative[F].pure(a))\n      y <- (x + 10).someTF[F] // == OptionT.liftF(Applicative[F].pure(x + 10))\n      z <- effectOf(y + 100).someT // == OptionT.lieftF(effectOf(y + 100))\n    } yield z).run\n\n    def bar(a: Option[Int]): F[Option[Int]] = (for {\n      x <- a.optionT[F] // == OptionT(pureOf(a: Option[Int]))\n      y <- effectOf((x + 999).some).optionT  // == OptionT(effectOf((x + 999).some))\n    } yield y).run\n  }\n\n}\n\nimport scalaz.effect._\n\nSomething[IO].foo(1).unsafePerformIO()\n// res1: Option[Int] = Some(value = 111)\nSomething[IO].foo(10).unsafePerformIO()\n// res2: Option[Int] = Some(value = 120)\n\nSomething[IO].bar(1.some).unsafePerformIO()\n// res3: Option[Int] = Some(value = 1000)\nSomething[IO].bar(none[Int]).unsafePerformIO()\n// res4: Option[Int] = None\n")))}l.isMDXComponent=!0}}]);