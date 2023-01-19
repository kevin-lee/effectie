"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[668],{3905:(e,n,t)=>{t.d(n,{Zo:()=>p,kt:()=>d});var r=t(7294);function o(e,n,t){return n in e?Object.defineProperty(e,n,{value:t,enumerable:!0,configurable:!0,writable:!0}):e[n]=t,e}function i(e,n){var t=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);n&&(r=r.filter((function(n){return Object.getOwnPropertyDescriptor(e,n).enumerable}))),t.push.apply(t,r)}return t}function a(e){for(var n=1;n<arguments.length;n++){var t=null!=arguments[n]?arguments[n]:{};n%2?i(Object(t),!0).forEach((function(n){o(e,n,t[n])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(t)):i(Object(t)).forEach((function(n){Object.defineProperty(e,n,Object.getOwnPropertyDescriptor(t,n))}))}return e}function c(e,n){if(null==e)return{};var t,r,o=function(e,n){if(null==e)return{};var t,r,o={},i=Object.keys(e);for(r=0;r<i.length;r++)t=i[r],n.indexOf(t)>=0||(o[t]=e[t]);return o}(e,n);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(r=0;r<i.length;r++)t=i[r],n.indexOf(t)>=0||Object.prototype.propertyIsEnumerable.call(e,t)&&(o[t]=e[t])}return o}var l=r.createContext({}),f=function(e){var n=r.useContext(l),t=n;return e&&(t="function"==typeof e?e(n):a(a({},n),e)),t},p=function(e){var n=f(e.components);return r.createElement(l.Provider,{value:n},e.children)},m="mdxType",s={inlineCode:"code",wrapper:function(e){var n=e.children;return r.createElement(r.Fragment,{},n)}},u=r.forwardRef((function(e,n){var t=e.components,o=e.mdxType,i=e.originalType,l=e.parentName,p=c(e,["components","mdxType","originalType","parentName"]),m=f(t),u=o,d=m["".concat(l,".").concat(u)]||m[u]||s[u]||i;return t?r.createElement(d,a(a({ref:n},p),{},{components:t})):r.createElement(d,a({ref:n},p))}));function d(e,n){var t=arguments,o=n&&n.mdxType;if("string"==typeof e||o){var i=t.length,a=new Array(i);a[0]=u;var c={};for(var l in n)hasOwnProperty.call(n,l)&&(c[l]=n[l]);c.originalType=e,c[m]="string"==typeof e?e:o,a[1]=c;for(var f=2;f<i;f++)a[f]=t[f];return r.createElement.apply(null,a)}return r.createElement.apply(null,t)}u.displayName="MDXCreateElement"},4965:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>l,contentTitle:()=>a,default:()=>m,frontMatter:()=>i,metadata:()=>c,toc:()=>f});var r=t(7462),o=(t(7294),t(3905));const i={sidebar_position:1,id:"fx",title:"Fx"},a=void 0,c={unversionedId:"monix3/fx",id:"monix3/fx",title:"Fx",description:"Fx",source:"@site/../generated-docs/docs/monix3/fx.md",sourceDirName:"monix3",slug:"/monix3/fx",permalink:"/docs/monix3/fx",draft:!1,tags:[],version:"current",sidebarPosition:1,frontMatter:{sidebar_position:1,id:"fx",title:"Fx"},sidebar:"latestSidebar",previous:{title:"For Monix",permalink:"/docs/monix3/"},next:{title:"FromFuture",permalink:"/docs/monix3/from-future"}},l={},f=[{value:"Fx",id:"fx",level:2},{value:"Effectful",id:"effectful",level:2}],p={toc:f};function m(e){let{components:n,...t}=e;return(0,o.kt)("wrapper",(0,r.Z)({},p,t,{components:n,mdxType:"MDXLayout"}),(0,o.kt)("h2",{id:"fx"},"Fx"),(0,o.kt)("p",null,"If you use Monix and write tagless final code, and look for a generic way to construct ",(0,o.kt)("inlineCode",{parentName:"p"},"F[A]"),", ",(0,o.kt)("inlineCode",{parentName:"p"},"Fx")," can help you."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-scala"},"import effectie.core._\n\ntrait Something[F[_]] {\n  def get[A](a: => A): F[A]\n}\n\nobject Something {\n  def apply[F[_]: Something]: Something[F] =\n    implicitly[Something[F]]\n\n  implicit def something[F[_]: Fx]: Something[F] =\n    new SomethingF[F]\n\n  final class SomethingF[F[_]: Fx]\n    extends Something[F] {\n\n    def get[A](a: => A): F[A] =\n      Fx[F].effectOf(a)\n  }\n}\n\nimport monix.eval._\nimport monix.execution.Scheduler.Implicits.global\n\nimport effectie.instances.monix3.fx._\n\nval get1 = Something[Task].get(1)\n// get1: Task[Int] = Eval(thunk = <function0>)\n\nget1.runSyncUnsafe()\n// res1: Int = 1\n")),(0,o.kt)("p",null,"If you feel it's too cumbersome to repeat ",(0,o.kt)("inlineCode",{parentName:"p"},"Fx[F].effectOf()"),", consider using ",(0,o.kt)("a",{parentName:"p",href:"#effectful"},"Effectful")),(0,o.kt)("h2",{id:"effectful"},"Effectful"),(0,o.kt)("p",null,"If you're sick of repeating ",(0,o.kt)("inlineCode",{parentName:"p"},"Fx[F].effectOf()")," and looking for more convenient ways?, use ",(0,o.kt)("inlineCode",{parentName:"p"},"Effectful")," instead."),(0,o.kt)("pre",null,(0,o.kt)("code",{parentName:"pre",className:"language-scala"},"import effectie.syntax.all._\nimport effectie.core._\n\ntrait Something[F[_]] {\n  def get[A](a: => A): F[A]\n}\n\nobject Something {\n  def apply[F[_]: Something]: Something[F] =\n    implicitly[Something[F]]\n\n  implicit def something[F[_]: Fx]: Something[F] =\n    new SomethingF[F]\n\n  final class SomethingF[F[_]: Fx]\n    extends Something[F] {\n\n    def get[A](a: => A): F[A] =\n      effectOf(a)\n      // No more Fx[F].effectOf(a)\n  }\n}\n\nimport monix.eval._\nimport monix.execution.Scheduler.Implicits.global\n\nimport effectie.instances.monix3.fx._\n\nval get1 = Something[Task].get(1)\n// get1: Task[Int] = Eval(thunk = <function0>)\n\nget1.runSyncUnsafe()\n// res3: Int = 1\n")))}m.isMDXComponent=!0}}]);