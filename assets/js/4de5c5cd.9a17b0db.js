"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[3478],{3905:(e,t,a)=>{a.d(t,{Zo:()=>u,kt:()=>m});var n=a(7294);function r(e,t,a){return t in e?Object.defineProperty(e,t,{value:a,enumerable:!0,configurable:!0,writable:!0}):e[t]=a,e}function l(e,t){var a=Object.keys(e);if(Object.getOwnPropertySymbols){var n=Object.getOwnPropertySymbols(e);t&&(n=n.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),a.push.apply(a,n)}return a}function o(e){for(var t=1;t<arguments.length;t++){var a=null!=arguments[t]?arguments[t]:{};t%2?l(Object(a),!0).forEach((function(t){r(e,t,a[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(a)):l(Object(a)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(a,t))}))}return e}function i(e,t){if(null==e)return{};var a,n,r=function(e,t){if(null==e)return{};var a,n,r={},l=Object.keys(e);for(n=0;n<l.length;n++)a=l[n],t.indexOf(a)>=0||(r[a]=e[a]);return r}(e,t);if(Object.getOwnPropertySymbols){var l=Object.getOwnPropertySymbols(e);for(n=0;n<l.length;n++)a=l[n],t.indexOf(a)>=0||Object.prototype.propertyIsEnumerable.call(e,a)&&(r[a]=e[a])}return r}var s=n.createContext({}),c=function(e){var t=n.useContext(s),a=t;return e&&(a="function"==typeof e?e(t):o(o({},t),e)),a},u=function(e){var t=c(e.components);return n.createElement(s.Provider,{value:t},e.children)},f="mdxType",p={inlineCode:"code",wrapper:function(e){var t=e.children;return n.createElement(n.Fragment,{},t)}},d=n.forwardRef((function(e,t){var a=e.components,r=e.mdxType,l=e.originalType,s=e.parentName,u=i(e,["components","mdxType","originalType","parentName"]),f=c(a),d=r,m=f["".concat(s,".").concat(d)]||f[d]||p[d]||l;return a?n.createElement(m,o(o({ref:t},u),{},{components:a})):n.createElement(m,o({ref:t},u))}));function m(e,t){var a=arguments,r=t&&t.mdxType;if("string"==typeof e||r){var l=a.length,o=new Array(l);o[0]=d;var i={};for(var s in t)hasOwnProperty.call(t,s)&&(i[s]=t[s]);i.originalType=e,i[f]="string"==typeof e?e:r,o[1]=i;for(var c=2;c<l;c++)o[c]=a[c];return n.createElement.apply(null,o)}return n.createElement.apply(null,a)}d.displayName="MDXCreateElement"},5162:(e,t,a)=>{a.d(t,{Z:()=>o});var n=a(7294),r=a(6010);const l={tabItem:"tabItem_Ymn6"};function o(e){let{children:t,hidden:a,className:o}=e;return n.createElement("div",{role:"tabpanel",className:(0,r.Z)(l.tabItem,o),hidden:a},t)}},4866:(e,t,a)=>{a.d(t,{Z:()=>N});var n=a(7462),r=a(7294),l=a(6010),o=a(2466),i=a(6550),s=a(1980),c=a(7392),u=a(12);function f(e){return function(e){return r.Children.map(e,(e=>{if(!e||(0,r.isValidElement)(e)&&function(e){const{props:t}=e;return!!t&&"object"==typeof t&&"value"in t}(e))return e;throw new Error(`Docusaurus error: Bad <Tabs> child <${"string"==typeof e.type?e.type:e.type.name}>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.`)}))?.filter(Boolean)??[]}(e).map((e=>{let{props:{value:t,label:a,attributes:n,default:r}}=e;return{value:t,label:a,attributes:n,default:r}}))}function p(e){const{values:t,children:a}=e;return(0,r.useMemo)((()=>{const e=t??f(a);return function(e){const t=(0,c.l)(e,((e,t)=>e.value===t.value));if(t.length>0)throw new Error(`Docusaurus error: Duplicate values "${t.map((e=>e.value)).join(", ")}" found in <Tabs>. Every value needs to be unique.`)}(e),e}),[t,a])}function d(e){let{value:t,tabValues:a}=e;return a.some((e=>e.value===t))}function m(e){let{queryString:t=!1,groupId:a}=e;const n=(0,i.k6)(),l=function(e){let{queryString:t=!1,groupId:a}=e;if("string"==typeof t)return t;if(!1===t)return null;if(!0===t&&!a)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return a??null}({queryString:t,groupId:a});return[(0,s._X)(l),(0,r.useCallback)((e=>{if(!l)return;const t=new URLSearchParams(n.location.search);t.set(l,e),n.replace({...n.location,search:t.toString()})}),[l,n])]}function b(e){const{defaultValue:t,queryString:a=!1,groupId:n}=e,l=p(e),[o,i]=(0,r.useState)((()=>function(e){let{defaultValue:t,tabValues:a}=e;if(0===a.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(t){if(!d({value:t,tabValues:a}))throw new Error(`Docusaurus error: The <Tabs> has a defaultValue "${t}" but none of its children has the corresponding value. Available values are: ${a.map((e=>e.value)).join(", ")}. If you intend to show no default tab, use defaultValue={null} instead.`);return t}const n=a.find((e=>e.default))??a[0];if(!n)throw new Error("Unexpected error: 0 tabValues");return n.value}({defaultValue:t,tabValues:l}))),[s,c]=m({queryString:a,groupId:n}),[f,b]=function(e){let{groupId:t}=e;const a=function(e){return e?`docusaurus.tab.${e}`:null}(t),[n,l]=(0,u.Nk)(a);return[n,(0,r.useCallback)((e=>{a&&l.set(e)}),[a,l])]}({groupId:n}),k=(()=>{const e=s??f;return d({value:e,tabValues:l})?e:null})();(0,r.useLayoutEffect)((()=>{k&&i(k)}),[k]);return{selectedValue:o,selectValue:(0,r.useCallback)((e=>{if(!d({value:e,tabValues:l}))throw new Error(`Can't select invalid tab value=${e}`);i(e),c(e),b(e)}),[c,b,l]),tabValues:l}}var k=a(2389);const g={tabList:"tabList__CuJ",tabItem:"tabItem_LNqP"};function h(e){let{className:t,block:a,selectedValue:i,selectValue:s,tabValues:c}=e;const u=[],{blockElementScrollPositionUntilNextRender:f}=(0,o.o5)(),p=e=>{const t=e.currentTarget,a=u.indexOf(t),n=c[a].value;n!==i&&(f(t),s(n))},d=e=>{let t=null;switch(e.key){case"Enter":p(e);break;case"ArrowRight":{const a=u.indexOf(e.currentTarget)+1;t=u[a]??u[0];break}case"ArrowLeft":{const a=u.indexOf(e.currentTarget)-1;t=u[a]??u[u.length-1];break}}t?.focus()};return r.createElement("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,l.Z)("tabs",{"tabs--block":a},t)},c.map((e=>{let{value:t,label:a,attributes:o}=e;return r.createElement("li",(0,n.Z)({role:"tab",tabIndex:i===t?0:-1,"aria-selected":i===t,key:t,ref:e=>u.push(e),onKeyDown:d,onClick:p},o,{className:(0,l.Z)("tabs__item",g.tabItem,o?.className,{"tabs__item--active":i===t})}),a??t)})))}function v(e){let{lazy:t,children:a,selectedValue:n}=e;const l=(Array.isArray(a)?a:[a]).filter(Boolean);if(t){const e=l.find((e=>e.props.value===n));return e?(0,r.cloneElement)(e,{className:"margin-top--md"}):null}return r.createElement("div",{className:"margin-top--md"},l.map(((e,t)=>(0,r.cloneElement)(e,{key:t,hidden:e.props.value!==n}))))}function y(e){const t=b(e);return r.createElement("div",{className:(0,l.Z)("tabs-container",g.tabList)},r.createElement(h,(0,n.Z)({},e,t)),r.createElement(v,(0,n.Z)({},e,t)))}function N(e){const t=(0,k.Z)();return r.createElement(y,(0,n.Z)({key:String(t)},e))}},5317:(e,t,a)=>{a.r(t),a.d(t,{assets:()=>u,contentTitle:()=>s,default:()=>m,frontMatter:()=>i,metadata:()=>c,toc:()=>f});var n=a(7462),r=(a(7294),a(3905)),l=a(4866),o=a(5162);const i={sidebar_position:1,id:"getting-started",title:"Getting Started",slug:"/"},s=void 0,c={unversionedId:"getting-started",id:"getting-started",title:"Getting Started",description:"Effectie Logo Effectie",source:"@site/../generated-docs/docs/getting-started.md",sourceDirName:".",slug:"/",permalink:"/docs/",draft:!1,tags:[],version:"current",sidebarPosition:1,frontMatter:{sidebar_position:1,id:"getting-started",title:"Getting Started",slug:"/"},sidebar:"latestSidebar",next:{title:"For Cats Effect",permalink:"/docs/cats-effect2/"}},u={},f=[{value:"Effectie Logo Effectie",id:"effectie-logo-effectie",level:2},{value:"Getting Started",id:"getting-started",level:2},{value:"For Cats Effect 3",id:"for-cats-effect-3",level:3},{value:"For Cats Effect 2",id:"for-cats-effect-2",level:3},{value:"For Monix",id:"for-monix",level:3},{value:"Why?",id:"why",level:2},{value:"Problem: Duplicate Implementations",id:"problem-duplicate-implementations",level:3},{value:"No More Duplicates with Effectie",id:"no-more-duplicates-with-effectie",level:3}],p={toc:f},d="wrapper";function m(e){let{components:t,...i}=e;return(0,r.kt)(d,(0,n.Z)({},p,i,{components:t,mdxType:"MDXLayout"}),(0,r.kt)("h2",{id:"effectie-logo-effectie"},(0,r.kt)("img",{alt:"Effectie Logo",src:a(6907).Z,width:"96",height:"96"})," Effectie"),(0,r.kt)("p",null,(0,r.kt)("a",{parentName:"p",href:"https://github.com/Kevin-Lee/effectie/actions?workflow=Build-All"},(0,r.kt)("img",{parentName:"a",src:"https://github.com/Kevin-Lee/effectie/workflows/Build-All/badge.svg",alt:"Build Status"})),"\n",(0,r.kt)("a",{parentName:"p",href:"https://github.com/Kevin-Lee/effectie/actions?workflow=Release"},(0,r.kt)("img",{parentName:"a",src:"https://github.com/Kevin-Lee/effectie/workflows/Release/badge.svg",alt:"Release Status"})),"\n",(0,r.kt)("a",{parentName:"p",href:"https://index.scala-lang.org/kevin-lee/effectie"},(0,r.kt)("img",{parentName:"a",src:"https://index.scala-lang.org/kevin-lee/effectie/latest.svg",alt:"Latest version"}))),(0,r.kt)("table",null,(0,r.kt)("thead",{parentName:"table"},(0,r.kt)("tr",{parentName:"thead"},(0,r.kt)("th",{parentName:"tr",align:"right"},"Project"),(0,r.kt)("th",{parentName:"tr",align:null},"Maven Central"))),(0,r.kt)("tbody",{parentName:"table"},(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:"right"},"effectie-cats-effect3"),(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("a",{parentName:"td",href:"https://search.maven.org/artifact/io.kevinlee/effectie-cats-effect3_2.13"},(0,r.kt)("img",{parentName:"a",src:"https://maven-badges.herokuapp.com/maven-central/io.kevinlee/effectie-cats-effect3_2.13/badge.svg",alt:"Maven Central"})))),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:"right"},"effectie-cats-effect2"),(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("a",{parentName:"td",href:"https://search.maven.org/artifact/io.kevinlee/effectie-cats-effect2_2.13"},(0,r.kt)("img",{parentName:"a",src:"https://maven-badges.herokuapp.com/maven-central/io.kevinlee/effectie-cats-effect2_2.13/badge.svg",alt:"Maven Central"})))),(0,r.kt)("tr",{parentName:"tbody"},(0,r.kt)("td",{parentName:"tr",align:"right"},"effectie-monix3"),(0,r.kt)("td",{parentName:"tr",align:null},(0,r.kt)("a",{parentName:"td",href:"https://search.maven.org/artifact/io.kevinlee/effectie-monix3_2.13"},(0,r.kt)("img",{parentName:"a",src:"https://maven-badges.herokuapp.com/maven-central/io.kevinlee/effectie-monix3_2.13/badge.svg",alt:"Maven Central"})))))),(0,r.kt)("ul",null,(0,r.kt)("li",{parentName:"ul"},"Supported Scala Versions: ",(0,r.kt)("inlineCode",{parentName:"li"},"3"),", ",(0,r.kt)("inlineCode",{parentName:"li"},"2.13")," and ",(0,r.kt)("inlineCode",{parentName:"li"},"2.12"))),(0,r.kt)("p",null,"A set of type-classes and utils for functional effect libraries (i.e.  Cats Effect, Monix and Scalaz's Effect)."),(0,r.kt)("p",null,"Why Effectie? Please read ",(0,r.kt)("a",{parentName:"p",href:"#why"},'"Why?"')," section."),(0,r.kt)("h2",{id:"getting-started"},"Getting Started"),(0,r.kt)("h3",{id:"for-cats-effect-3"},"For Cats Effect 3"),(0,r.kt)(l.Z,{groupId:"cats-effect3",defaultValue:"cats-effect3-sbt",values:[{label:"sbt",value:"cats-effect3-sbt"},{label:"sbt (with libraryDependencies)",value:"cats-effect3-sbt-lib"},{label:"scala-cli",value:"cats-effect3-scala-cli"}],mdxType:"Tabs"},(0,r.kt)(o.Z,{value:"cats-effect3-sbt",mdxType:"TabItem"},(0,r.kt)("p",null,"In ",(0,r.kt)("inlineCode",{parentName:"p"},"build.sbt"),","),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'"io.kevinlee" %% "effectie-cats-effect3" % "2.0.0-beta13"\n'))),(0,r.kt)(o.Z,{value:"cats-effect3-sbt-lib",mdxType:"TabItem"},(0,r.kt)("p",null,"In ",(0,r.kt)("inlineCode",{parentName:"p"},"build.sbt"),","),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'libraryDependencies += "io.kevinlee" %% "effectie-cats-effect3" % "2.0.0-beta13"\n'))),(0,r.kt)(o.Z,{value:"cats-effect3-scala-cli",mdxType:"TabItem"},(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'//> using dep "io.kevinlee::effectie-cats-effect3:2.0.0-beta13"\n')))),(0,r.kt)("h3",{id:"for-cats-effect-2"},"For Cats Effect 2"),(0,r.kt)(l.Z,{groupId:"cats-effect2",defaultValue:"cats-effect2-sbt",values:[{label:"sbt",value:"cats-effect2-sbt"},{label:"sbt (with libraryDependencies)",value:"cats-effect2-sbt-lib"},{label:"scala-cli",value:"cats-effect2-scala-cli"}],mdxType:"Tabs"},(0,r.kt)(o.Z,{value:"cats-effect2-sbt",mdxType:"TabItem"},(0,r.kt)("p",null,"In ",(0,r.kt)("inlineCode",{parentName:"p"},"build.sbt"),","),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'"io.kevinlee" %% "effectie-cats-effect2" % "2.0.0-beta13"\n'))),(0,r.kt)(o.Z,{value:"cats-effect2-sbt-lib",mdxType:"TabItem"},(0,r.kt)("p",null,"In ",(0,r.kt)("inlineCode",{parentName:"p"},"build.sbt"),","),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'libraryDependencies += "io.kevinlee" %% "effectie-cats-effect2" % "2.0.0-beta13"\n'))),(0,r.kt)(o.Z,{value:"cats-effect2-scala-cli",mdxType:"TabItem"},(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'//> using dep "io.kevinlee::effectie-cats-effect2:2.0.0-beta13"\n')))),(0,r.kt)("p",null,"For more details, check out ",(0,r.kt)("a",{parentName:"p",href:"/docs/cats-effect2/"},"Effectie for Cats Effect"),"."),(0,r.kt)("h3",{id:"for-monix"},"For Monix"),(0,r.kt)(l.Z,{groupId:"monix3",defaultValue:"monix3-sbt",values:[{label:"sbt",value:"monix3-sbt"},{label:"sbt (with libraryDependencies)",value:"monix3-sbt-lib"},{label:"scala-cli",value:"monix3-scala-cli"}],mdxType:"Tabs"},(0,r.kt)(o.Z,{value:"monix3-sbt",mdxType:"TabItem"},(0,r.kt)("p",null,"In ",(0,r.kt)("inlineCode",{parentName:"p"},"build.sbt"),","),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'"io.kevinlee" %% "effectie-monix3" % "2.0.0-beta13"\n'))),(0,r.kt)(o.Z,{value:"monix3-sbt-lib",mdxType:"TabItem"},(0,r.kt)("p",null,"In ",(0,r.kt)("inlineCode",{parentName:"p"},"build.sbt"),","),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'libraryDependencies += "io.kevinlee" %% "effectie-monix3" % "2.0.0-beta13"\n'))),(0,r.kt)(o.Z,{value:"monix3-scala-cli",mdxType:"TabItem"},(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'//> using dep "io.kevinlee::effectie-monix3:2.0.0-beta13"\n')))),(0,r.kt)("p",null,"For more details, check out ",(0,r.kt)("a",{parentName:"p",href:"/docs/monix3/"},"Effectie for Monix"),"."),(0,r.kt)("h2",{id:"why"},"Why?"),(0,r.kt)("p",null,"Tagless final gives us power to defer the decision of the implementations of contexts we're binding and functional effect libraries like Cats Effect and Monix give us referential transparency (and more). There might be an issue though with writing implementation for the abstraction which is supposed to support not only effect libraries like Cats Effect but also ",(0,r.kt)("inlineCode",{parentName:"p"},"Future"),". You may end up writing exactly the same code with only an exception to how you construct effect data type (e.g. ",(0,r.kt)("inlineCode",{parentName:"p"},"IO")," vs ",(0,r.kt)("inlineCode",{parentName:"p"},"Future"),"). "),(0,r.kt)("p",null,"Let's check out some code examples."),(0,r.kt)("h3",{id:"problem-duplicate-implementations"},"Problem: Duplicate Implementations"),(0,r.kt)("p",null,"If you use Cats Effect, you may write code like this."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'import cats.syntax.all._\nimport cats.effect._\n\ntrait Foo[F[_]] {\n  def foo(a: Int, b: Int): F[Int]\n}\nobject Foo {\n  def apply[F[_]: Sync](): Foo[F] = new Foo[F] {\n    def foo(a: Int, b: Int): F[Int] =\n      for {\n        n1 <- bar(a)\n        n2 <- bar(b)\n      } yield n1 + n2\n\n    private def bar(n: Int): F[Int] = for {\n      n2 <- Sync[F].delay(math.abs(n))\n      result <- if (n2 < 0)\n                  Sync[F].raiseError(\n                    new IllegalArgumentException("n is Int.MinValue so abs doesn\'t work for it.")\n                  )\n                else\n                  Sync[F].pure(n2)\n    } yield result\n  }\n}\n\nval foo = Foo[IO]()\n// foo: Foo[IO] = repl.MdocSession$App0$Foo$$anon$1@5bb37653\n\nfoo.foo(1, 2).unsafeRunSync()\n// res1: Int = 3\n')),(0,r.kt)("p",null,"Then for some reason, your company uses another tech stack with ",(0,r.kt)("inlineCode",{parentName:"p"},"Future")," so you need to repeat the same logic with ",(0,r.kt)("inlineCode",{parentName:"p"},"Future")," like this."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'import scala.concurrent._\nimport scala.concurrent.ExecutionContext.Implicits.global\n\nclass FooFuture extends Foo[Future] {\n  def foo(a: Int, b: Int): Future[Int] =\n    for {\n      n1 <- bar(a)\n      n2 <- bar(b)\n    } yield n1 + n2\n\n  private def bar(n: Int): Future[Int] = for {\n    n2 <- Future(math.abs(n))\n    result <- if (n2 < 0)\n      Future.failed(\n        new IllegalArgumentException("n is Int.MinValue so abs doesn\'t work for it.")\n      )\n    else\n      Future.successful(n2)\n  } yield result\n}\n\nimport scala.concurrent.duration._\n\nval foo = new FooFuture\n// foo: FooFuture = repl.MdocSession$App0$FooFuture$1@62f56e04\nAwait.result(\n  foo.foo(1, 2),\n  Duration.Inf\n)\n// res2: Int = 3\n')),(0,r.kt)("p",null,"Now you need to continuously spend more time to maintain two code bases for the same operation."),(0,r.kt)("hr",null),(0,r.kt)("h3",{id:"no-more-duplicates-with-effectie"},"No More Duplicates with Effectie"),(0,r.kt)("p",null,"This issue can be solved easily with Effectie."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},'import cats._\nimport cats.syntax.all._\nimport effectie.core._\nimport effectie.syntax.all._\n\ntrait Foo[F[_]] {\n  def foo(a: Int, b: Int): F[Int]\n}\nobject Foo {\n  def apply[F[_]: Fx: Monad](): Foo[F] = new Foo[F] {\n    def foo(a: Int, b: Int): F[Int] =\n      for {\n        n1 <- bar(a)\n        n2 <- bar(b)\n      } yield n1 + n2\n\n    private def bar(n: Int): F[Int] = for {\n      n2 <- effectOf(math.abs(n))\n      result <- if (n2 < 0)\n                  errorOf(\n                    new IllegalArgumentException("n is Int.MinValue so abs doesn\'t work for it.")\n                  )\n                else\n                  pureOf(n2)\n    } yield result\n  }\n}\n')),(0,r.kt)("p",null,"With just one code base above, you can use ",(0,r.kt)("inlineCode",{parentName:"p"},"IO")," or ",(0,r.kt)("inlineCode",{parentName:"p"},"Future")," as you wish like this."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},"import cats.effect._\nimport effectie.instances.ce2.fx._\n\nval foo = Foo[IO]()\n// foo: Foo[IO] = repl.MdocSession$App3$Foo$$anon$2@6dca7565\nfoo.foo(1, 2).unsafeRunSync()\n// res4: Int = 3\n")),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-scala"},"import scala.concurrent._\nimport scala.concurrent.ExecutionContext.Implicits.global\n\nimport scala.concurrent.duration._\n\nimport effectie.instances.future.fx._\nval foo2 = Foo[Future]()\n// foo2: Foo[Future] = repl.MdocSession$App3$Foo$$anon$2@2d8b8761\nAwait.result(\n  foo2.foo(1, 2),\n  Duration.Inf\n)\n// res5: Int = 3\n")),(0,r.kt)("p",null,"As you can see, you can use the same ",(0,r.kt)("inlineCode",{parentName:"p"},"Foo")," for both ",(0,r.kt)("inlineCode",{parentName:"p"},"IO")," and ",(0,r.kt)("inlineCode",{parentName:"p"},"Future"),"."),(0,r.kt)("p",null,"Check out"),(0,r.kt)("ul",null,(0,r.kt)("li",{parentName:"ul"},(0,r.kt)("a",{parentName:"li",href:"/docs/cats-effect2/"},"Effectie for Cats Effect 2")),(0,r.kt)("li",{parentName:"ul"},"Effectie for Cats Effect 3 (Writing docs WIP)"),(0,r.kt)("li",{parentName:"ul"},(0,r.kt)("a",{parentName:"li",href:"/docs/monix3/"},"Effectie for Monix 3"))))}m.isMDXComponent=!0},6907:(e,t,a)=>{a.d(t,{Z:()=>n});const n=a.p+"assets/images/effectie-logo-96x96-e63a86ccb6f2e7637f259ff47575cf69.png"}}]);