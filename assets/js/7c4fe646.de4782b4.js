"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[2461],{3905:(e,t,n)=>{n.d(t,{Zo:()=>i,kt:()=>d});var a=n(7294);function r(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function l(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);t&&(a=a.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,a)}return n}function o(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?l(Object(n),!0).forEach((function(t){r(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):l(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function s(e,t){if(null==e)return{};var n,a,r=function(e,t){if(null==e)return{};var n,a,r={},l=Object.keys(e);for(a=0;a<l.length;a++)n=l[a],t.indexOf(n)>=0||(r[n]=e[n]);return r}(e,t);if(Object.getOwnPropertySymbols){var l=Object.getOwnPropertySymbols(e);for(a=0;a<l.length;a++)n=l[a],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(r[n]=e[n])}return r}var c=a.createContext({}),u=function(e){var t=a.useContext(c),n=t;return e&&(n="function"==typeof e?e(t):o(o({},t),e)),n},i=function(e){var t=u(e.components);return a.createElement(c.Provider,{value:t},e.children)},f="mdxType",p={inlineCode:"code",wrapper:function(e){var t=e.children;return a.createElement(a.Fragment,{},t)}},m=a.forwardRef((function(e,t){var n=e.components,r=e.mdxType,l=e.originalType,c=e.parentName,i=s(e,["components","mdxType","originalType","parentName"]),f=u(n),m=r,d=f["".concat(c,".").concat(m)]||f[m]||p[m]||l;return n?a.createElement(d,o(o({ref:t},i),{},{components:n})):a.createElement(d,o({ref:t},i))}));function d(e,t){var n=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var l=n.length,o=new Array(l);o[0]=m;var s={};for(var c in t)hasOwnProperty.call(t,c)&&(s[c]=t[c]);s.originalType=e,s[f]="string"==typeof e?e:r,o[1]=s;for(var u=2;u<l;u++)o[u]=n[u];return a.createElement.apply(null,o)}return a.createElement.apply(null,n)}m.displayName="MDXCreateElement"},5162:(e,t,n)=>{n.d(t,{Z:()=>o});var a=n(7294),r=n(6010);const l={tabItem:"tabItem_Ymn6"};function o(e){let{children:t,hidden:n,className:o}=e;return a.createElement("div",{role:"tabpanel",className:(0,r.Z)(l.tabItem,o),hidden:n},t)}},4866:(e,t,n)=>{n.d(t,{Z:()=>v});var a=n(7462),r=n(7294),l=n(6010),o=n(2466),s=n(6550),c=n(1980),u=n(7392),i=n(12);function f(e){return function(e){return r.Children.map(e,(e=>{if((0,r.isValidElement)(e)&&"value"in e.props)return e;throw new Error(`Docusaurus error: Bad <Tabs> child <${"string"==typeof e.type?e.type:e.type.name}>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.`)}))}(e).map((e=>{let{props:{value:t,label:n,attributes:a,default:r}}=e;return{value:t,label:n,attributes:a,default:r}}))}function p(e){const{values:t,children:n}=e;return(0,r.useMemo)((()=>{const e=t??f(n);return function(e){const t=(0,u.l)(e,((e,t)=>e.value===t.value));if(t.length>0)throw new Error(`Docusaurus error: Duplicate values "${t.map((e=>e.value)).join(", ")}" found in <Tabs>. Every value needs to be unique.`)}(e),e}),[t,n])}function m(e){let{value:t,tabValues:n}=e;return n.some((e=>e.value===t))}function d(e){let{queryString:t=!1,groupId:n}=e;const a=(0,s.k6)(),l=function(e){let{queryString:t=!1,groupId:n}=e;if("string"==typeof t)return t;if(!1===t)return null;if(!0===t&&!n)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return n??null}({queryString:t,groupId:n});return[(0,c._X)(l),(0,r.useCallback)((e=>{if(!l)return;const t=new URLSearchParams(a.location.search);t.set(l,e),a.replace({...a.location,search:t.toString()})}),[l,a])]}function x(e){const{defaultValue:t,queryString:n=!1,groupId:a}=e,l=p(e),[o,s]=(0,r.useState)((()=>function(e){let{defaultValue:t,tabValues:n}=e;if(0===n.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(t){if(!m({value:t,tabValues:n}))throw new Error(`Docusaurus error: The <Tabs> has a defaultValue "${t}" but none of its children has the corresponding value. Available values are: ${n.map((e=>e.value)).join(", ")}. If you intend to show no default tab, use defaultValue={null} instead.`);return t}const a=n.find((e=>e.default))??n[0];if(!a)throw new Error("Unexpected error: 0 tabValues");return a.value}({defaultValue:t,tabValues:l}))),[c,u]=d({queryString:n,groupId:a}),[f,x]=function(e){let{groupId:t}=e;const n=function(e){return e?`docusaurus.tab.${e}`:null}(t),[a,l]=(0,i.Nk)(n);return[a,(0,r.useCallback)((e=>{n&&l.set(e)}),[n,l])]}({groupId:a}),y=(()=>{const e=c??f;return m({value:e,tabValues:l})?e:null})();(0,r.useEffect)((()=>{y&&s(y)}),[y]);return{selectedValue:o,selectValue:(0,r.useCallback)((e=>{if(!m({value:e,tabValues:l}))throw new Error(`Can't select invalid tab value=${e}`);s(e),u(e),x(e)}),[u,x,l]),tabValues:l}}var y=n(2389);const g={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};function b(e){let{className:t,block:n,selectedValue:s,selectValue:c,tabValues:u}=e;const i=[],{blockElementScrollPositionUntilNextRender:f}=(0,o.o5)(),p=e=>{const t=e.currentTarget,n=i.indexOf(t),a=u[n].value;a!==s&&(f(t),c(a))},m=e=>{let t=null;switch(e.key){case"Enter":p(e);break;case"ArrowRight":{const n=i.indexOf(e.currentTarget)+1;t=i[n]??i[0];break}case"ArrowLeft":{const n=i.indexOf(e.currentTarget)-1;t=i[n]??i[i.length-1];break}}t?.focus()};return r.createElement("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,l.Z)("tabs",{"tabs--block":n},t)},u.map((e=>{let{value:t,label:n,attributes:o}=e;return r.createElement("li",(0,a.Z)({role:"tab",tabIndex:s===t?0:-1,"aria-selected":s===t,key:t,ref:e=>i.push(e),onKeyDown:m,onClick:p},o,{className:(0,l.Z)("tabs__item",g.tabItem,o?.className,{"tabs__item--active":s===t})}),n??t)})))}function h(e){let{lazy:t,children:n,selectedValue:a}=e;if(t){const e=n.find((e=>e.props.value===a));return e?(0,r.cloneElement)(e,{className:"margin-top--md"}):null}return r.createElement("div",{className:"margin-top--md"},n.map(((e,t)=>(0,r.cloneElement)(e,{key:t,hidden:e.props.value!==a}))))}function k(e){const t=x(e);return r.createElement("div",{className:(0,l.Z)("tabs-container",g.tabList)},r.createElement(b,(0,a.Z)({},e,t)),r.createElement(h,(0,a.Z)({},e,t)))}function v(e){const t=(0,y.Z)();return r.createElement(k,(0,a.Z)({key:String(t)},e))}},274:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>i,contentTitle:()=>c,default:()=>d,frontMatter:()=>s,metadata:()=>u,toc:()=>f});var a=n(7462),r=(n(7294),n(3905)),l=n(4866),o=n(5162);const s={sidebar_position:1,id:"construct",title:"Construct - F[A]"},c="Fx",u={unversionedId:"cats-effect2/fx/construct",id:"cats-effect2/fx/construct",title:"Construct - F[A]",description:"If you use Cats Effect and write tagless final code, and look for a generic way to construct F[A], Fx can help you.",source:"@site/../generated-docs/docs/cats-effect2/fx/construct.md",sourceDirName:"cats-effect2/fx",slug:"/cats-effect2/fx/construct",permalink:"/docs/cats-effect2/fx/construct",draft:!1,tags:[],version:"current",sidebarPosition:1,frontMatter:{sidebar_position:1,id:"construct",title:"Construct - F[A]"},sidebar:"latestSidebar",previous:{title:"Fx",permalink:"/docs/cats-effect2/fx/"},next:{title:"Error Handling",permalink:"/docs/cats-effect2/fx/error-handling/"}},i={},f=[{value:"pureOf",id:"pureof",level:2},{value:"effectOf",id:"effectof",level:2},{value:"pureOrError",id:"pureorerror",level:2},{value:"Example",id:"example",level:2}],p={toc:f},m="wrapper";function d(e){let{components:t,...n}=e;return(0,r.kt)(m,(0,a.Z)({},p,n,{components:t,mdxType:"MDXLayout"}),(0,r.kt)("h1",{id:"fx"},"Fx"),(0,r.kt)("p",null,"If you use Cats Effect and write tagless final code, and look for a generic way to construct ",(0,r.kt)("inlineCode",{parentName:"p"},"F[A]"),", ",(0,r.kt)("inlineCode",{parentName:"p"},"Fx")," can help you."),(0,r.kt)("h2",{id:"pureof"},"pureOf"),(0,r.kt)("p",null,"To construct ",(0,r.kt)("inlineCode",{parentName:"p"},"F[A]")," for a pure value ",(0,r.kt)("inlineCode",{parentName:"p"},"A"),", you can use ",(0,r.kt)("inlineCode",{parentName:"p"},"pureOf"),"."),(0,r.kt)(l.Z,{groupId:"fx",defaultValue:"syntax",values:[{label:"with syntax",value:"syntax"},{label:"without syntax",value:"mo-syntax"}],mdxType:"Tabs"},(0,r.kt)(o.Z,{value:"syntax",mdxType:"TabItem"},(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},"import effectie.core._\nimport effectie.syntax.all._\n\ndef foo[F[_]: Fx]: F[Int] = pureOf(1)\n"))),(0,r.kt)(o.Z,{value:"mo-syntax",mdxType:"TabItem"},(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},"import effectie.core._\n\ndef foo[F[_]: Fx]: F[Int] = Fx[F].pureOf(1)\n")))),(0,r.kt)(l.Z,{groupId:"fx",defaultValue:"syntax",values:[{label:"with syntax",value:"syntax"},{label:"without syntax",value:"mo-syntax"}],mdxType:"Tabs"},(0,r.kt)(o.Z,{value:"syntax",mdxType:"TabItem"},(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},"import cats.effect._\nimport effectie.syntax.all._\n\nimport effectie.instances.ce2.fx._\n\npureOf[IO](1)\n// res3: IO[Int] = Pure(a = 1)\n"))),(0,r.kt)(o.Z,{value:"mo-syntax",mdxType:"TabItem"},(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},"import cats.effect._\nimport effectie.core._\n\nimport effectie.instances.ce2.fx._\n\nFx[IO].pureOf(1)\n// res5: IO[Int] = Pure(a = 1)\n")))),(0,r.kt)("h2",{id:"effectof"},"effectOf"),(0,r.kt)("p",null,"To construct ",(0,r.kt)("inlineCode",{parentName:"p"},"F[A]")," for an operation with referential transparency, you can use ",(0,r.kt)("inlineCode",{parentName:"p"},"effectOf"),"."),(0,r.kt)(l.Z,{groupId:"fx",defaultValue:"syntax",values:[{label:"with syntax",value:"syntax"},{label:"without syntax",value:"mo-syntax"}],mdxType:"Tabs"},(0,r.kt)(o.Z,{value:"syntax",mdxType:"TabItem"},(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'import effectie.core._\nimport effectie.syntax.all._\n\ndef foo[F[_]: Fx](): F[Unit] = effectOf(println("Hello"))\n')),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},"import cats.effect._\n\nimport effectie.instances.ce2.fx._\n\n(for {\n  _ <- foo[IO]()\n  _ <- foo[IO]()\n  _ <- foo[IO]()\n} yield ()).unsafeRunSync()\n// Hello\n// Hello\n// Hello\n"))),(0,r.kt)(o.Z,{value:"mo-syntax",mdxType:"TabItem"},(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'import effectie.core._\n\ndef foo[F[_]: Fx](): F[Unit] = Fx[F].effectOf(println("Hello"))\n')),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},"import cats.effect._\n\nimport effectie.instances.ce2.fx._\n\n(for {\n  _ <- foo[IO]()\n  _ <- foo[IO]()\n  _ <- foo[IO]()\n} yield ()).unsafeRunSync()\n// Hello\n// Hello\n// Hello\n")))),(0,r.kt)(l.Z,{groupId:"fx",defaultValue:"syntax",values:[{label:"with syntax",value:"syntax"},{label:"without syntax",value:"mo-syntax"}],mdxType:"Tabs"},(0,r.kt)(o.Z,{value:"syntax",mdxType:"TabItem"},(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'import cats.effect._\nimport effectie.syntax.all._\n\nimport effectie.instances.ce2.fx._\n\neffectOf[IO](println("Hello"))\n// res11: IO[Unit] = Delay(\n//   thunk = <function0>,\n//   trace = StackTrace(\n// ...\n\n// effectOf can handle exception properly.\neffectOf[IO][Int](throw new RuntimeException("ERROR"))\n// res12: IO[Int] = Delay(\n//   thunk = <function0>,\n//   trace = StackTrace(\n// ...\n'))),(0,r.kt)(o.Z,{value:"mo-syntax",mdxType:"TabItem"},(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'import cats.effect._\nimport effectie.core._\n\nimport effectie.instances.ce2.fx._\n\nFx[IO].effectOf(println("Hello"))\n// res14: IO[Unit] = Delay(\n//   thunk = <function0>,\n//   trace = StackTrace(\n// ...\n\n// effectOf can handle exception properly.\nFx[IO].effectOf[Int](throw new RuntimeException("ERROR"))\n// res15: IO[Int] = Delay(\n//   thunk = <function0>,\n//   trace = StackTrace(\n// ...\n')))),(0,r.kt)("h2",{id:"pureorerror"},"pureOrError"),(0,r.kt)("p",null,"To construct ",(0,r.kt)("inlineCode",{parentName:"p"},"F[A]")," for a pure value, but it can also throw an exception, you can use ",(0,r.kt)("inlineCode",{parentName:"p"},"pureOrError")," instead of ",(0,r.kt)("inlineCode",{parentName:"p"},"effectOf"),"."),(0,r.kt)("p",null,"If an expression returns a pure value, and it's always the same so there's no point in using ",(0,r.kt)("inlineCode",{parentName:"p"},"effectOf")," for referential transparency, you can use ",(0,r.kt)("inlineCode",{parentName:"p"},"pureOf"),". However, if that expression can also throw an exception, ",(0,r.kt)("inlineCode",{parentName:"p"},"pureOf")," can handle it properly. In this case, ",(0,r.kt)("inlineCode",{parentName:"p"},"pureOrError")," is the right one."),(0,r.kt)("p",null,"e.g.)"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'val s: String = "abc"\npureOf[IO](s.substring(5))\n// This immediately throws a StringIndexOutOfBoundsException even though F[_] here is IO.\n')),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'val s: String = "abc"\npureOrError[IO](s.substring(5))\n// StringIndexOutOfBoundsException is now captured by IO.\n')),(0,r.kt)(l.Z,{groupId:"fx",defaultValue:"syntax",values:[{label:"with syntax",value:"syntax"},{label:"without syntax",value:"mo-syntax"}],mdxType:"Tabs"},(0,r.kt)(o.Z,{value:"syntax",mdxType:"TabItem"},(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},"import effectie.core._\nimport effectie.syntax.all._\n\ndef foo[F[_]: Fx]: F[Int] = pureOrError(1)\ndef bar[F[_]: Fx](s: String): F[String] = pureOrError(s.substring(5))\n"))),(0,r.kt)(o.Z,{value:"mo-syntax",mdxType:"TabItem"},(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},"import effectie.core._\n\ndef foo[F[_]: Fx]: F[Int] = Fx[F].pureOrError(1)\ndef bar[F[_]: Fx](s: String): F[String] = Fx[F].pureOrError(s.substring(5))\n")))),(0,r.kt)(l.Z,{groupId:"fx",defaultValue:"syntax",values:[{label:"with syntax",value:"syntax"},{label:"without syntax",value:"mo-syntax"}],mdxType:"Tabs"},(0,r.kt)(o.Z,{value:"syntax",mdxType:"TabItem"},(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'import cats.effect._\nimport effectie.syntax.all._\n\nimport effectie.instances.ce2.fx._\n\npureOrError[IO](1)\n// res19: IO[Int] = Pure(a = 1)\n\npureOrError[IO]("abc".substring(5))\n// res20: IO[String] = RaiseError(\n//   e = java.lang.StringIndexOutOfBoundsException: String index out of range: -2\n// )\n'))),(0,r.kt)(o.Z,{value:"mo-syntax",mdxType:"TabItem"},(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'import cats.effect._\nimport effectie.core._\n\nimport effectie.instances.ce2.fx._\n\nFx[IO].pureOrError(1)\n// res22: IO[Int] = Pure(a = 1)\n\nFx[IO].pureOrError("abc".substring(5))\n// res23: IO[String] = RaiseError(\n//   e = java.lang.StringIndexOutOfBoundsException: String index out of range: -2\n// )\n')))),(0,r.kt)("hr",null),(0,r.kt)("h2",{id:"example"},"Example"),(0,r.kt)(l.Z,{groupId:"fx",defaultValue:"syntax",values:[{label:"with syntax",value:"syntax"},{label:"without syntax",value:"mo-syntax"}],mdxType:"Tabs"},(0,r.kt)(o.Z,{value:"syntax",mdxType:"TabItem"},(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},"import effectie.core._\nimport effectie.syntax.all._\n\ntrait Something[F[_]] {\n  def get[A](a: => A): F[A]\n}\n\nobject Something {\n  def apply[F[_]: Something]: Something[F] =\n    implicitly[Something[F]]\n\n  implicit def something[F[_]: Fx]: Something[F] =\n    new SomethingF[F]\n\n  final class SomethingF[F[_]: Fx]\n    extends Something[F] {\n\n    def get[A](a: => A): F[A] =\n      effectOf(a)\n      // No more Fx[F].effectOf(a)\n  }\n}\n\nimport cats.effect._\nimport effectie.instances.ce2.fx._\n\nval get1 = Something[IO].get(1)\n// get1: IO[Int] = Delay(\n//   thunk = <function0>,\n//   trace = StackTrace(\n// ...\n\nget1.unsafeRunSync()\n// res25: Int = 1\n"))),(0,r.kt)(o.Z,{value:"mo-syntax",mdxType:"TabItem"},(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},"import effectie.core._\n\ntrait Something[F[_]] {\n  def get[A](a: => A): F[A]\n}\n\nobject Something {\n  def apply[F[_]: Something]: Something[F] =\n    implicitly[Something[F]]\n\n  implicit def something[F[_]: Fx]: Something[F] =\n    new SomethingF[F]\n\n  final class SomethingF[F[_]: Fx]\n    extends Something[F] {\n\n    def get[A](a: => A): F[A] =\n      Fx[F].effectOf(a)\n  }\n}\n\nimport cats.effect._\nimport effectie.instances.ce2.fx._\n\nval get1 = Something[IO].get(1)\n// get1: IO[Int] = Delay(\n//   thunk = <function0>,\n//   trace = StackTrace(\n// ...\n\nget1.unsafeRunSync()\n// res27: Int = 1\n")))))}d.isMDXComponent=!0}}]);