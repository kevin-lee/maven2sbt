(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[514,75],{5750:(e,t,n)=>{"use strict";n.r(t),n.d(t,{default:()=>ve});var a=n(7294),o=n(3905),r=n(6291),l=n(6698),c=n(6010),s=n(941),i=n(3783),d=n(7898),m=n(5537),u=n(7462);const p=e=>a.createElement("svg",(0,u.Z)({width:"20",height:"20","aria-hidden":"true"},e),a.createElement("g",{fill:"#7a7a7a"},a.createElement("path",{d:"M9.992 10.023c0 .2-.062.399-.172.547l-4.996 7.492a.982.982 0 01-.828.454H1c-.55 0-1-.453-1-1 0-.2.059-.403.168-.551l4.629-6.942L.168 3.078A.939.939 0 010 2.528c0-.548.45-.997 1-.997h2.996c.352 0 .649.18.828.45L9.82 9.472c.11.148.172.347.172.55zm0 0"}),a.createElement("path",{d:"M19.98 10.023c0 .2-.058.399-.168.547l-4.996 7.492a.987.987 0 01-.828.454h-3c-.547 0-.996-.453-.996-1 0-.2.059-.403.168-.551l4.625-6.942-4.625-6.945a.939.939 0 01-.168-.55 1 1 0 01.996-.997h3c.348 0 .649.18.828.45l4.996 7.492c.11.148.168.347.168.55zm0 0"})));var h=n(4973),y=n(6742),b=n(3919),g=n(8617);const f={menuLinkText:"menuLinkText_OKON"},v=(e,t)=>"link"===e.type?(0,s.Mg)(e.href,t):"category"===e.type&&e.items.some((e=>v(e,t))),k=(0,a.memo)((function(e){let{items:t,...n}=e;return a.createElement(a.Fragment,null,t.map(((e,t)=>a.createElement(E,(0,u.Z)({key:t,item:e},n)))))}));function E(e){let{item:t,...n}=e;return"category"===t.type?0===t.items.length?null:a.createElement(C,(0,u.Z)({item:t},n)):a.createElement(N,(0,u.Z)({item:t},n))}function C(e){let{item:t,onItemClick:n,activePath:o,...r}=e;const{items:l,label:i,collapsible:d}=t,m=v(t,o),{collapsed:p,setCollapsed:h,toggleCollapsed:y}=(0,s.uR)({initialState:()=>!!d&&(!m&&t.collapsed)});return function(e){let{isActive:t,collapsed:n,setCollapsed:o}=e;const r=(0,s.D9)(t);(0,a.useEffect)((()=>{t&&!r&&n&&o(!1)}),[t,r,n])}({isActive:m,collapsed:p,setCollapsed:h}),a.createElement("li",{className:(0,c.Z)(s.kM.docs.docSidebarItemCategory,"menu__list-item",{"menu__list-item--collapsed":p})},a.createElement("a",(0,u.Z)({className:(0,c.Z)("menu__link",{"menu__link--sublist":d,"menu__link--active":d&&m,[f.menuLinkText]:!d}),onClick:d?e=>{e.preventDefault(),y()}:void 0,href:d?"#":void 0},r),i),a.createElement(s.zF,{lazy:!0,as:"ul",className:"menu__list",collapsed:p},a.createElement(k,{items:l,tabIndex:p?-1:0,onItemClick:n,activePath:o})))}function N(e){let{item:t,onItemClick:n,activePath:o,...r}=e;const{href:l,label:i}=t,d=v(t,o);return a.createElement("li",{className:(0,c.Z)(s.kM.docs.docSidebarItemLink,"menu__list-item"),key:i},a.createElement(y.Z,(0,u.Z)({className:(0,c.Z)("menu__link",{"menu__link--active":d}),"aria-current":d?"page":void 0,to:l},(0,b.Z)(l)&&{onClick:n},r),(0,b.Z)(l)?i:a.createElement("span",null,i,a.createElement(g.Z,null))))}const Z={sidebar:"sidebar_a3j0",sidebarWithHideableNavbar:"sidebarWithHideableNavbar_VlPv",sidebarHidden:"sidebarHidden_OqfG",sidebarLogo:"sidebarLogo_hmkv",menu:"menu_cyFh",menuWithAnnouncementBar:"menuWithAnnouncementBar_+O1J",collapseSidebarButton:"collapseSidebarButton_eoK2",collapseSidebarButtonIcon:"collapseSidebarButtonIcon_e+kA",sidebarMenuIcon:"sidebarMenuIcon_iZzd",sidebarMenuCloseIcon:"sidebarMenuCloseIcon_6kU2"};function _(e){let{onClick:t}=e;return a.createElement("button",{type:"button",title:(0,h.I)({id:"theme.docs.sidebar.collapseButtonTitle",message:"Collapse sidebar",description:"The title attribute for collapse button of doc sidebar"}),"aria-label":(0,h.I)({id:"theme.docs.sidebar.collapseButtonAriaLabel",message:"Collapse sidebar",description:"The title attribute for collapse button of doc sidebar"}),className:(0,c.Z)("button button--secondary button--outline",Z.collapseSidebarButton),onClick:t},a.createElement(p,{className:Z.collapseSidebarButtonIcon}))}function T(e){let{path:t,sidebar:n,onCollapse:o,isHidden:r}=e;const l=function(){const{isClosed:e}=(0,s.nT)(),[t,n]=(0,a.useState)(!e);return(0,d.Z)((t=>{let{scrollY:a}=t;e||n(0===a)})),t}(),{navbar:{hideOnScroll:i},hideableSidebar:u}=(0,s.LU)(),{isClosed:p}=(0,s.nT)();return a.createElement("div",{className:(0,c.Z)(Z.sidebar,{[Z.sidebarWithHideableNavbar]:i,[Z.sidebarHidden]:r})},i&&a.createElement(m.Z,{tabIndex:-1,className:Z.sidebarLogo}),a.createElement("nav",{className:(0,c.Z)("menu thin-scrollbar",Z.menu,{[Z.menuWithAnnouncementBar]:!p&&l})},a.createElement("ul",{className:(0,c.Z)(s.kM.docs.docSidebarMenu,"menu__list")},a.createElement(k,{items:n,activePath:t}))),u&&a.createElement(_,{onClick:o}))}const S=e=>{let{toggleSidebar:t,sidebar:n,path:o}=e;return a.createElement("ul",{className:(0,c.Z)(s.kM.docs.docSidebarMenu,"menu__list")},a.createElement(k,{items:n,activePath:o,onItemClick:()=>t()}))};function x(e){return a.createElement(s.Cv,{component:S,props:e})}const I=a.memo(T),B=a.memo(x);function w(e){const t=(0,i.Z)(),n="desktop"===t||"ssr"===t,o="mobile"===t;return a.createElement(a.Fragment,null,n&&a.createElement(I,e),o&&a.createElement(B,e))}var L=n(9105);const j={plain:{backgroundColor:"#2a2734",color:"#9a86fd"},styles:[{types:["comment","prolog","doctype","cdata","punctuation"],style:{color:"#6c6783"}},{types:["namespace"],style:{opacity:.7}},{types:["tag","operator","number"],style:{color:"#e09142"}},{types:["property","function"],style:{color:"#9a86fd"}},{types:["tag-id","selector","atrule-id"],style:{color:"#eeebff"}},{types:["attr-name"],style:{color:"#c4b9fe"}},{types:["boolean","string","entity","url","attr-value","keyword","control","directive","unit","statement","regex","atrule","placeholder","variable"],style:{color:"#ffcc99"}},{types:["deleted"],style:{textDecorationLine:"line-through"}},{types:["inserted"],style:{textDecorationLine:"underline"}},{types:["italic"],style:{fontStyle:"italic"}},{types:["important","bold"],style:{fontWeight:"bold"}},{types:["important"],style:{color:"#c4b9fe"}}]};var M={Prism:n(7410).default,theme:j};function P(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function A(){return A=Object.assign||function(e){for(var t=1;t<arguments.length;t++){var n=arguments[t];for(var a in n)Object.prototype.hasOwnProperty.call(n,a)&&(e[a]=n[a])}return e},A.apply(this,arguments)}var D=/\r\n|\r|\n/,O=function(e){0===e.length?e.push({types:["plain"],content:"\n",empty:!0}):1===e.length&&""===e[0].content&&(e[0].content="\n",e[0].empty=!0)},F=function(e,t){var n=e.length;return n>0&&e[n-1]===t?e:e.concat(t)};function H(e,t){var n={};for(var a in e)Object.prototype.hasOwnProperty.call(e,a)&&-1===t.indexOf(a)&&(n[a]=e[a]);return n}var z=function(e){function t(){for(var t=this,n=[],a=arguments.length;a--;)n[a]=arguments[a];e.apply(this,n),P(this,"getThemeDict",(function(e){if(void 0!==t.themeDict&&e.theme===t.prevTheme&&e.language===t.prevLanguage)return t.themeDict;t.prevTheme=e.theme,t.prevLanguage=e.language;var n=e.theme?function(e,t){var n=e.plain,a=Object.create(null),o=e.styles.reduce((function(e,n){var a=n.languages,o=n.style;return a&&!a.includes(t)||n.types.forEach((function(t){var n=A({},e[t],o);e[t]=n})),e}),a);return o.root=n,o.plain=A({},n,{backgroundColor:null}),o}(e.theme,e.language):void 0;return t.themeDict=n})),P(this,"getLineProps",(function(e){var n=e.key,a=e.className,o=e.style,r=A({},H(e,["key","className","style","line"]),{className:"token-line",style:void 0,key:void 0}),l=t.getThemeDict(t.props);return void 0!==l&&(r.style=l.plain),void 0!==o&&(r.style=void 0!==r.style?A({},r.style,o):o),void 0!==n&&(r.key=n),a&&(r.className+=" "+a),r})),P(this,"getStyleForToken",(function(e){var n=e.types,a=e.empty,o=n.length,r=t.getThemeDict(t.props);if(void 0!==r){if(1===o&&"plain"===n[0])return a?{display:"inline-block"}:void 0;if(1===o&&!a)return r[n[0]];var l=a?{display:"inline-block"}:{},c=n.map((function(e){return r[e]}));return Object.assign.apply(Object,[l].concat(c))}})),P(this,"getTokenProps",(function(e){var n=e.key,a=e.className,o=e.style,r=e.token,l=A({},H(e,["key","className","style","token"]),{className:"token "+r.types.join(" "),children:r.content,style:t.getStyleForToken(r),key:void 0});return void 0!==o&&(l.style=void 0!==l.style?A({},l.style,o):o),void 0!==n&&(l.key=n),a&&(l.className+=" "+a),l})),P(this,"tokenize",(function(e,t,n,a){var o={code:t,grammar:n,language:a,tokens:[]};e.hooks.run("before-tokenize",o);var r=o.tokens=e.tokenize(o.code,o.grammar,o.language);return e.hooks.run("after-tokenize",o),r}))}return e&&(t.__proto__=e),t.prototype=Object.create(e&&e.prototype),t.prototype.constructor=t,t.prototype.render=function(){var e=this.props,t=e.Prism,n=e.language,a=e.code,o=e.children,r=this.getThemeDict(this.props),l=t.languages[n];return o({tokens:function(e){for(var t=[[]],n=[e],a=[0],o=[e.length],r=0,l=0,c=[],s=[c];l>-1;){for(;(r=a[l]++)<o[l];){var i=void 0,d=t[l],m=n[l][r];if("string"==typeof m?(d=l>0?d:["plain"],i=m):(d=F(d,m.type),m.alias&&(d=F(d,m.alias)),i=m.content),"string"==typeof i){var u=i.split(D),p=u.length;c.push({types:d,content:u[0]});for(var h=1;h<p;h++)O(c),s.push(c=[]),c.push({types:d,content:u[h]})}else l++,t.push(d),n.push(i),a.push(0),o.push(i.length)}l--,t.pop(),n.pop(),a.pop(),o.pop()}return O(c),s}(void 0!==l?this.tokenize(t,a,l,n):[a]),className:"prism-code language-"+n,style:void 0!==r?r.root:{},getLineProps:this.getLineProps,getTokenProps:this.getTokenProps})},t}(a.Component);const R=z;var $=n(7594),W=n.n($);const U={plain:{color:"#bfc7d5",backgroundColor:"#292d3e"},styles:[{types:["comment"],style:{color:"rgb(105, 112, 152)",fontStyle:"italic"}},{types:["string","inserted"],style:{color:"rgb(195, 232, 141)"}},{types:["number"],style:{color:"rgb(247, 140, 108)"}},{types:["builtin","char","constant","function"],style:{color:"rgb(130, 170, 255)"}},{types:["punctuation","selector"],style:{color:"rgb(199, 146, 234)"}},{types:["variable"],style:{color:"rgb(191, 199, 213)"}},{types:["class-name","attr-name"],style:{color:"rgb(255, 203, 107)"}},{types:["tag","deleted"],style:{color:"rgb(255, 85, 114)"}},{types:["operator"],style:{color:"rgb(137, 221, 255)"}},{types:["boolean"],style:{color:"rgb(255, 88, 116)"}},{types:["keyword"],style:{fontStyle:"italic"}},{types:["doctype"],style:{color:"rgb(199, 146, 234)",fontStyle:"italic"}},{types:["namespace"],style:{color:"rgb(178, 204, 214)"}},{types:["url"],style:{color:"rgb(221, 221, 221)"}}]};var V=n(5350);const Y=()=>{const{prism:e}=(0,s.LU)(),{isDarkTheme:t}=(0,V.Z)(),n=e.theme||U,a=e.darkTheme||n;return t?a:n},q="codeBlockContainer_J+bg",J="codeBlockContent_csEI",K="codeBlockTitle_oQzk",Q="codeBlock_rtdJ",G="copyButton_M3SB",X="codeBlockLines_1zSZ",ee=/{([\d,-]+)}/,te=["js","jsBlock","jsx","python","html"],ne={js:{start:"\\/\\/",end:""},jsBlock:{start:"\\/\\*",end:"\\*\\/"},jsx:{start:"\\{\\s*\\/\\*",end:"\\*\\/\\s*\\}"},python:{start:"#",end:""},html:{start:"\x3c!--",end:"--\x3e"}},ae=["highlight-next-line","highlight-start","highlight-end"],oe=function(e){void 0===e&&(e=te);const t=e.map((e=>{const{start:t,end:n}=ne[e];return`(?:${t}\\s*(${ae.join("|")})\\s*${n})`})).join("|");return new RegExp(`^\\s*(?:${t})\\s*$`)};function re(e){let{children:t,className:n,metastring:o,title:r}=e;const{prism:l}=(0,s.LU)(),[i,d]=(0,a.useState)(!1),[m,p]=(0,a.useState)(!1);(0,a.useEffect)((()=>{p(!0)}),[]);const y=(0,s.bc)(o)||r,b=(0,a.useRef)(null);let g=[];const f=Y(),v=Array.isArray(t)?t.join(""):t;if(o&&ee.test(o)){const e=o.match(ee)[1];g=W()(e).filter((e=>e>0))}let k=n?.replace(/language-/,"");!k&&l.defaultLanguage&&(k=l.defaultLanguage);let E=v.replace(/\n$/,"");if(0===g.length&&void 0!==k){let e="";const t=(e=>{switch(e){case"js":case"javascript":case"ts":case"typescript":return oe(["js","jsBlock"]);case"jsx":case"tsx":return oe(["js","jsBlock","jsx"]);case"html":return oe(["js","jsBlock","html"]);case"python":case"py":return oe(["python"]);default:return oe()}})(k),n=v.replace(/\n$/,"").split("\n");let a;for(let o=0;o<n.length;){const r=o+1,l=n[o].match(t);if(null!==l){switch(l.slice(1).reduce(((e,t)=>e||t),void 0)){case"highlight-next-line":e+=`${r},`;break;case"highlight-start":a=r;break;case"highlight-end":e+=`${a}-${r-1},`}n.splice(o,1)}else o+=1}g=W()(e),E=n.join("\n")}const C=()=>{!function(e,t){let{target:n=document.body}=void 0===t?{}:t;if("string"!=typeof e)throw new TypeError(`Expected parameter \`text\` to be a \`string\`, got \`${typeof e}\`.`);const a=document.createElement("textarea"),o=document.activeElement;a.value=e,a.setAttribute("readonly",""),a.style.contain="strict",a.style.position="absolute",a.style.left="-9999px",a.style.fontSize="12pt";const r=document.getSelection(),l=r.rangeCount>0&&r.getRangeAt(0);n.append(a),a.select(),a.selectionStart=0,a.selectionEnd=e.length;let c=!1;try{c=document.execCommand("copy")}catch{}a.remove(),l&&(r.removeAllRanges(),r.addRange(l)),o&&o.focus()}(E),d(!0),setTimeout((()=>d(!1)),2e3)};return a.createElement(R,(0,u.Z)({},M,{key:String(m),theme:f,code:E,language:k}),(e=>{let{className:t,style:n,tokens:o,getLineProps:r,getTokenProps:l}=e;return a.createElement("div",{className:q},y&&a.createElement("div",{style:n,className:K},y),a.createElement("div",{className:(0,c.Z)(J,k)},a.createElement("pre",{tabIndex:0,className:(0,c.Z)(t,Q,"thin-scrollbar"),style:n},a.createElement("code",{className:X},o.map(((e,t)=>{1===e.length&&""===e[0].content&&(e[0].content="\n");const n=r({line:e,key:t});return g.includes(t+1)&&(n.className+=" docusaurus-highlight-code-line"),a.createElement("span",(0,u.Z)({key:t},n),e.map(((e,t)=>a.createElement("span",(0,u.Z)({key:t},l({token:e,key:t}))))))})))),a.createElement("button",{ref:b,type:"button","aria-label":(0,h.I)({id:"theme.CodeBlock.copyButtonAriaLabel",message:"Copy code to clipboard",description:"The ARIA label for copy code blocks button"}),className:(0,c.Z)(G,"clean-btn"),onClick:C},i?a.createElement(h.Z,{id:"theme.CodeBlock.copied",description:"The copied button label on code blocks"},"Copied"):a.createElement(h.Z,{id:"theme.CodeBlock.copy",description:"The copy button label on code blocks"},"Copy"))))}))}var le=n(6159);const ce="details_h+cY";function se(e){let{...t}=e;return a.createElement(s.PO,(0,u.Z)({},t,{className:(0,c.Z)("alert alert--info",ce,t.className)}))}const ie={head:e=>{const t=a.Children.map(e.children,(e=>function(e){if(e?.props?.mdxType&&e?.props?.originalType){const{mdxType:t,originalType:n,...o}=e.props;return a.createElement(e.props.originalType,o)}return e}(e)));return a.createElement(L.Z,e,t)},code:e=>{const{children:t}=e;return(0,a.isValidElement)(t)?t:t.includes("\n")?a.createElement(re,e):a.createElement("code",e)},a:e=>a.createElement(y.Z,e),pre:e=>{const{children:t}=e;return(0,a.isValidElement)(t)&&(0,a.isValidElement)(t?.props?.children)?t.props.children:a.createElement(re,(0,a.isValidElement)(t)?t?.props:{...e})},details:e=>{const t=a.Children.toArray(e.children),n=t.find((e=>"summary"===e?.props?.mdxType)),o=a.createElement(a.Fragment,null,t.filter((e=>e!==n)));return a.createElement(se,(0,u.Z)({},e,{summary:n}),o)},h1:(0,le.Z)("h1"),h2:(0,le.Z)("h2"),h3:(0,le.Z)("h3"),h4:(0,le.Z)("h4"),h5:(0,le.Z)("h5"),h6:(0,le.Z)("h6")};var de=n(4608),me=n(6550);const ue="backToTopButton_i9tI",pe="backToTopButtonShow_wCmF",he=!1;function ye(){const e=(0,a.useRef)(null);return{smoothScrollTop:function(){e.current=he?(window.scrollTo({top:0,behavior:"smooth"}),()=>{}):function(){let e=null;return function t(){const n=document.documentElement.scrollTop;n>0&&(e=requestAnimationFrame(t),window.scrollTo(0,Math.floor(.85*n)))}(),()=>e&&cancelAnimationFrame(e)}()},cancelScrollToTop:()=>e.current?.()}}const be=function(){const e=(0,me.TH)(),{smoothScrollTop:t,cancelScrollToTop:n}=ye(),[o,r]=(0,a.useState)(!1);return(0,d.Z)(((e,t)=>{let{scrollY:a}=e;if(!t)return;const o=a<t.scrollY;if(o||n(),a<300)r(!1);else if(o){const e=document.documentElement.scrollHeight;a+window.innerHeight<e&&r(!0)}else r(!1)}),[e]),a.createElement("button",{className:(0,c.Z)("clean-btn",ue,{[pe]:o}),type:"button",onClick:()=>t()},a.createElement("svg",{viewBox:"0 0 24 24",width:"28"},a.createElement("path",{d:"M7.41 15.41L12 10.83l4.59 4.58L18 14l-6-6-6 6z",fill:"currentColor"})))},ge={docPage:"docPage_lDyR",docMainContainer:"docMainContainer_r8cw",docSidebarContainer:"docSidebarContainer_0YBq",docMainContainerEnhanced:"docMainContainerEnhanced_SOUu",docSidebarContainerHidden:"docSidebarContainerHidden_Qlt2",collapsedDocSidebar:"collapsedDocSidebar_zZpm",expandSidebarButtonIcon:"expandSidebarButtonIcon_cxi8",docItemWrapperEnhanced:"docItemWrapperEnhanced_aT5H"};function fe(e){let{currentDocRoute:t,versionMetadata:n,children:r}=e;const{pluginId:i,version:d}=n,m=t.sidebar,u=m?n.docsSidebars[m]:void 0,[y,b]=(0,a.useState)(!1),[g,f]=(0,a.useState)(!1),v=(0,a.useCallback)((()=>{g&&f(!1),b(!y)}),[g]);return a.createElement(l.Z,{wrapperClassName:s.kM.wrapper.docsPages,pageClassName:s.kM.page.docsDocPage,searchMetadatas:{version:d,tag:(0,s.os)(i,d)}},a.createElement("div",{className:ge.docPage},a.createElement(be,null),u&&a.createElement("aside",{className:(0,c.Z)(ge.docSidebarContainer,{[ge.docSidebarContainerHidden]:y}),onTransitionEnd:e=>{e.currentTarget.classList.contains(ge.docSidebarContainer)&&y&&f(!0)}},a.createElement(w,{key:m,sidebar:u,path:t.path,onCollapse:v,isHidden:g}),g&&a.createElement("div",{className:ge.collapsedDocSidebar,title:(0,h.I)({id:"theme.docs.sidebar.expandButtonTitle",message:"Expand sidebar",description:"The ARIA label and title attribute for expand button of doc sidebar"}),"aria-label":(0,h.I)({id:"theme.docs.sidebar.expandButtonAriaLabel",message:"Expand sidebar",description:"The ARIA label and title attribute for expand button of doc sidebar"}),tabIndex:0,role:"button",onKeyDown:v,onClick:v},a.createElement(p,{className:ge.expandSidebarButtonIcon}))),a.createElement("main",{className:(0,c.Z)(ge.docMainContainer,{[ge.docMainContainerEnhanced]:y||!u})},a.createElement("div",{className:(0,c.Z)("container padding-top--md padding-bottom--lg",ge.docItemWrapper,{[ge.docItemWrapperEnhanced]:y})},a.createElement(o.Zo,{components:ie},r)))))}const ve=function(e){const{route:{routes:t},versionMetadata:n,location:o}=e,l=t.find((e=>(0,me.LX)(o.pathname,e)));return l?a.createElement(a.Fragment,null,a.createElement(L.Z,null,a.createElement("html",{className:n.className})),a.createElement(fe,{currentDocRoute:l,versionMetadata:n},(0,r.Z)(t,{versionMetadata:n}))):a.createElement(de.default,e)}},6159:(e,t,n)=>{"use strict";n.d(t,{N:()=>d,Z:()=>m});var a=n(7462),o=n(7294),r=n(6010),l=n(4973),c=n(941);const s="anchorWithStickyNavbar_y2LR",i="anchorWithHideOnScrollNavbar_3ly5",d=function(e){let{...t}=e;return o.createElement("header",null,o.createElement("h1",(0,a.Z)({},t,{id:void 0}),t.children))},m=e=>{return"h1"===e?d:(t=e,function(e){let{id:n,...a}=e;const{navbar:{hideOnScroll:d}}=(0,c.LU)();return n?o.createElement(t,a,o.createElement("a",{"aria-hidden":"true",tabIndex:-1,className:(0,r.Z)("anchor",`anchor__${t}`,{[i]:d,[s]:!d}),id:n}),a.children,o.createElement("a",{className:"hash-link",href:`#${n}`,title:(0,l.I)({id:"theme.common.headingLinkTitle",message:"Direct link to heading",description:"Title for link to heading"})},"#")):o.createElement(t,a)});var t}},4608:(e,t,n)=>{"use strict";n.r(t),n.d(t,{default:()=>l});var a=n(7294),o=n(6698),r=n(4973);const l=function(){return a.createElement(o.Z,{title:(0,r.I)({id:"theme.NotFound.title",message:"Page Not Found"})},a.createElement("main",{className:"container margin-vert--xl"},a.createElement("div",{className:"row"},a.createElement("div",{className:"col col--6 col--offset-3"},a.createElement("h1",{className:"hero__title"},a.createElement(r.Z,{id:"theme.NotFound.title",description:"The title of the 404 page"},"Page Not Found")),a.createElement("p",null,a.createElement(r.Z,{id:"theme.NotFound.p1",description:"The first paragraph of the 404 page"},"We could not find what you were looking for.")),a.createElement("p",null,a.createElement(r.Z,{id:"theme.NotFound.p2",description:"The 2nd paragraph of the 404 page"},"Please contact the owner of the site that linked you to the original URL and let them know their link is broken."))))))}},7594:(e,t)=>{function n(e){let t,n=[];for(let a of e.split(",").map((e=>e.trim())))if(/^-?\d+$/.test(a))n.push(parseInt(a,10));else if(t=a.match(/^(-?\d+)(-|\.\.\.?|\u2025|\u2026|\u22EF)(-?\d+)$/)){let[e,a,o,r]=t;if(a&&r){a=parseInt(a),r=parseInt(r);const e=a<r?1:-1;"-"!==o&&".."!==o&&"\u2025"!==o||(r+=e);for(let t=a;t!==r;t+=e)n.push(t)}}return n}t.default=n,e.exports=n}}]);