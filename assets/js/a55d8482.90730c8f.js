"use strict";(self.webpackChunkwebsite=self.webpackChunkwebsite||[]).push([[997],{742:(e,t,a)=>{a.r(t),a.d(t,{contentTitle:()=>s,default:()=>c,frontMatter:()=>l,metadata:()=>o,toc:()=>m});var n=a(7462),i=(a(7294),a(3905));const l={id:"get-cli",title:"Get maven2sbt CLI",sidebar_label:"Get CLI"},s=void 0,o={unversionedId:"cli/get-cli",id:"cli/get-cli",isDocsHomePage:!1,title:"Get maven2sbt CLI",description:"Standalone CLI App",source:"@site/docs/cli/get-cli.md",sourceDirName:"cli",slug:"/cli/get-cli",permalink:"/docs/cli/get-cli",tags:[],version:"current",frontMatter:{id:"get-cli",title:"Get maven2sbt CLI",sidebar_label:"Get CLI"},sidebar:"docs",previous:{title:"Getting Started",permalink:"/docs/"},next:{title:"How to Use",permalink:"/docs/cli/how-to-use"}},m=[{value:"Standalone CLI App",id:"standalone-cli-app",children:[{value:"macOS (Native)",id:"macos-native",children:[]},{value:"Debian / Ubuntu Linux",id:"debian--ubuntu-linux",children:[]},{value:"Linux / macOS (JVM)",id:"linux--macos-jvm",children:[]},{value:"Windows",id:"windows",children:[]}]}],d={toc:m},r="wrapper";function c(e){let{components:t,...a}=e;return(0,i.kt)(r,(0,n.Z)({},d,a,{components:t,mdxType:"MDXLayout"}),(0,i.kt)("h2",{id:"standalone-cli-app"},"Standalone CLI App"),(0,i.kt)("p",null,"It requires Java 11 or higher. So JRE should be installed and available to run ",(0,i.kt)("inlineCode",{parentName:"p"},"maven2sbt-cli"),"."),(0,i.kt)("h3",{id:"macos-native"},"macOS (Native)"),(0,i.kt)("h4",{id:"install-graalvm-native-image-recommended"},"Install GraalVM Native Image (Recommended)"),(0,i.kt)("p",null,"Just run the following one in the terminal then it installs maven2sbt GraalVM Native Image which does not require JVM so much faster to run."),(0,i.kt)("div",{className:"admonition admonition-tip alert alert--success"},(0,i.kt)("div",{parentName:"div",className:"admonition-heading"},(0,i.kt)("h5",{parentName:"div"},(0,i.kt)("span",{parentName:"h5",className:"admonition-icon"},(0,i.kt)("svg",{parentName:"span",xmlns:"http://www.w3.org/2000/svg",width:"12",height:"16",viewBox:"0 0 12 16"},(0,i.kt)("path",{parentName:"svg",fillRule:"evenodd",d:"M6.5 0C3.48 0 1 2.19 1 5c0 .92.55 2.25 1 3 1.34 2.25 1.78 2.78 2 4v1h5v-1c.22-1.22.66-1.75 2-4 .45-.75 1-2.08 1-3 0-2.81-2.48-5-5.5-5zm3.64 7.48c-.25.44-.47.8-.67 1.11-.86 1.41-1.25 2.06-1.45 3.23-.02.05-.02.11-.02.17H5c0-.06 0-.13-.02-.17-.2-1.17-.59-1.83-1.45-3.23-.2-.31-.42-.67-.67-1.11C2.44 6.78 2 5.65 2 5c0-2.2 2.02-4 4.5-4 1.22 0 2.36.42 3.22 1.19C10.55 2.94 11 3.94 11 5c0 .66-.44 1.78-.86 2.48zM4 14h5c-.23 1.14-1.3 2-2.5 2s-2.27-.86-2.5-2z"}))),"Recommended")),(0,i.kt)("div",{parentName:"div",className:"admonition-content"},(0,i.kt)("pre",{parentName:"div"},(0,i.kt)("code",{parentName:"pre",className:"language-shell"},'bash -c "$(curl -fsSL https://raw.githubusercontent.com/kevin-lee/maven2sbt/main/.scripts/install-graal-macos.sh)"\n')))),(0,i.kt)("h3",{id:"debian--ubuntu-linux"},"Debian / Ubuntu Linux"),(0,i.kt)("h4",{id:"install-graalvm-native-image-recommended-1"},"Install GraalVM Native Image (Recommended)"),(0,i.kt)("p",null,"Just run the following one in the terminal then it installs maven2sbt GraalVM Native Image which does not require JVM so much faster to run."),(0,i.kt)("div",{className:"admonition admonition-tip alert alert--success"},(0,i.kt)("div",{parentName:"div",className:"admonition-heading"},(0,i.kt)("h5",{parentName:"div"},(0,i.kt)("span",{parentName:"h5",className:"admonition-icon"},(0,i.kt)("svg",{parentName:"span",xmlns:"http://www.w3.org/2000/svg",width:"12",height:"16",viewBox:"0 0 12 16"},(0,i.kt)("path",{parentName:"svg",fillRule:"evenodd",d:"M6.5 0C3.48 0 1 2.19 1 5c0 .92.55 2.25 1 3 1.34 2.25 1.78 2.78 2 4v1h5v-1c.22-1.22.66-1.75 2-4 .45-.75 1-2.08 1-3 0-2.81-2.48-5-5.5-5zm3.64 7.48c-.25.44-.47.8-.67 1.11-.86 1.41-1.25 2.06-1.45 3.23-.02.05-.02.11-.02.17H5c0-.06 0-.13-.02-.17-.2-1.17-.59-1.83-1.45-3.23-.2-.31-.42-.67-.67-1.11C2.44 6.78 2 5.65 2 5c0-2.2 2.02-4 4.5-4 1.22 0 2.36.42 3.22 1.19C10.55 2.94 11 3.94 11 5c0 .66-.44 1.78-.86 2.48zM4 14h5c-.23 1.14-1.3 2-2.5 2s-2.27-.86-2.5-2z"}))),"Recommended")),(0,i.kt)("div",{parentName:"div",className:"admonition-content"},(0,i.kt)("pre",{parentName:"div"},(0,i.kt)("code",{parentName:"pre",className:"language-shell"},'bash -c "$(curl -fsSL https://raw.githubusercontent.com/kevin-lee/maven2sbt/main/.scripts/install-graal-ubuntu.sh)"\n')))),(0,i.kt)("h3",{id:"linux--macos-jvm"},"Linux / macOS (JVM)"),(0,i.kt)("h4",{id:"use-curl"},"Use ",(0,i.kt)("inlineCode",{parentName:"h4"},"curl")),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-shell"},'bash -c "$(curl -fsSL https://raw.githubusercontent.com/kevin-lee/maven2sbt/main/.scripts/install.sh)" \n')),(0,i.kt)("hr",null),(0,i.kt)("h4",{id:"or-use-wget"},"Or use ",(0,i.kt)("inlineCode",{parentName:"h4"},"wget")),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-shell"},'bash -c "$(wget -O- https://raw.githubusercontent.com/kevin-lee/maven2sbt/main/.scripts/install.sh)" \n')),(0,i.kt)("hr",null),(0,i.kt)("h4",{id:"or-do-it-manually-not-recommended"},"Or do it manually (not recommended)"),(0,i.kt)("p",null,"Download ",(0,i.kt)("a",{parentName:"p",href:"https://github.com/kevin-lee/maven2sbt/releases/download/v1.5.0/maven2sbt-cli-1.5.0.zip"},"maven2sbt-cli-1.5.0.zip")," and unzip it."),(0,i.kt)("p",null,"Add an alias for convenience to ",(0,i.kt)("inlineCode",{parentName:"p"},"~/.zshrc")," or ",(0,i.kt)("inlineCode",{parentName:"p"},"~/.bashrc")," or ",(0,i.kt)("inlineCode",{parentName:"p"},"~/.bash_profile")," or the run commands file for your shell. "),(0,i.kt)("pre",null,(0,i.kt)("code",{parentName:"pre",className:"language-shell"},"alias maven2sbt='/path/to/maven2sbt-cli-1.5.0/bin/maven2sbt'\n")),(0,i.kt)("h3",{id:"windows"},"Windows"),(0,i.kt)("h4",{id:"install-graalvm-native-image-recommended-2"},"Install GraalVM Native Image (Recommended)"),(0,i.kt)("p",null,"Download maven2sbt-cli-windows-latest.exe blow. Sorry, there's no installation script for Windows yet."),(0,i.kt)("div",{className:"admonition admonition-tip alert alert--success"},(0,i.kt)("div",{parentName:"div",className:"admonition-heading"},(0,i.kt)("h5",{parentName:"div"},(0,i.kt)("span",{parentName:"h5",className:"admonition-icon"},(0,i.kt)("svg",{parentName:"span",xmlns:"http://www.w3.org/2000/svg",width:"12",height:"16",viewBox:"0 0 12 16"},(0,i.kt)("path",{parentName:"svg",fillRule:"evenodd",d:"M6.5 0C3.48 0 1 2.19 1 5c0 .92.55 2.25 1 3 1.34 2.25 1.78 2.78 2 4v1h5v-1c.22-1.22.66-1.75 2-4 .45-.75 1-2.08 1-3 0-2.81-2.48-5-5.5-5zm3.64 7.48c-.25.44-.47.8-.67 1.11-.86 1.41-1.25 2.06-1.45 3.23-.02.05-.02.11-.02.17H5c0-.06 0-.13-.02-.17-.2-1.17-.59-1.83-1.45-3.23-.2-.31-.42-.67-.67-1.11C2.44 6.78 2 5.65 2 5c0-2.2 2.02-4 4.5-4 1.22 0 2.36.42 3.22 1.19C10.55 2.94 11 3.94 11 5c0 .66-.44 1.78-.86 2.48zM4 14h5c-.23 1.14-1.3 2-2.5 2s-2.27-.86-2.5-2z"}))),"Recommended")),(0,i.kt)("div",{parentName:"div",className:"admonition-content"},(0,i.kt)("p",{parentName:"div"},"Download: ",(0,i.kt)("a",{parentName:"p",href:"https://github.com/kevin-lee/maven2sbt/releases/download/v1.5.0/maven2sbt-cli-windows-latest.exe"},"maven2sbt-cli-windows-latest.exe")))),(0,i.kt)("h4",{id:"java-package-jvm"},"Java Package (JVM)"),(0,i.kt)("p",null,"Download and unzip the ",(0,i.kt)("inlineCode",{parentName:"p"},"maven2sbt-cli-1.5.0.zip")," just like Linux or macOS."),(0,i.kt)("p",null,"You can run ",(0,i.kt)("inlineCode",{parentName:"p"},"maven2sbt-cli-1.5.0/bin/maven2sbt.bat")," file but it hasn't been tested."))}c.isMDXComponent=!0}}]);