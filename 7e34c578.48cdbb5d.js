(window.webpackJsonp=window.webpackJsonp||[]).push([[8],{106:function(e,n,t){"use strict";t.r(n),t.d(n,"frontMatter",(function(){return a})),t.d(n,"metadata",(function(){return i})),t.d(n,"rightToc",(function(){return l})),t.d(n,"default",(function(){return s}));var o=t(2),r=t(6),c=(t(0),t(118)),a={id:"console-effect",title:"ConsoleEffect - Cats"},i={id:"cats-effect/console-effect",isDocsHomePage:!1,title:"ConsoleEffect - Cats",description:"ConsoleEffect",source:"@site/../generated-docs/target/mdoc/cats-effect/console-effect.md",permalink:"/docs/cats-effect/console-effect",sidebar:"someSidebar",previous:{title:"EffectConstructor - Cats",permalink:"/docs/cats-effect/effect-constructor"},next:{title:"OptionTSupport - Cats",permalink:"/docs/cats-effect/optiont-support"}},l=[{value:"ConsoleEffect",id:"consoleeffect",children:[]},{value:"ConsoleEffectful",id:"consoleeffectful",children:[]}],f={rightToc:l};function s(e){var n=e.components,t=Object(r.a)(e,["components"]);return Object(c.b)("wrapper",Object(o.a)({},f,t,{components:n,mdxType:"MDXLayout"}),Object(c.b)("h2",{id:"consoleeffect"},"ConsoleEffect"),Object(c.b)("pre",null,Object(c.b)("code",Object(o.a)({parentName:"pre"},{className:"language-scala"}),'import cats._\nimport cats.implicits._\n\nimport effectie.cats._\nimport effectie.YesNo\n\ntrait Something[F[_]] {\n  def foo[A](): F[Unit]\n}\n\nobject Something {\n\n  def apply[F[_]: Something]: Something[F] =\n    implicitly[Something[F]]\n\n  implicit def something[F[_]: ConsoleEffect: Monad]: Something[F] =\n    new SomethingF[F]\n\n  final class SomethingF[F[_]: ConsoleEffect: Monad]\n    extends Something[F] {\n\n    def foo[A](): F[Unit] = for {\n      _ <- ConsoleEffect[F].putStrLn("Hello")\n      answer <- ConsoleEffect[F].readYesNo("Would you like to proceed?")\n      result = answer match {\n            case YesNo.Yes =>\n              "Done"\n            case YesNo.No =>\n              "Cancelled"\n          }\n      _ <- ConsoleEffect[F].putStrLn(result)\n    } yield ()\n  }\n}\n\nimport cats.effect._\n\nval foo = Something[IO].foo()\nfoo.unsafeRunSync()\n')),Object(c.b)("pre",null,Object(c.b)("code",Object(o.a)({parentName:"pre"},{}),"Hello\nWould you like to proceed?\nn\nCancelled\n")),Object(c.b)("pre",null,Object(c.b)("code",Object(o.a)({parentName:"pre"},{}),"Hello\nWould you like to proceed?\ny\nDone\n")),Object(c.b)("h2",{id:"consoleeffectful"},"ConsoleEffectful"),Object(c.b)("pre",null,Object(c.b)("code",Object(o.a)({parentName:"pre"},{className:"language-scala"}),'import cats._\nimport cats.implicits._\n\nimport effectie.ConsoleEffectful._\nimport effectie.cats._\nimport effectie.YesNo\n\ntrait Something[F[_]] {\n  def foo[A](): F[Unit]\n}\n\nobject Something {\n\n  def apply[F[_]: Something]: Something[F] =\n    implicitly[Something[F]]\n\n  implicit def something[F[_]: ConsoleEffect: Monad]: Something[F] =\n    new SomethingF[F]\n\n  final class SomethingF[F[_]: ConsoleEffect: Monad]\n    extends Something[F] {\n\n    def foo[A](): F[Unit] = for {\n      _ <- putStrLn("Hello")\n      answer <- readYesNo("Would you like to proceed?")\n      result = answer match {\n            case YesNo.Yes =>\n              "Done"\n            case YesNo.No =>\n              "Cancelled"\n          }\n      _ <- putStrLn(result)\n    } yield ()\n  }\n}\n\nimport cats.effect._\n\nval foo = Something[IO].foo()\nfoo.unsafeRunSync()\n')),Object(c.b)("pre",null,Object(c.b)("code",Object(o.a)({parentName:"pre"},{}),"Hello\nWould you like to proceed?\nn\nCancelled\n")),Object(c.b)("pre",null,Object(c.b)("code",Object(o.a)({parentName:"pre"},{}),"Hello\nWould you like to proceed?\ny\nDone\n")))}s.isMDXComponent=!0},118:function(e,n,t){"use strict";t.d(n,"a",(function(){return p})),t.d(n,"b",(function(){return d}));var o=t(0),r=t.n(o);function c(e,n,t){return n in e?Object.defineProperty(e,n,{value:t,enumerable:!0,configurable:!0,writable:!0}):e[n]=t,e}function a(e,n){var t=Object.keys(e);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);n&&(o=o.filter((function(n){return Object.getOwnPropertyDescriptor(e,n).enumerable}))),t.push.apply(t,o)}return t}function i(e){for(var n=1;n<arguments.length;n++){var t=null!=arguments[n]?arguments[n]:{};n%2?a(Object(t),!0).forEach((function(n){c(e,n,t[n])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(t)):a(Object(t)).forEach((function(n){Object.defineProperty(e,n,Object.getOwnPropertyDescriptor(t,n))}))}return e}function l(e,n){if(null==e)return{};var t,o,r=function(e,n){if(null==e)return{};var t,o,r={},c=Object.keys(e);for(o=0;o<c.length;o++)t=c[o],n.indexOf(t)>=0||(r[t]=e[t]);return r}(e,n);if(Object.getOwnPropertySymbols){var c=Object.getOwnPropertySymbols(e);for(o=0;o<c.length;o++)t=c[o],n.indexOf(t)>=0||Object.prototype.propertyIsEnumerable.call(e,t)&&(r[t]=e[t])}return r}var f=r.a.createContext({}),s=function(e){var n=r.a.useContext(f),t=n;return e&&(t="function"==typeof e?e(n):i(i({},n),e)),t},p=function(e){var n=s(e.components);return r.a.createElement(f.Provider,{value:n},e.children)},u={inlineCode:"code",wrapper:function(e){var n=e.children;return r.a.createElement(r.a.Fragment,{},n)}},m=r.a.forwardRef((function(e,n){var t=e.components,o=e.mdxType,c=e.originalType,a=e.parentName,f=l(e,["components","mdxType","originalType","parentName"]),p=s(t),m=o,d=p["".concat(a,".").concat(m)]||p[m]||u[m]||c;return t?r.a.createElement(d,i(i({ref:n},f),{},{components:t})):r.a.createElement(d,i({ref:n},f))}));function d(e,n){var t=arguments,o=n&&n.mdxType;if("string"==typeof e||o){var c=t.length,a=new Array(c);a[0]=m;var i={};for(var l in n)hasOwnProperty.call(n,l)&&(i[l]=n[l]);i.originalType=e,i.mdxType="string"==typeof e?e:o,a[1]=i;for(var f=2;f<c;f++)a[f]=t[f];return r.a.createElement.apply(null,a)}return r.a.createElement.apply(null,t)}m.displayName="MDXCreateElement"}}]);