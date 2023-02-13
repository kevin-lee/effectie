"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[8273],{3905:(e,t,n)=>{n.d(t,{Zo:()=>s,kt:()=>d});var r=n(7294);function o(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function a(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);t&&(r=r.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,r)}return n}function i(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?a(Object(n),!0).forEach((function(t){o(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):a(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function c(e,t){if(null==e)return{};var n,r,o=function(e,t){if(null==e)return{};var n,r,o={},a=Object.keys(e);for(r=0;r<a.length;r++)n=a[r],t.indexOf(n)>=0||(o[n]=e[n]);return o}(e,t);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);for(r=0;r<a.length;r++)n=a[r],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(o[n]=e[n])}return o}var f=r.createContext({}),l=function(e){var t=r.useContext(f),n=t;return e&&(n="function"==typeof e?e(t):i(i({},t),e)),n},s=function(e){var t=l(e.components);return r.createElement(f.Provider,{value:t},e.children)},p="mdxType",u={inlineCode:"code",wrapper:function(e){var t=e.children;return r.createElement(r.Fragment,{},t)}},m=r.forwardRef((function(e,t){var n=e.components,o=e.mdxType,a=e.originalType,f=e.parentName,s=c(e,["components","mdxType","originalType","parentName"]),p=l(n),m=o,d=p["".concat(f,".").concat(m)]||p[m]||u[m]||a;return n?r.createElement(d,i(i({ref:t},s),{},{components:n})):r.createElement(d,i({ref:t},s))}));function d(e,t){var n=arguments,o=t&&t.mdxType;if("string"==typeof e||o){var a=n.length,i=new Array(a);i[0]=m;var c={};for(var f in t)hasOwnProperty.call(t,f)&&(c[f]=t[f]);c.originalType=e,c[p]="string"==typeof e?e:o,i[1]=c;for(var l=2;l<a;l++)i[l]=n[l];return r.createElement.apply(null,i)}return r.createElement.apply(null,n)}m.displayName="MDXCreateElement"},8716:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>f,contentTitle:()=>i,default:()=>u,frontMatter:()=>a,metadata:()=>c,toc:()=>l});var r=n(7462),o=(n(7294),n(3905));const a={sidebar_position:1,id:"fx",title:"Fx"},i=void 0,c={unversionedId:"docs/cats-effect/fx",id:"version-v1/docs/cats-effect/fx",title:"Fx",description:"Fx",source:"@site/versioned_docs/version-v1/docs/cats-effect/fx.md",sourceDirName:"docs/cats-effect",slug:"/docs/cats-effect/fx",permalink:"/docs/v1/docs/cats-effect/fx",draft:!1,tags:[],version:"v1",sidebarPosition:1,frontMatter:{sidebar_position:1,id:"fx",title:"Fx"},sidebar:"version-v1/docs",previous:{title:"For Cats Effect",permalink:"/docs/v1/docs/cats-effect/"},next:{title:"CanCatch",permalink:"/docs/v1/docs/cats-effect/can-catch"}},f={},l=[{value:"Fx",id:"fx",level:2},{value:"Effectful",id:"effectful",level:2}],s={toc:l},p="wrapper";function u(e){let{components:t,...n}=e;return(0,o.kt)(p,(0,r.Z)({},s,n,{components:t,mdxType:"MDXLayout"}),(0,o.kt)("h2",{id:"fx"},"Fx"),(0,o.kt)("p",null,"If you use Cats Effect and write tagless final code, and look for a generic way to construct ",(0,o.kt)("inlineCode",{parentName:"p"},"F[A]"),", ",(0,o.kt)("inlineCode",{parentName:"p"},"Fx")," can help you."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-scala"},"import effectie.cats._\n\ntrait Something[F[_]] {\n  def get[A](a: => A): F[A]\n}\n\nobject Something {\n  def apply[F[_]: Something]: Something[F] =\n    implicitly[Something[F]]\n\n  implicit def something[F[_]: Fx]: Something[F] =\n    new SomethingF[F]\n\n  final class SomethingF[F[_]: Fx]\n    extends Something[F] {\n\n    def get[A](a: => A): F[A] =\n      Fx[F].effectOf(a)\n  }\n}\n\nimport cats.effect._\n\nval get1 = Something[IO].get(1)\n// get1: IO[Int] = Delay(thunk = <function0>)\n\nget1.unsafeRunSync()\n// res1: Int = 1\n")),(0,o.kt)("p",null,"If you feel it's too cumbersome to repeat ",(0,o.kt)("inlineCode",{parentName:"p"},"Fx[F].effectOf()"),", consider using ",(0,o.kt)("a",{parentName:"p",href:"#effectful"},"Effectful")),(0,o.kt)("h2",{id:"effectful"},"Effectful"),(0,o.kt)("p",null,"If you're sick of repeating ",(0,o.kt)("inlineCode",{parentName:"p"},"Fx[F].effectOf()")," and looking for more convenient ways?, use ",(0,o.kt)("inlineCode",{parentName:"p"},"Effectful")," instead."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-scala"},"import effectie.cats.Effectful._\nimport effectie.cats._\n\ntrait Something[F[_]] {\n  def get[A](a: => A): F[A]\n}\n\nobject Something {\n  def apply[F[_]: Something]: Something[F] =\n    implicitly[Something[F]]\n\n  implicit def something[F[_]: Fx]: Something[F] =\n    new SomethingF[F]\n\n  final class SomethingF[F[_]: Fx]\n    extends Something[F] {\n\n    def get[A](a: => A): F[A] =\n      effectOf(a)\n      // No more Fx[F].effectOf(a)\n  }\n}\n\nimport cats.effect._\n\nval get1 = Something[IO].get(1)\n// get1: IO[Int] = Delay(thunk = <function0>)\n\nget1.unsafeRunSync()\n// res3: Int = 1\n")))}u.isMDXComponent=!0}}]);