
var Box2DLib = (() => {
  var _scriptDir = typeof document !== 'undefined' && document.currentScript ? document.currentScript.src : undefined;
  if (typeof __filename !== 'undefined') _scriptDir = _scriptDir || __filename;
  return (
function(Box2DLib) {
  Box2DLib = Box2DLib || {};


var b;b||(b=typeof Box2DLib !== 'undefined' ? Box2DLib : {});var g=Object.assign,h,l;b.ready=new Promise(function(a,c){h=a;l=c});var m=g({},b),n="object"===typeof window,p="function"===typeof importScripts,q="",r,t,u,fs,v,w;
if("object"===typeof process&&"object"===typeof process.versions&&"string"===typeof process.versions.node)q=p?require("path").dirname(q)+"/":__dirname+"/",w=function(){v||(fs=require("fs"),v=require("path"))},r=function(a,c){w();a=v.normalize(a);return fs.readFileSync(a,c?null:"utf8")},u=function(a){a=r(a,!0);a.buffer||(a=new Uint8Array(a));return a},t=function(a,c,f){w();a=v.normalize(a);fs.readFile(a,function(e,d){e?f(e):c(d.buffer)})},1<process.argv.length&&process.argv[1].replace(/\\/g,"/"),process.argv.slice(2),
process.on("uncaughtException",function(a){throw a;}),process.on("unhandledRejection",function(a){throw a;}),b.inspect=function(){return"[Emscripten Module object]"};else if(n||p)p?q=self.location.href:"undefined"!==typeof document&&document.currentScript&&(q=document.currentScript.src),_scriptDir&&(q=_scriptDir),0!==q.indexOf("blob:")?q=q.substr(0,q.replace(/[?#].*/,"").lastIndexOf("/")+1):q="",r=function(a){var c=new XMLHttpRequest;c.open("GET",a,!1);c.send(null);return c.responseText},p&&(u=function(a){var c=
new XMLHttpRequest;c.open("GET",a,!1);c.responseType="arraybuffer";c.send(null);return new Uint8Array(c.response)}),t=function(a,c,f){var e=new XMLHttpRequest;e.open("GET",a,!0);e.responseType="arraybuffer";e.onload=function(){200==e.status||0==e.status&&e.response?c(e.response):f()};e.onerror=f;e.send(null)};b.print||console.log.bind(console);var x=b.printErr||console.warn.bind(console);g(b,m);m=null;var y;b.wasmBinary&&(y=b.wasmBinary);var noExitRuntime=b.noExitRuntime||!0;
"object"!==typeof WebAssembly&&z("no native wasm support detected");var A,B=!1,C="undefined"!==typeof TextDecoder?new TextDecoder("utf8"):void 0,D,E,F=[],G=[],H=[];function aa(){var a=b.preRun.shift();F.unshift(a)}var I=0,J=null,K=null;b.preloadedImages={};b.preloadedAudios={};function z(a){if(b.onAbort)b.onAbort(a);a="Aborted("+a+")";x(a);B=!0;a=new WebAssembly.RuntimeError(a+". Build with -s ASSERTIONS=1 for more info.");l(a);throw a;}
function L(){return M.startsWith("data:application/octet-stream;base64,")}var M;M="box2D.wasm.wasm";if(!L()){var N=M;M=b.locateFile?b.locateFile(N,q):q+N}function O(){var a=M;try{if(a==M&&y)return new Uint8Array(y);if(u)return u(a);throw"both async and sync fetching of the wasm failed";}catch(c){z(c)}}
function ba(){if(!y&&(n||p)){if("function"===typeof fetch&&!M.startsWith("file://"))return fetch(M,{credentials:"same-origin"}).then(function(a){if(!a.ok)throw"failed to load wasm binary file at '"+M+"'";return a.arrayBuffer()}).catch(function(){return O()});if(t)return new Promise(function(a,c){t(M,function(f){a(new Uint8Array(f))},c)})}return Promise.resolve().then(function(){return O()})}
function P(a){for(;0<a.length;){var c=a.shift();if("function"==typeof c)c(b);else{var f=c.u;"number"===typeof f?void 0===c.s?Q(f)():Q(f)(c.s):f(void 0===c.s?null:c.s)}}}var R=[];function Q(a){var c=R[a];c||(a>=R.length&&(R.length=a+1),R[a]=c=E.get(a));return c}var da={b:function(){z("")},a:function(){z("OOM")}};
(function(){function a(d){b.asm=d.exports;A=b.asm.c;d=A.buffer;b.HEAP8=new Int8Array(d);b.HEAP16=new Int16Array(d);b.HEAP32=new Int32Array(d);b.HEAPU8=D=new Uint8Array(d);b.HEAPU16=new Uint16Array(d);b.HEAPU32=new Uint32Array(d);b.HEAPF32=new Float32Array(d);b.HEAPF64=new Float64Array(d);E=b.asm.h;G.unshift(b.asm.d);I--;b.monitorRunDependencies&&b.monitorRunDependencies(I);0==I&&(null!==J&&(clearInterval(J),J=null),K&&(d=K,K=null,d()))}function c(d){a(d.instance)}function f(d){return ba().then(function(k){return WebAssembly.instantiate(k,
e)}).then(function(k){return k}).then(d,function(k){x("failed to asynchronously prepare wasm: "+k);z(k)})}var e={a:da};I++;b.monitorRunDependencies&&b.monitorRunDependencies(I);if(b.instantiateWasm)try{return b.instantiateWasm(e,a)}catch(d){return x("Module.instantiateWasm callback failed with error: "+d),!1}(function(){return y||"function"!==typeof WebAssembly.instantiateStreaming||L()||M.startsWith("file://")||"function"!==typeof fetch?f(c):fetch(M,{credentials:"same-origin"}).then(function(d){return WebAssembly.instantiateStreaming(d,
e).then(c,function(k){x("wasm streaming compile failed: "+k);x("falling back to ArrayBuffer instantiation");return f(c)})})})().catch(l);return{}})();b.___wasm_call_ctors=function(){return(b.___wasm_call_ctors=b.asm.d).apply(null,arguments)};
var S=b._emscripten_bind_VoidPtr___destroy___0=function(){return(S=b._emscripten_bind_VoidPtr___destroy___0=b.asm.e).apply(null,arguments)},ea=b._emscripten_bind_b2Vec2_b2Vec2_0=function(){return(ea=b._emscripten_bind_b2Vec2_b2Vec2_0=b.asm.f).apply(null,arguments)},fa=b._emscripten_bind_b2Vec2___destroy___0=function(){return(fa=b._emscripten_bind_b2Vec2___destroy___0=b.asm.g).apply(null,arguments)};b._malloc=function(){return(b._malloc=b.asm.i).apply(null,arguments)};
b._free=function(){return(b._free=b.asm.j).apply(null,arguments)};
b.UTF8ToString=function(a,c){if(a){var f=D,e=a+c;for(c=a;f[c]&&!(c>=e);)++c;if(16<c-a&&f.subarray&&C)a=C.decode(f.subarray(a,c));else{for(e="";a<c;){var d=f[a++];if(d&128){var k=f[a++]&63;if(192==(d&224))e+=String.fromCharCode((d&31)<<6|k);else{var ca=f[a++]&63;d=224==(d&240)?(d&15)<<12|k<<6|ca:(d&7)<<18|k<<12|ca<<6|f[a++]&63;65536>d?e+=String.fromCharCode(d):(d-=65536,e+=String.fromCharCode(55296|d>>10,56320|d&1023))}}else e+=String.fromCharCode(d)}a=e}}else a="";return a};var T;
K=function ha(){T||U();T||(K=ha)};
function U(){function a(){if(!T&&(T=!0,b.calledRun=!0,!B)){P(G);h(b);if(b.onRuntimeInitialized)b.onRuntimeInitialized();if(b.postRun)for("function"==typeof b.postRun&&(b.postRun=[b.postRun]);b.postRun.length;){var c=b.postRun.shift();H.unshift(c)}P(H)}}if(!(0<I)){if(b.preRun)for("function"==typeof b.preRun&&(b.preRun=[b.preRun]);b.preRun.length;)aa();P(F);0<I||(b.setStatus?(b.setStatus("Running..."),setTimeout(function(){setTimeout(function(){b.setStatus("")},1);a()},1)):a())}}b.run=U;
if(b.preInit)for("function"==typeof b.preInit&&(b.preInit=[b.preInit]);0<b.preInit.length;)b.preInit.pop()();U();function V(){}V.prototype=Object.create(V.prototype);V.prototype.constructor=V;V.prototype.m=V;V.o={};b.WrapperObject=V;function W(a){return(a||V).o}b.getCache=W;function X(a,c){var f=W(c),e=f[a];if(e)return e;e=Object.create((c||V).prototype);e.l=a;return f[a]=e}b.wrapPointer=X;b.castObject=function(a,c){return X(a.l,c)};b.NULL=X(0);
b.destroy=function(a){if(!a.__destroy__)throw"Error: Cannot destroy object. (Did you create it yourself?)";a.__destroy__();delete W(a.m)[a.l]};b.compare=function(a,c){return a.l===c.l};b.getPointer=function(a){return a.l};b.getClass=function(a){return a.m};function Y(){throw"cannot construct a VoidPtr, no constructor in IDL";}Y.prototype=Object.create(V.prototype);Y.prototype.constructor=Y;Y.prototype.m=Y;Y.o={};b.VoidPtr=Y;Y.prototype.__destroy__=function(){S(this.l)};
function Z(){this.l=ea();W(Z)[this.l]=this}Z.prototype=Object.create(V.prototype);Z.prototype.constructor=Z;Z.prototype.m=Z;Z.o={};b.b2Vec2=Z;Z.prototype.__destroy__=function(){fa(this.l)};


  return Box2DLib.ready
}
);
})();
if (typeof exports === 'object' && typeof module === 'object')
  module.exports = Box2DLib;
else if (typeof define === 'function' && define['amd'])
  define([], function() { return Box2DLib; });
else if (typeof exports === 'object')
  exports["Box2DLib"] = Box2DLib;
