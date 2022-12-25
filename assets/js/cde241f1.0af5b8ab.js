"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[266],{3905:(e,n,t)=>{t.d(n,{Zo:()=>s,kt:()=>d});var o=t(7294);function r(e,n,t){return n in e?Object.defineProperty(e,n,{value:t,enumerable:!0,configurable:!0,writable:!0}):e[n]=t,e}function l(e,n){var t=Object.keys(e);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);n&&(o=o.filter((function(n){return Object.getOwnPropertyDescriptor(e,n).enumerable}))),t.push.apply(t,o)}return t}function c(e){for(var n=1;n<arguments.length;n++){var t=null!=arguments[n]?arguments[n]:{};n%2?l(Object(t),!0).forEach((function(n){r(e,n,t[n])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(t)):l(Object(t)).forEach((function(n){Object.defineProperty(e,n,Object.getOwnPropertyDescriptor(t,n))}))}return e}function a(e,n){if(null==e)return{};var t,o,r=function(e,n){if(null==e)return{};var t,o,r={},l=Object.keys(e);for(o=0;o<l.length;o++)t=l[o],n.indexOf(t)>=0||(r[t]=e[t]);return r}(e,n);if(Object.getOwnPropertySymbols){var l=Object.getOwnPropertySymbols(e);for(o=0;o<l.length;o++)t=l[o],n.indexOf(t)>=0||Object.prototype.propertyIsEnumerable.call(e,t)&&(r[t]=e[t])}return r}var f=o.createContext({}),i=function(e){var n=o.useContext(f),t=n;return e&&(t="function"==typeof e?e(n):c(c({},n),e)),t},s=function(e){var n=i(e.components);return o.createElement(f.Provider,{value:n},e.children)},p="mdxType",u={inlineCode:"code",wrapper:function(e){var n=e.children;return o.createElement(o.Fragment,{},n)}},m=o.forwardRef((function(e,n){var t=e.components,r=e.mdxType,l=e.originalType,f=e.parentName,s=a(e,["components","mdxType","originalType","parentName"]),p=i(t),m=r,d=p["".concat(f,".").concat(m)]||p[m]||u[m]||l;return t?o.createElement(d,c(c({ref:n},s),{},{components:t})):o.createElement(d,c({ref:n},s))}));function d(e,n){var t=arguments,r=n&&n.mdxType;if("string"==typeof e||r){var l=t.length,c=new Array(l);c[0]=m;var a={};for(var f in n)hasOwnProperty.call(n,f)&&(a[f]=n[f]);a.originalType=e,a[p]="string"==typeof e?e:r,c[1]=a;for(var i=2;i<l;i++)c[i]=t[i];return o.createElement.apply(null,c)}return o.createElement.apply(null,t)}m.displayName="MDXCreateElement"},4199:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>f,contentTitle:()=>c,default:()=>p,frontMatter:()=>l,metadata:()=>a,toc:()=>i});var o=t(7462),r=(t(7294),t(3905));const l={layout:"docs",title:"ConsoleEffect"},c=void 0,a={unversionedId:"scalaz-effect/console-effect",id:"scalaz-effect/console-effect",title:"ConsoleEffect",description:"ConsoleEffect",source:"@site/../generated-docs/target/mdoc/scalaz-effect/console-effect.md",sourceDirName:"scalaz-effect",slug:"/scalaz-effect/console-effect",permalink:"/docs/scalaz-effect/console-effect",draft:!1,tags:[],version:"current",frontMatter:{layout:"docs",title:"ConsoleEffect"},sidebar:"someSidebar",previous:{title:"EitherTSupport",permalink:"/docs/scalaz-effect/eithert-support"}},f={},i=[{value:"ConsoleEffect",id:"consoleeffect",level:2},{value:"ConsoleEffectful",id:"consoleeffectful",level:2}],s={toc:i};function p(e){let{components:n,...t}=e;return(0,r.kt)("wrapper",(0,o.Z)({},s,t,{components:n,mdxType:"MDXLayout"}),(0,r.kt)("h2",{id:"consoleeffect"},"ConsoleEffect"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'import scalaz._\nimport Scalaz._\n\nimport effectie.scalaz._\nimport effectie.YesNo\n\ntrait Something[F[_]] {\n  def foo[A](): F[Unit]\n}\n\nobject Something {\n\n  def apply[F[_]: Something]: Something[F] =\n    implicitly[Something[F]]\n\n  implicit def something[F[_]: ConsoleEffect: Monad]: Something[F] =\n    new SomethingF[F]\n\n  final class SomethingF[F[_]: ConsoleEffect: Monad]\n    extends Something[F] {\n    \n    def foo[A](): F[Unit] = for {\n      _ <- ConsoleEffect[F].putStrLn("Hello")\n      answer <- ConsoleEffect[F].readYesNo("Would you like to proceed?")\n      result = answer match {\n            case YesNo.Yes =>\n              "Done"\n            case YesNo.No =>\n              "Cancelled"\n          }\n      _ <- ConsoleEffect[F].putStrLn(result)\n    } yield ()\n  }\n}\n\nimport scalaz.effect._\n\nval foo = Something[IO].foo()\nfoo.unsafePerformIO()\n')),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},"Hello\nWould you like to proceed?\nn\nCancelled\n")),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},"Hello\nWould you like to proceed?\ny\nDone\n")),(0,r.kt)("h2",{id:"consoleeffectful"},"ConsoleEffectful"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'import scalaz._\nimport Scalaz._\n\nimport effectie.scalaz.ConsoleEffectful._\nimport effectie.scalaz._\nimport effectie.YesNo\n\ntrait Something[F[_]] {\n  def foo[A](): F[Unit]\n}\n\nobject Something {\n\n  def apply[F[_]: Something]: Something[F] =\n    implicitly[Something[F]]\n\n  implicit def something[F[_]: ConsoleEffect: Monad]: Something[F] =\n    new SomethingF[F]\n\n  final class SomethingF[F[_]: ConsoleEffect: Monad]\n    extends Something[F] {\n\n    def foo[A](): F[Unit] = for {\n      _ <- putStrLn("Hello")\n      answer <- readYesNo("Would you like to proceed?")\n      result = answer match {\n            case YesNo.Yes =>\n              "Done"\n            case YesNo.No =>\n              "Cancelled"\n          }\n      _ <- putStrLn(result)\n    } yield ()\n  }\n}\n\nimport scalaz.effect._\n\nval foo = Something[IO].foo()\nfoo.unsafePerformIO()\n')),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},"Hello\nWould you like to proceed?\nn\nCancelled\n")),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},"Hello\nWould you like to proceed?\ny\nDone\n")))}p.isMDXComponent=!0}}]);