"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[897],{3905:(e,n,t)=>{t.d(n,{Zo:()=>f,kt:()=>d});var o=t(7294);function r(e,n,t){return n in e?Object.defineProperty(e,n,{value:t,enumerable:!0,configurable:!0,writable:!0}):e[n]=t,e}function l(e,n){var t=Object.keys(e);if(Object.getOwnPropertySymbols){var o=Object.getOwnPropertySymbols(e);n&&(o=o.filter((function(n){return Object.getOwnPropertyDescriptor(e,n).enumerable}))),t.push.apply(t,o)}return t}function a(e){for(var n=1;n<arguments.length;n++){var t=null!=arguments[n]?arguments[n]:{};n%2?l(Object(t),!0).forEach((function(n){r(e,n,t[n])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(t)):l(Object(t)).forEach((function(n){Object.defineProperty(e,n,Object.getOwnPropertyDescriptor(t,n))}))}return e}function c(e,n){if(null==e)return{};var t,o,r=function(e,n){if(null==e)return{};var t,o,r={},l=Object.keys(e);for(o=0;o<l.length;o++)t=l[o],n.indexOf(t)>=0||(r[t]=e[t]);return r}(e,n);if(Object.getOwnPropertySymbols){var l=Object.getOwnPropertySymbols(e);for(o=0;o<l.length;o++)t=l[o],n.indexOf(t)>=0||Object.prototype.propertyIsEnumerable.call(e,t)&&(r[t]=e[t])}return r}var i=o.createContext({}),s=function(e){var n=o.useContext(i),t=n;return e&&(t="function"==typeof e?e(n):a(a({},n),e)),t},f=function(e){var n=s(e.components);return o.createElement(i.Provider,{value:n},e.children)},p="mdxType",u={inlineCode:"code",wrapper:function(e){var n=e.children;return o.createElement(o.Fragment,{},n)}},m=o.forwardRef((function(e,n){var t=e.components,r=e.mdxType,l=e.originalType,i=e.parentName,f=c(e,["components","mdxType","originalType","parentName"]),p=s(t),m=r,d=p["".concat(i,".").concat(m)]||p[m]||u[m]||l;return t?o.createElement(d,a(a({ref:n},f),{},{components:t})):o.createElement(d,a({ref:n},f))}));function d(e,n){var t=arguments,r=n&&n.mdxType;if("string"==typeof e||r){var l=t.length,a=new Array(l);a[0]=m;var c={};for(var i in n)hasOwnProperty.call(n,i)&&(c[i]=n[i]);c.originalType=e,c[p]="string"==typeof e?e:r,a[1]=c;for(var s=2;s<l;s++)a[s]=t[s];return o.createElement.apply(null,a)}return o.createElement.apply(null,t)}m.displayName="MDXCreateElement"},686:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>i,contentTitle:()=>a,default:()=>u,frontMatter:()=>l,metadata:()=>c,toc:()=>s});var o=t(7462),r=(t(7294),t(3905));const l={sidebar_position:5,id:"console-effect",title:"ConsoleFx"},a=void 0,c={unversionedId:"cats-effect2/console-effect",id:"cats-effect2/console-effect",title:"ConsoleFx",description:"ConsoleFx",source:"@site/../generated-docs/docs/cats-effect2/console-fx.md",sourceDirName:"cats-effect2",slug:"/cats-effect2/console-effect",permalink:"/docs/cats-effect2/console-effect",draft:!1,tags:[],version:"current",sidebarPosition:5,frontMatter:{sidebar_position:5,id:"console-effect",title:"ConsoleFx"},sidebar:"latestSidebar",previous:{title:"FromFuture",permalink:"/docs/cats-effect2/from-future"},next:{title:"For Monix",permalink:"/docs/monix3/"}},i={},s=[{value:"ConsoleFx",id:"consolefx",level:2},{value:"Syntax",id:"syntax",level:2}],f={toc:s},p="wrapper";function u(e){let{components:n,...t}=e;return(0,r.kt)(p,(0,o.Z)({},f,t,{components:n,mdxType:"MDXLayout"}),(0,r.kt)("h2",{id:"consolefx"},"ConsoleFx"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'import cats._\nimport cats.syntax.all._\n\nimport effectie.core._\nimport effectie.core.YesNo\n\ntrait Something[F[_]] {\n  def foo[A](): F[Unit]\n}\n\nobject Something {\n\n  def apply[F[_]: Something]: Something[F] =\n    implicitly[Something[F]]\n\n  implicit def something[F[_]: Fx: Monad]: Something[F] =\n    new SomethingF[F]\n\n  final class SomethingF[F[_]: Fx: Monad]\n    extends Something[F] {\n\n    def foo[A](): F[Unit] = for {\n      _ <- ConsoleFx[F].putStrLn("Hello")\n      answer <- ConsoleFx[F].readYesNo("Would you like to proceed?")\n      result = answer match {\n            case YesNo.Yes =>\n              "Done"\n            case YesNo.No =>\n              "Cancelled"\n          }\n      _ <- ConsoleFx[F].putStrLn(result)\n    } yield ()\n  }\n}\n\nimport cats.effect._\nimport effectie.instances.ce2.fx._\n\nval foo = Something[IO].foo()\nfoo.unsafeRunSync()\n')),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},"Hello\nWould you like to proceed?\nn\nCancelled\n")),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},"Hello\nWould you like to proceed?\ny\nDone\n")),(0,r.kt)("h2",{id:"syntax"},"Syntax"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala",metastring:"modc:compile-only","modc:compile-only":!0},'import cats._\nimport cats.syntax.all._\n\nimport effectie.core._\nimport effectie.syntax.all._\nimport effectie.YesNo\n\ntrait Something[F[_]] {\n  def foo[A](): F[Unit]\n}\n\nobject Something {\n\n  def apply[F[_]: Something]: Something[F] =\n    implicitly[Something[F]]\n\n  implicit def something[F[_]: Fx: Monad]: Something[F] =\n    new SomethingF[F]\n\n  final class SomethingF[F[_]: Fx: Monad]\n    extends Something[F] {\n\n    def foo[A](): F[Unit] = for {\n      _ <- putStrLn("Hello")\n      answer <- readYesNo("Would you like to proceed?")\n      result = answer match {\n            case YesNo.Yes =>\n              "Done"\n            case YesNo.No =>\n              "Cancelled"\n          }\n      _ <- putStrLn(result)\n    } yield ()\n  }\n}\n\nimport cats.effect._\n\nval foo = Something[IO].foo()\nfoo.unsafeRunSync()\n')),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},"Hello\nWould you like to proceed?\nn\nCancelled\n")),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre"},"Hello\nWould you like to proceed?\ny\nDone\n")))}u.isMDXComponent=!0}}]);