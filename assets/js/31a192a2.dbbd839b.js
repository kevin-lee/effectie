(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[945],{3905:function(e,n,t){"use strict";t.d(n,{Zo:function(){return u},kt:function(){return s}});var r=t(7294);function o(e,n,t){return n in e?Object.defineProperty(e,n,{value:t,enumerable:!0,configurable:!0,writable:!0}):e[n]=t,e}function i(e,n){var t=Object.keys(e);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);n&&(r=r.filter((function(n){return Object.getOwnPropertyDescriptor(e,n).enumerable}))),t.push.apply(t,r)}return t}function a(e){for(var n=1;n<arguments.length;n++){var t=null!=arguments[n]?arguments[n]:{};n%2?i(Object(t),!0).forEach((function(n){o(e,n,t[n])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(t)):i(Object(t)).forEach((function(n){Object.defineProperty(e,n,Object.getOwnPropertyDescriptor(t,n))}))}return e}function c(e,n){if(null==e)return{};var t,r,o=function(e,n){if(null==e)return{};var t,r,o={},i=Object.keys(e);for(r=0;r<i.length;r++)t=i[r],n.indexOf(t)>=0||(o[t]=e[t]);return o}(e,n);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(r=0;r<i.length;r++)t=i[r],n.indexOf(t)>=0||Object.prototype.propertyIsEnumerable.call(e,t)&&(o[t]=e[t])}return o}var f=r.createContext({}),l=function(e){var n=r.useContext(f),t=n;return e&&(t="function"==typeof e?e(n):a(a({},n),e)),t},u=function(e){var n=l(e.components);return r.createElement(f.Provider,{value:n},e.children)},m={inlineCode:"code",wrapper:function(e){var n=e.children;return r.createElement(r.Fragment,{},n)}},p=r.forwardRef((function(e,n){var t=e.components,o=e.mdxType,i=e.originalType,f=e.parentName,u=c(e,["components","mdxType","originalType","parentName"]),p=l(t),s=o,d=p["".concat(f,".").concat(s)]||p[s]||m[s]||i;return t?r.createElement(d,a(a({ref:n},u),{},{components:t})):r.createElement(d,a({ref:n},u))}));function s(e,n){var t=arguments,o=n&&n.mdxType;if("string"==typeof e||o){var i=t.length,a=new Array(i);a[0]=p;var c={};for(var f in n)hasOwnProperty.call(n,f)&&(c[f]=n[f]);c.originalType=e,c.mdxType="string"==typeof e?e:o,a[1]=c;for(var l=2;l<i;l++)a[l]=t[l];return r.createElement.apply(null,a)}return r.createElement.apply(null,t)}p.displayName="MDXCreateElement"},1871:function(e,n,t){"use strict";t.r(n),t.d(n,{frontMatter:function(){return c},contentTitle:function(){return f},metadata:function(){return l},toc:function(){return u},default:function(){return p}});var r=t(2122),o=t(9756),i=(t(7294),t(3905)),a=["components"],c={id:"fx",title:"Fx"},f=void 0,l={unversionedId:"monix/fx",id:"monix/fx",isDocsHomePage:!1,title:"Fx",description:"Fx",source:"@site/../generated-docs/target/mdoc/monix/fx.md",sourceDirName:"monix",slug:"/monix/fx",permalink:"/docs/monix/fx",version:"current",frontMatter:{id:"fx",title:"Fx"},sidebar:"someSidebar",previous:{title:"For Monix",permalink:"/docs/monix/monix"},next:{title:"CanCatch",permalink:"/docs/monix/can-catch"}},u=[{value:"Fx",id:"fx",children:[]},{value:"Effectful",id:"effectful",children:[]}],m={toc:u};function p(e){var n=e.components,t=(0,o.Z)(e,a);return(0,i.kt)("wrapper",(0,r.Z)({},m,t,{components:n,mdxType:"MDXLayout"}),(0,i.kt)("h2",{id:"fx"},"Fx"),(0,i.kt)("p",null,"If you use Monix and write tagless final code, and look for a generic way to construct ",(0,i.kt)("inlineCode",{parentName:"p"},"F[A]"),", ",(0,i.kt)("inlineCode",{parentName:"p"},"Fx")," can help you."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},"import effectie.monix._\n\ntrait Something[F[_]] {\n  def get[A](a: => A): F[A]\n}\n\nobject Something {\n  def apply[F[_]: Something]: Something[F] =\n    implicitly[Something[F]]\n\n  implicit def something[F[_]: Fx]: Something[F] =\n    new SomethingF[F]\n\n  final class SomethingF[F[_]: Fx]\n    extends Something[F] {\n\n    def get[A](a: => A): F[A] =\n      Fx[F].effectOf(a)\n  }\n}\n\nimport monix.eval._\nimport monix.execution.Scheduler.Implicits.global\n\nval get1 = Something[Task].get(1)\n// get1: Task[Int] = Eval(thunk = <function0>)\n\nget1.runSyncUnsafe()\n// res1: Int = 1\n")),(0,i.kt)("p",null,"If you feel it's too cumbersome to repeat ",(0,i.kt)("inlineCode",{parentName:"p"},"Fx[F].effectOf()"),", consider using ",(0,i.kt)("a",{parentName:"p",href:"#effectful"},"Effectful")),(0,i.kt)("h2",{id:"effectful"},"Effectful"),(0,i.kt)("p",null,"If you're sick of repeating ",(0,i.kt)("inlineCode",{parentName:"p"},"Fx[F].effectOf()")," and looking for more convenient ways?, use ",(0,i.kt)("inlineCode",{parentName:"p"},"Effectful")," instead."),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-scala"},"import effectie.monix.Effectful._\nimport effectie.monix._\n\ntrait Something[F[_]] {\n  def get[A](a: => A): F[A]\n}\n\nobject Something {\n  def apply[F[_]: Something]: Something[F] =\n    implicitly[Something[F]]\n\n  implicit def something[F[_]: Fx]: Something[F] =\n    new SomethingF[F]\n\n  final class SomethingF[F[_]: Fx]\n    extends Something[F] {\n\n    def get[A](a: => A): F[A] =\n      effectOf(a)\n      // No more Fx[F].effectOf(a)\n  }\n}\n\nimport monix.eval._\nimport monix.execution.Scheduler.Implicits.global\n\nval get1 = Something[Task].get(1)\n// get1: Task[Int] = Eval(thunk = <function0>)\n\nget1.runSyncUnsafe()\n// res3: Int = 1\n")))}p.isMDXComponent=!0}}]);