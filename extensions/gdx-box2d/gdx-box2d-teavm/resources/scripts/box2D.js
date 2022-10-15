
var Box2DLib = (() => {
  var _scriptDir = typeof document !== 'undefined' && document.currentScript ? document.currentScript.src : undefined;
  if (typeof __filename !== 'undefined') _scriptDir = _scriptDir || __filename;
  return (
function(Box2DLib) {
  Box2DLib = Box2DLib || {};


var da="function"==typeof Object.defineProperties?Object.defineProperty:function(a,c,d){if(a==Array.prototype||a==Object.prototype)return a;a[c]=d.value;return a};function ea(a){a=["object"==typeof globalThis&&globalThis,a,"object"==typeof window&&window,"object"==typeof self&&self,"object"==typeof global&&global];for(var c=0;c<a.length;++c){var d=a[c];if(d&&d.Math==Math)return d}throw Error("Cannot find global object");}var fa=ea(this);
function ha(a,c){if(c)a:{var d=fa;a=a.split(".");for(var f=0;f<a.length-1;f++){var h=a[f];if(!(h in d))break a;d=d[h]}a=a[a.length-1];f=d[a];c=c(f);c!=f&&null!=c&&da(d,a,{configurable:!0,writable:!0,value:c})}}
ha("String.prototype.startsWith",function(a){return a?a:function(c,d){if(null==this)throw new TypeError("The 'this' value for String.prototype.startsWith must not be null or undefined");if(c instanceof RegExp)throw new TypeError("First argument to String.prototype.startsWith must not be a regular expression");var f=this.length,h=c.length;d=Math.max(0,Math.min(d|0,this.length));for(var m=0;m<h&&d<f;)if(this[d++]!=c[m++])return!1;return m>=h}});var b;b||(b=typeof Box2DLib !== 'undefined' ? Box2DLib : {});
var Promise=function(){function a(){}function c(e,g){return function(){e.apply(g,arguments)}}function d(e){if(!(this instanceof d))throw new TypeError("Promises must be constructed via new");if("function"!==typeof e)throw new TypeError("not a function");this.A=0;this.H=!1;this.B=void 0;this.C=[];aa(e,this)}function f(e,g){for(;3===e.A;)e=e.B;0===e.A?e.C.push(g):(e.H=!0,d.I(function(){var l=1===e.A?g.M:g.N;if(null===l)(1===e.A?h:m)(g.promise,e.B);else{try{var n=l(e.B)}catch(t){m(g.promise,t);return}h(g.promise,
n)}}))}function h(e,g){try{if(g===e)throw new TypeError("A promise cannot be resolved with itself.");if(g&&("object"===typeof g||"function"===typeof g)){var l=g.then;if(g instanceof d){e.A=3;e.B=g;y(e);return}if("function"===typeof l){aa(c(l,g),e);return}}e.A=1;e.B=g;y(e)}catch(n){m(e,n)}}function m(e,g){e.A=2;e.B=g;y(e)}function y(e){2===e.A&&0===e.C.length&&d.I(function(){e.H||d.J(e.B)});for(var g=0,l=e.C.length;g<l;g++)f(e,e.C[g]);e.C=null}function Q(e,g,l){this.M="function"===typeof e?e:null;
this.N="function"===typeof g?g:null;this.promise=l}function aa(e,g){var l=!1;try{e(function(n){l||(l=!0,h(g,n))},function(n){l||(l=!0,m(g,n))})}catch(n){l||(l=!0,m(g,n))}}d.prototype["catch"]=function(e){return this.then(null,e)};d.prototype.then=function(e,g){var l=new this.constructor(a);f(this,new Q(e,g,l));return l};d.all=function(e){return new d(function(g,l){function n(ba,z){try{if(z&&("object"===typeof z||"function"===typeof z)){var ca=z.then;if("function"===typeof ca){ca.call(z,function(R){n(ba,
R)},l);return}}t[ba]=z;0===--Ma&&g(t)}catch(R){l(R)}}if(!Array.isArray(e))return l(new TypeError("Promise.all accepts an array"));var t=Array.prototype.slice.call(e);if(0===t.length)return g([]);for(var Ma=t.length,H=0;H<t.length;H++)n(H,t[H])})};d.resolve=function(e){return e&&"object"===typeof e&&e.constructor===d?e:new d(function(g){g(e)})};d.reject=function(e){return new d(function(g,l){l(e)})};d.race=function(e){return new d(function(g,l){if(!Array.isArray(e))return l(new TypeError("Promise.race accepts an array"));
for(var n=0,t=e.length;n<t;n++)d.resolve(e[n]).then(g,l)})};d.I="function"===typeof setImmediate&&function(e){setImmediate(e)}||function(e){setTimeout(e,0)};d.J=function(e){"undefined"!==typeof console&&console&&console.warn("Possible Unhandled Promise Rejection:",e)};return d}();function ia(a,c){for(var d in c)c.hasOwnProperty(d)&&(a[d]=c[d]);return a}var ja,k;b.ready=new Promise(function(a,c){ja=a;k=c});
var ka=ia({},b),la="object"===typeof window,p="function"===typeof importScripts,q="object"===typeof process&&"object"===typeof process.versions&&"string"===typeof process.versions.node,r="",u,v,w,fs,x,A;
if(q)r=p?require("path").dirname(r)+"/":__dirname+"/",A=function(){x||(fs=require("fs"),x=require("path"))},u=function(a,c){var d=B(a);if(d)return c?d:d.toString();A();a=x.normalize(a);return fs.readFileSync(a,c?null:"utf8")},w=function(a){a=u(a,!0);a.buffer||(a=new Uint8Array(a));return a},v=function(a,c,d){var f=B(a);f&&c(f);A();a=x.normalize(a);fs.readFile(a,function(h,m){h?d(h):c(m.buffer)})},1<process.argv.length&&process.argv[1].replace(/\\/g,"/"),process.argv.slice(2),process.on("uncaughtException",
function(a){throw a;}),process.on("unhandledRejection",function(a){throw a;}),b.inspect=function(){return"[Emscripten Module object]"};else if(la||p)p?r=self.location.href:"undefined"!==typeof document&&document.currentScript&&(r=document.currentScript.src),_scriptDir&&(r=_scriptDir),0!==r.indexOf("blob:")?r=r.substr(0,r.replace(/[?#].*/,"").lastIndexOf("/")+1):r="",u=function(a){try{var c=new XMLHttpRequest;c.open("GET",a,!1);c.send(null);return c.responseText}catch(h){if(a=B(a)){c=[];for(var d=
0;d<a.length;d++){var f=a[d];255<f&&(ma&&C("Character code "+f+" ("+String.fromCharCode(f)+")  at offset "+d+" not in 0x00-0xFF."),f&=255);c.push(String.fromCharCode(f))}return c.join("")}throw h;}},p&&(w=function(a){try{var c=new XMLHttpRequest;c.open("GET",a,!1);c.responseType="arraybuffer";c.send(null);return new Uint8Array(c.response)}catch(d){if(a=B(a))return a;throw d;}}),v=function(a,c,d){var f=new XMLHttpRequest;f.open("GET",a,!0);f.responseType="arraybuffer";f.onload=function(){if(200==f.status||
0==f.status&&f.response)c(f.response);else{var h=B(a);h?c(h.buffer):d()}};f.onerror=d;f.send(null)};b.print||console.log.bind(console);var D=b.printErr||console.warn.bind(console);ia(b,ka);ka=null;var E;b.wasmBinary&&(E=b.wasmBinary);var noExitRuntime=b.noExitRuntime||!0;function na(){this.buffer=new ArrayBuffer(oa/65536*65536)}function pa(){this.exports=(
// EMSCRIPTEN_START_ASM
function instantiate(X){function e(f){f.grow=function(b){var a=this.length;this.length=this.length+b;return a};f.set=function(c,d){this[c]=d};f.get=function(c){return this[c]};return f}var g;var h=new Uint8Array(123);for(var c=25;c>=0;--c){h[48+c]=52+c;h[65+c]=c;h[97+c]=26+c}h[43]=62;h[47]=63;function n(o,p,q){var i,j,c=0,k=p,l=q.length,m=p+(l*3>>2)-(q[l-2]=="=")-(q[l-1]=="=");for(;c<l;c+=4){i=h[q.charCodeAt(c+1)];j=h[q.charCodeAt(c+2)];o[k++]=h[q.charCodeAt(c)]<<2|i>>4;if(k<m)o[k++]=i<<4|j>>2;if(k<m)o[k++]=j<<6|h[q.charCodeAt(c+3)]}}function r(s){n(g,1024,"YAZQ")}function V(W){var t=W.a;var u=t.buffer;var v=new Int8Array(u);var w=new Int16Array(u);var x=new Int32Array(u);var y=new Uint8Array(u);var z=new Uint16Array(u);var A=new Uint32Array(u);var B=new Float32Array(u);var C=new Float64Array(u);var D=Math.imul;var E=Math.fround;var F=Math.abs;var G=Math.clz32;var H=Math.min;var I=Math.max;var J=Math.floor;var K=Math.ceil;var L=Math.trunc;var M=Math.sqrt;var N=W.abort;var O=NaN;var P=Infinity;var Q=W.b;var R=W.c;var S=5244512;
// EMSCRIPTEN_START_FUNCS
function $(a){a=a|0;var b=0,c=0,d=0,e=0,f=0,g=0,h=0,i=0,j=0,k=0,l=0;l=S-16|0;S=l;a:{b:{c:{d:{e:{f:{g:{h:{i:{j:{k:{if(a>>>0<=244){e=x[284];h=a>>>0<11?16:a+11&-8;c=h>>>3|0;b=e>>>c|0;if(b&3){d=c+((b^-1)&1)|0;b=d<<3;f=x[b+1184>>2];a=f+8|0;c=x[f+8>>2];b=b+1176|0;l:{if((c|0)==(b|0)){x[284]=pa(d)&e;break l}x[c+12>>2]=b;x[b+8>>2]=c}b=d<<3;x[f+4>>2]=b|3;b=b+f|0;x[b+4>>2]=x[b+4>>2]|1;break a}k=x[286];if(k>>>0>=h>>>0){break k}if(b){a=2<<c;a=(0-a|a)&b<<c;b=(0-a&a)-1|0;a=b>>>12&16;c=a;b=b>>>a|0;a=b>>>5&8;c=c|a;b=b>>>a|0;a=b>>>2&4;c=c|a;b=b>>>a|0;a=b>>>1&2;c=c|a;b=b>>>a|0;a=b>>>1&1;c=(c|a)+(b>>>a|0)|0;a=c<<3;g=x[a+1184>>2];b=x[g+8>>2];a=a+1176|0;m:{if((b|0)==(a|0)){e=pa(c)&e;x[284]=e;break m}x[b+12>>2]=a;x[a+8>>2]=b}a=g+8|0;x[g+4>>2]=h|3;d=g+h|0;b=c<<3;f=b-h|0;x[d+4>>2]=f|1;x[b+g>>2]=f;if(k){b=k>>>3|0;c=(b<<3)+1176|0;g=x[289];b=1<<b;n:{if(!(b&e)){x[284]=b|e;b=c;break n}b=x[c+8>>2]}x[c+8>>2]=g;x[b+12>>2]=g;x[g+12>>2]=c;x[g+8>>2]=b}x[289]=d;x[286]=f;break a}j=x[285];if(!j){break k}b=(j&0-j)-1|0;a=b>>>12&16;c=a;b=b>>>a|0;a=b>>>5&8;c=c|a;b=b>>>a|0;a=b>>>2&4;c=c|a;b=b>>>a|0;a=b>>>1&2;c=c|a;b=b>>>a|0;a=b>>>1&1;b=x[((c|a)+(b>>>a|0)<<2)+1440>>2];d=(x[b+4>>2]&-8)-h|0;c=b;while(1){o:{a=x[c+16>>2];if(!a){a=x[c+20>>2];if(!a){break o}}c=(x[a+4>>2]&-8)-h|0;f=c>>>0<d>>>0;d=f?c:d;b=f?a:b;c=a;continue}break}i=x[b+24>>2];f=x[b+12>>2];if((f|0)!=(b|0)){a=x[b+8>>2];x[a+12>>2]=f;x[f+8>>2]=a;break b}c=b+20|0;a=x[c>>2];if(!a){a=x[b+16>>2];if(!a){break j}c=b+16|0}while(1){g=c;f=a;c=a+20|0;a=x[c>>2];if(a){continue}c=f+16|0;a=x[f+16>>2];if(a){continue}break}x[g>>2]=0;break b}h=-1;if(a>>>0>4294967231){break k}a=a+11|0;h=a&-8;j=x[285];if(!j){break k}d=0-h|0;e=0;p:{if(h>>>0<256){break p}e=31;if(h>>>0>16777215){break p}a=a>>>8|0;g=a+1048320>>>16&8;a=a<<g;c=a+520192>>>16&4;a=a<<c;b=a+245760>>>16&2;a=(a<<b>>>15|0)-(b|(c|g))|0;e=(a<<1|h>>>a+21&1)+28|0}c=x[(e<<2)+1440>>2];q:{r:{s:{if(!c){a=0;break s}a=0;b=h<<((e|0)==31?0:25-(e>>>1|0)|0);while(1){t:{g=(x[c+4>>2]&-8)-h|0;if(g>>>0>=d>>>0){break t}f=c;d=g;if(d){break t}d=0;a=c;break r}g=x[c+20>>2];c=x[((b>>>29&4)+c|0)+16>>2];a=g?(g|0)==(c|0)?a:g:a;b=b<<1;if(c){continue}break}}if(!(a|f)){f=0;a=2<<e;a=(0-a|a)&j;if(!a){break k}b=(a&0-a)-1|0;a=b>>>12&16;c=a;b=b>>>a|0;a=b>>>5&8;c=c|a;b=b>>>a|0;a=b>>>2&4;c=c|a;b=b>>>a|0;a=b>>>1&2;c=c|a;b=b>>>a|0;a=b>>>1&1;a=x[((c|a)+(b>>>a|0)<<2)+1440>>2]}if(!a){break q}}while(1){b=(x[a+4>>2]&-8)-h|0;c=b>>>0<d>>>0;d=c?b:d;f=c?a:f;b=x[a+16>>2];if(b){a=b}else{a=x[a+20>>2]}if(a){continue}break}}if(!f|x[286]-h>>>0<=d>>>0){break k}e=x[f+24>>2];b=x[f+12>>2];if((f|0)!=(b|0)){a=x[f+8>>2];x[a+12>>2]=b;x[b+8>>2]=a;break c}c=f+20|0;a=x[c>>2];if(!a){a=x[f+16>>2];if(!a){break i}c=f+16|0}while(1){g=c;b=a;c=a+20|0;a=x[c>>2];if(a){continue}c=b+16|0;a=x[b+16>>2];if(a){continue}break}x[g>>2]=0;break c}c=x[286];if(c>>>0>=h>>>0){d=x[289];b=c-h|0;u:{if(b>>>0>=16){x[286]=b;a=d+h|0;x[289]=a;x[a+4>>2]=b|1;x[c+d>>2]=b;x[d+4>>2]=h|3;break u}x[289]=0;x[286]=0;x[d+4>>2]=c|3;a=c+d|0;x[a+4>>2]=x[a+4>>2]|1}a=d+8|0;break a}i=x[287];if(i>>>0>h>>>0){b=i-h|0;x[287]=b;c=x[290];a=c+h|0;x[290]=a;x[a+4>>2]=b|1;x[c+4>>2]=h|3;a=c+8|0;break a}a=0;j=h+47|0;if(x[402]){c=x[404]}else{x[405]=-1;x[406]=-1;x[403]=4096;x[404]=4096;x[402]=l+12&-16^1431655768;x[407]=0;x[395]=0;c=4096}g=j+c|0;f=0-c|0;c=g&f;if(c>>>0<=h>>>0){break a}d=x[394];if(d){b=x[392];e=b+c|0;if(d>>>0<e>>>0|b>>>0>=e>>>0){break a}}if(y[1580]&4){break f}v:{w:{d=x[290];if(d){a=1584;while(1){b=x[a>>2];if(b>>>0<=d>>>0&d>>>0<b+x[a+4>>2]>>>0){break w}a=x[a+8>>2];if(a){continue}break}}b=Y(0);if((b|0)==-1){break g}e=c;d=x[403];a=d-1|0;if(a&b){e=(c-b|0)+(a+b&0-d)|0}if(e>>>0<=h>>>0|e>>>0>2147483646){break g}d=x[394];if(d){a=x[392];f=a+e|0;if(d>>>0<f>>>0|a>>>0>=f>>>0){break g}}a=Y(e);if((b|0)!=(a|0)){break v}break e}e=f&g-i;if(e>>>0>2147483646){break g}b=Y(e);if((b|0)==(x[a>>2]+x[a+4>>2]|0)){break h}a=b}if(!((a|0)==-1|h+48>>>0<=e>>>0)){b=x[404];b=b+(j-e|0)&0-b;if(b>>>0>2147483646){b=a;break e}if((Y(b)|0)!=-1){e=b+e|0;b=a;break e}Y(0-e|0);break g}b=a;if((a|0)!=-1){break e}break g}f=0;break b}b=0;break c}if((b|0)!=-1){break e}}x[395]=x[395]|4}if(c>>>0>2147483646){break d}b=Y(c);a=Y(0);if((b|0)==-1|(a|0)==-1|a>>>0<=b>>>0){break d}e=a-b|0;if(e>>>0<=h+40>>>0){break d}}a=x[392]+e|0;x[392]=a;if(a>>>0>A[393]){x[393]=a}x:{y:{z:{g=x[290];if(g){a=1584;while(1){d=x[a>>2];c=x[a+4>>2];if((d+c|0)==(b|0)){break z}a=x[a+8>>2];if(a){continue}break}break y}a=x[288];if(!(a>>>0<=b>>>0?a:0)){x[288]=b}a=0;x[397]=e;x[396]=b;x[292]=-1;x[293]=x[402];x[399]=0;while(1){d=a<<3;c=d+1176|0;x[d+1184>>2]=c;x[d+1188>>2]=c;a=a+1|0;if((a|0)!=32){continue}break}d=e-40|0;a=b+8&7?-8-b&7:0;c=d-a|0;x[287]=c;a=a+b|0;x[290]=a;x[a+4>>2]=c|1;x[(b+d|0)+4>>2]=40;x[291]=x[406];break x}if(y[a+12|0]&8|d>>>0>g>>>0|b>>>0<=g>>>0){break y}x[a+4>>2]=c+e;a=g+8&7?-8-g&7:0;c=a+g|0;x[290]=c;b=x[287]+e|0;a=b-a|0;x[287]=a;x[c+4>>2]=a|1;x[(b+g|0)+4>>2]=40;x[291]=x[406];break x}if(A[288]>b>>>0){x[288]=b}c=b+e|0;a=1584;A:{B:{C:{D:{E:{F:{while(1){if((c|0)!=x[a>>2]){a=x[a+8>>2];if(a){continue}break F}break}if(!(y[a+12|0]&8)){break E}}a=1584;while(1){c=x[a>>2];if(c>>>0<=g>>>0){f=c+x[a+4>>2]|0;if(f>>>0>g>>>0){break D}}a=x[a+8>>2];continue}}x[a>>2]=b;x[a+4>>2]=x[a+4>>2]+e;j=(b+8&7?-8-b&7:0)+b|0;x[j+4>>2]=h|3;e=c+(c+8&7?-8-c&7:0)|0;i=h+j|0;c=e-i|0;if((e|0)==(g|0)){x[290]=i;a=x[287]+c|0;x[287]=a;x[i+4>>2]=a|1;break B}if(x[289]==(e|0)){x[289]=i;a=x[286]+c|0;x[286]=a;x[i+4>>2]=a|1;x[a+i>>2]=a;break B}a=x[e+4>>2];if((a&3)==1){g=a&-8;G:{if(a>>>0<=255){d=x[e+8>>2];a=a>>>3|0;b=x[e+12>>2];if((b|0)==(d|0)){x[284]=x[284]&pa(a);break G}x[d+12>>2]=b;x[b+8>>2]=d;break G}h=x[e+24>>2];b=x[e+12>>2];H:{if((e|0)!=(b|0)){a=x[e+8>>2];x[a+12>>2]=b;x[b+8>>2]=a;break H}I:{a=e+20|0;d=x[a>>2];if(d){break I}a=e+16|0;d=x[a>>2];if(d){break I}b=0;break H}while(1){f=a;b=d;a=b+20|0;d=x[a>>2];if(d){continue}a=b+16|0;d=x[b+16>>2];if(d){continue}break}x[f>>2]=0}if(!h){break G}d=x[e+28>>2];a=(d<<2)+1440|0;J:{if(x[a>>2]==(e|0)){x[a>>2]=b;if(b){break J}x[285]=x[285]&pa(d);break G}x[h+(x[h+16>>2]==(e|0)?16:20)>>2]=b;if(!b){break G}}x[b+24>>2]=h;a=x[e+16>>2];if(a){x[b+16>>2]=a;x[a+24>>2]=b}a=x[e+20>>2];if(!a){break G}x[b+20>>2]=a;x[a+24>>2]=b}e=e+g|0;c=c+g|0}x[e+4>>2]=x[e+4>>2]&-2;x[i+4>>2]=c|1;x[c+i>>2]=c;if(c>>>0<=255){a=c>>>3|0;b=(a<<3)+1176|0;c=x[284];a=1<<a;K:{if(!(c&a)){x[284]=a|c;a=b;break K}a=x[b+8>>2]}x[b+8>>2]=i;x[a+12>>2]=i;x[i+12>>2]=b;x[i+8>>2]=a;break B}a=31;if(c>>>0<=16777215){a=c>>>8|0;f=a+1048320>>>16&8;a=a<<f;d=a+520192>>>16&4;a=a<<d;b=a+245760>>>16&2;a=(a<<b>>>15|0)-(b|(d|f))|0;a=(a<<1|c>>>a+21&1)+28|0}x[i+28>>2]=a;x[i+16>>2]=0;x[i+20>>2]=0;f=(a<<2)+1440|0;d=x[285];b=1<<a;L:{if(!(d&b)){x[285]=b|d;x[f>>2]=i;x[i+24>>2]=f;break L}a=c<<((a|0)==31?0:25-(a>>>1|0)|0);b=x[f>>2];while(1){d=b;if((x[b+4>>2]&-8)==(c|0)){break C}b=a>>>29|0;a=a<<1;f=d+(b&4)|0;b=x[f+16>>2];if(b){continue}break}x[f+16>>2]=i;x[i+24>>2]=d}x[i+12>>2]=i;x[i+8>>2]=i;break B}d=e-40|0;a=b+8&7?-8-b&7:0;c=d-a|0;x[287]=c;a=a+b|0;x[290]=a;x[a+4>>2]=c|1;x[(b+d|0)+4>>2]=40;x[291]=x[406];a=(f+(f-39&7?39-f&7:0)|0)-47|0;c=a>>>0<g+16>>>0?g:a;x[c+4>>2]=27;a=x[399];x[c+16>>2]=x[398];x[c+20>>2]=a;a=x[397];x[c+8>>2]=x[396];x[c+12>>2]=a;x[398]=c+8;x[397]=e;x[396]=b;x[399]=0;a=c+24|0;while(1){x[a+4>>2]=7;b=a+8|0;a=a+4|0;if(b>>>0<f>>>0){continue}break}if((c|0)==(g|0)){break x}x[c+4>>2]=x[c+4>>2]&-2;f=c-g|0;x[g+4>>2]=f|1;x[c>>2]=f;if(f>>>0<=255){a=f>>>3|0;b=(a<<3)+1176|0;c=x[284];a=1<<a;M:{if(!(c&a)){x[284]=a|c;a=b;break M}a=x[b+8>>2]}x[b+8>>2]=g;x[a+12>>2]=g;x[g+12>>2]=b;x[g+8>>2]=a;break x}a=31;x[g+16>>2]=0;x[g+20>>2]=0;if(f>>>0<=16777215){a=f>>>8|0;d=a+1048320>>>16&8;a=a<<d;c=a+520192>>>16&4;a=a<<c;b=a+245760>>>16&2;a=(a<<b>>>15|0)-(b|(c|d))|0;a=(a<<1|f>>>a+21&1)+28|0}x[g+28>>2]=a;d=(a<<2)+1440|0;c=x[285];b=1<<a;N:{if(!(c&b)){x[285]=b|c;x[d>>2]=g;x[g+24>>2]=d;break N}a=f<<((a|0)==31?0:25-(a>>>1|0)|0);b=x[d>>2];while(1){c=b;if((f|0)==(x[b+4>>2]&-8)){break A}b=a>>>29|0;a=a<<1;d=c+(b&4)|0;b=x[d+16>>2];if(b){continue}break}x[d+16>>2]=g;x[g+24>>2]=c}x[g+12>>2]=g;x[g+8>>2]=g;break x}a=x[d+8>>2];x[a+12>>2]=i;x[d+8>>2]=i;x[i+24>>2]=0;x[i+12>>2]=d;x[i+8>>2]=a}a=j+8|0;break a}a=x[c+8>>2];x[a+12>>2]=g;x[c+8>>2]=g;x[g+24>>2]=0;x[g+12>>2]=c;x[g+8>>2]=a}a=x[287];if(a>>>0<=h>>>0){break d}b=a-h|0;x[287]=b;c=x[290];a=c+h|0;x[290]=a;x[a+4>>2]=b|1;x[c+4>>2]=h|3;a=c+8|0;break a}x[283]=48;a=0;break a}O:{if(!e){break O}c=x[f+28>>2];a=(c<<2)+1440|0;P:{if(x[a>>2]==(f|0)){x[a>>2]=b;if(b){break P}j=pa(c)&j;x[285]=j;break O}x[e+(x[e+16>>2]==(f|0)?16:20)>>2]=b;if(!b){break O}}x[b+24>>2]=e;a=x[f+16>>2];if(a){x[b+16>>2]=a;x[a+24>>2]=b}a=x[f+20>>2];if(!a){break O}x[b+20>>2]=a;x[a+24>>2]=b}Q:{if(d>>>0<=15){a=d+h|0;x[f+4>>2]=a|3;a=a+f|0;x[a+4>>2]=x[a+4>>2]|1;break Q}x[f+4>>2]=h|3;e=f+h|0;x[e+4>>2]=d|1;x[d+e>>2]=d;if(d>>>0<=255){a=d>>>3|0;b=(a<<3)+1176|0;c=x[284];a=1<<a;R:{if(!(c&a)){x[284]=a|c;a=b;break R}a=x[b+8>>2]}x[b+8>>2]=e;x[a+12>>2]=e;x[e+12>>2]=b;x[e+8>>2]=a;break Q}a=31;if(d>>>0<=16777215){a=d>>>8|0;g=a+1048320>>>16&8;a=a<<g;c=a+520192>>>16&4;a=a<<c;b=a+245760>>>16&2;a=(a<<b>>>15|0)-(b|(c|g))|0;a=(a<<1|d>>>a+21&1)+28|0}x[e+28>>2]=a;x[e+16>>2]=0;x[e+20>>2]=0;b=(a<<2)+1440|0;S:{c=1<<a;T:{if(!(c&j)){x[285]=c|j;x[b>>2]=e;break T}a=d<<((a|0)==31?0:25-(a>>>1|0)|0);h=x[b>>2];while(1){b=h;if((x[b+4>>2]&-8)==(d|0)){break S}c=a>>>29|0;a=a<<1;c=(c&4)+b|0;h=x[c+16>>2];if(h){continue}break}x[c+16>>2]=e}x[e+24>>2]=b;x[e+12>>2]=e;x[e+8>>2]=e;break Q}a=x[b+8>>2];x[a+12>>2]=e;x[b+8>>2]=e;x[e+24>>2]=0;x[e+12>>2]=b;x[e+8>>2]=a}a=f+8|0;break a}U:{if(!i){break U}c=x[b+28>>2];a=(c<<2)+1440|0;V:{if(x[a>>2]==(b|0)){x[a>>2]=f;if(f){break V}x[285]=pa(c)&j;break U}x[i+(x[i+16>>2]==(b|0)?16:20)>>2]=f;if(!f){break U}}x[f+24>>2]=i;a=x[b+16>>2];if(a){x[f+16>>2]=a;x[a+24>>2]=f}a=x[b+20>>2];if(!a){break U}x[f+20>>2]=a;x[a+24>>2]=f}W:{if(d>>>0<=15){a=d+h|0;x[b+4>>2]=a|3;a=a+b|0;x[a+4>>2]=x[a+4>>2]|1;break W}x[b+4>>2]=h|3;f=b+h|0;x[f+4>>2]=d|1;x[d+f>>2]=d;if(k){a=k>>>3|0;c=(a<<3)+1176|0;g=x[289];a=1<<a;X:{if(!(a&e)){x[284]=a|e;a=c;break X}a=x[c+8>>2]}x[c+8>>2]=g;x[a+12>>2]=g;x[g+12>>2]=c;x[g+8>>2]=a}x[289]=f;x[286]=d}a=b+8|0}S=l+16|0;return a|0}function _(a){a=a|0;var b=0,c=0,d=0,e=0,f=0,g=0,h=0;a:{if(!a){break a}d=a-8|0;b=x[a-4>>2];a=b&-8;f=d+a|0;b:{if(b&1){break b}if(!(b&3)){break a}b=x[d>>2];d=d-b|0;if(d>>>0<A[288]){break a}a=a+b|0;if(x[289]!=(d|0)){if(b>>>0<=255){e=x[d+8>>2];b=b>>>3|0;c=x[d+12>>2];if((c|0)==(e|0)){x[284]=x[284]&pa(b);break b}x[e+12>>2]=c;x[c+8>>2]=e;break b}h=x[d+24>>2];b=x[d+12>>2];c:{if((d|0)!=(b|0)){c=x[d+8>>2];x[c+12>>2]=b;x[b+8>>2]=c;break c}d:{e=d+20|0;c=x[e>>2];if(c){break d}e=d+16|0;c=x[e>>2];if(c){break d}b=0;break c}while(1){g=e;b=c;e=b+20|0;c=x[e>>2];if(c){continue}e=b+16|0;c=x[b+16>>2];if(c){continue}break}x[g>>2]=0}if(!h){break b}e=x[d+28>>2];c=(e<<2)+1440|0;e:{if(x[c>>2]==(d|0)){x[c>>2]=b;if(b){break e}x[285]=x[285]&pa(e);break b}x[h+(x[h+16>>2]==(d|0)?16:20)>>2]=b;if(!b){break b}}x[b+24>>2]=h;c=x[d+16>>2];if(c){x[b+16>>2]=c;x[c+24>>2]=b}c=x[d+20>>2];if(!c){break b}x[b+20>>2]=c;x[c+24>>2]=b;break b}b=x[f+4>>2];if((b&3)!=3){break b}x[286]=a;x[f+4>>2]=b&-2;x[d+4>>2]=a|1;x[a+d>>2]=a;return}if(d>>>0>=f>>>0){break a}b=x[f+4>>2];if(!(b&1)){break a}f:{if(!(b&2)){if(x[290]==(f|0)){x[290]=d;a=x[287]+a|0;x[287]=a;x[d+4>>2]=a|1;if(x[289]!=(d|0)){break a}x[286]=0;x[289]=0;return}if(x[289]==(f|0)){x[289]=d;a=x[286]+a|0;x[286]=a;x[d+4>>2]=a|1;x[a+d>>2]=a;return}a=(b&-8)+a|0;g:{if(b>>>0<=255){e=x[f+8>>2];b=b>>>3|0;c=x[f+12>>2];if((c|0)==(e|0)){x[284]=x[284]&pa(b);break g}x[e+12>>2]=c;x[c+8>>2]=e;break g}h=x[f+24>>2];b=x[f+12>>2];h:{if((f|0)!=(b|0)){c=x[f+8>>2];x[c+12>>2]=b;x[b+8>>2]=c;break h}i:{e=f+20|0;c=x[e>>2];if(c){break i}e=f+16|0;c=x[e>>2];if(c){break i}b=0;break h}while(1){g=e;b=c;e=b+20|0;c=x[e>>2];if(c){continue}e=b+16|0;c=x[b+16>>2];if(c){continue}break}x[g>>2]=0}if(!h){break g}e=x[f+28>>2];c=(e<<2)+1440|0;j:{if(x[c>>2]==(f|0)){x[c>>2]=b;if(b){break j}x[285]=x[285]&pa(e);break g}x[h+(x[h+16>>2]==(f|0)?16:20)>>2]=b;if(!b){break g}}x[b+24>>2]=h;c=x[f+16>>2];if(c){x[b+16>>2]=c;x[c+24>>2]=b}c=x[f+20>>2];if(!c){break g}x[b+20>>2]=c;x[c+24>>2]=b}x[d+4>>2]=a|1;x[a+d>>2]=a;if(x[289]!=(d|0)){break f}x[286]=a;return}x[f+4>>2]=b&-2;x[d+4>>2]=a|1;x[a+d>>2]=a}if(a>>>0<=255){a=a>>>3|0;b=(a<<3)+1176|0;c=x[284];a=1<<a;k:{if(!(c&a)){x[284]=a|c;a=b;break k}a=x[b+8>>2]}x[b+8>>2]=d;x[a+12>>2]=d;x[d+12>>2]=b;x[d+8>>2]=a;return}e=31;x[d+16>>2]=0;x[d+20>>2]=0;if(a>>>0<=16777215){b=a>>>8|0;g=b+1048320>>>16&8;b=b<<g;e=b+520192>>>16&4;b=b<<e;c=b+245760>>>16&2;b=(b<<c>>>15|0)-(c|(e|g))|0;e=(b<<1|a>>>b+21&1)+28|0}x[d+28>>2]=e;g=(e<<2)+1440|0;l:{m:{c=x[285];b=1<<e;n:{if(!(c&b)){x[285]=b|c;x[g>>2]=d;x[d+24>>2]=g;break n}e=a<<((e|0)==31?0:25-(e>>>1|0)|0);b=x[g>>2];while(1){c=b;if((x[b+4>>2]&-8)==(a|0)){break m}b=e>>>29|0;e=e<<1;g=c+(b&4)|0;b=x[g+16>>2];if(b){continue}break}x[g+16>>2]=d;x[d+24>>2]=c}x[d+12>>2]=d;x[d+8>>2]=d;break l}a=x[c+8>>2];x[a+12>>2]=d;x[c+8>>2]=d;x[d+24>>2]=0;x[d+12>>2]=c;x[d+8>>2]=a}a=x[292]-1|0;x[292]=a?a:-1}}function oa(a){a=a|0;var b=0,c=0,d=0,e=0;c=S-16|0;S=c;x[c+12>>2]=a;a=S-16|0;S=a;x[a+12>>2]=x[c+12>>2];b=S-16|0;d=x[a+12>>2];B[b+12>>2]=B[d>>2];x[b+8>>2]=x[b+12>>2];if((x[b+8>>2]&2139095040)!=2139095040){b=S-16|0;B[b+12>>2]=B[d+4>>2];x[b+8>>2]=x[b+12>>2];e=(x[b+8>>2]&2139095040)!=2139095040}S=a+16|0;S=c+16|0;return e|0}function da(a){a=a|0;var b=0,c=0;c=S-16|0;S=c;x[c+12>>2]=a;a=S-16|0;S=a;x[a+8>>2]=x[c+12>>2];b=x[a+8>>2];B[a+4>>2]=Z(b);a:{if(B[a+4>>2]<E(1.1920928955078125e-7)){B[a+12>>2]=0;break a}B[a>>2]=E(1)/B[a+4>>2];B[b>>2]=B[b>>2]*B[a>>2];B[b+4>>2]=B[b+4>>2]*B[a>>2];B[a+12>>2]=B[a+4>>2]}S=a+16|0;S=c+16|0;return E(B[a+12>>2])}function ga(a,b,c){a=a|0;b=E(b);c=E(c);var d=0,e=0;d=S-16|0;S=d;x[d+12>>2]=a;B[d+8>>2]=b;B[d+4>>2]=c;b=B[d+8>>2];c=B[d+4>>2];a=S-16|0;x[a+12>>2]=x[d+12>>2];B[a+8>>2]=b;B[a+4>>2]=c;e=x[a+12>>2];B[e>>2]=B[a+8>>2];B[e+4>>2]=B[a+4>>2];S=d+16|0}function ia(a,b){a=E(a);b=E(b);var c=0,d=0,e=0,f=0;c=S-16|0;S=c;B[c+12>>2]=a;B[c+8>>2]=b;e=aa();a=B[c+12>>2];b=B[c+8>>2];d=S-16|0;x[d+12>>2]=e;B[d+8>>2]=a;B[d+4>>2]=b;f=x[d+12>>2];B[f>>2]=B[d+8>>2];B[f+4>>2]=B[d+4>>2];S=c+16|0;return e|0}function Y(a){var b=0,c=0;b=x[256];c=a+3&-4;a=b+c|0;a:{if(a>>>0<=b>>>0?c:0){break a}if(a>>>0>U()<<16>>>0){if(!(Q(a|0)|0)){break a}}x[256]=a;return b}x[283]=48;return-1}function ea(a){a=a|0;var b=0,c=E(0);b=S-16|0;S=b;x[b+12>>2]=a;a=S-16|0;x[a+12>>2]=x[b+12>>2];a=x[a+12>>2];c=B[a+4>>2];S=b+16|0;return E(E(E(B[a>>2]*B[a>>2])+E(c*c)))}function Z(a){var b=0,c=E(0),d=0;b=S-16|0;S=b;x[b+12>>2]=a;a=x[b+12>>2];c=B[a+4>>2];d=S-16|0;B[d+12>>2]=E(B[a>>2]*B[a>>2])+E(c*c);S=b+16|0;return E(M(B[d+12>>2]))}function ha(a){a=a|0;var b=0;b=S-16|0;S=b;x[b+12>>2]=a;a=S-16|0;x[a+12>>2]=x[b+12>>2];a=x[a+12>>2];B[a>>2]=0;B[a+4>>2]=0;S=b+16|0}function aa(){var a=0;a:{while(1){a=$(8);if(a){break a}a=x[282];if(a){T[a|0]();continue}break}R();N()}return a}function ka(a,b){a=a|0;b=E(b);var c=0;c=S-16|0;x[c+12>>2]=a;B[c+8>>2]=b;B[x[c+12>>2]+4>>2]=B[c+8>>2]}function ma(a,b){a=a|0;b=E(b);var c=0;c=S-16|0;x[c+12>>2]=a;B[c+8>>2]=b;B[x[c+12>>2]>>2]=B[c+8>>2]}function fa(a){a=a|0;var b=0,c=E(0);b=S-16|0;S=b;x[b+12>>2]=a;c=Z(x[b+12>>2]);S=b+16|0;return E(c)}function ba(a){a=a|0;var b=0;b=S-16|0;S=b;x[b+12>>2]=a;a=x[b+12>>2];if(a){_(a)}S=b+16|0}function la(a){a=a|0;var b=0;b=S-16|0;x[b+12>>2]=a;return E(B[x[b+12>>2]+4>>2])}function na(a){a=a|0;var b=0;b=S-16|0;x[b+12>>2]=a;return E(B[x[b+12>>2]>>2])}function pa(a){var b=0;b=a&31;a=0-a&31;return(-1>>>b&-2)<<b|(-1<<a&-2)>>>a}
function ja(){var a=0;a=aa();x[(S-16|0)+12>>2]=a;return a|0}function ca(){}
// EMSCRIPTEN_END_FUNCS
g=y;r(W);var T=e([]);function U(){return u.byteLength/65536|0}return{"d":ca,"e":ba,"f":ja,"g":ia,"h":ha,"i":ga,"j":fa,"k":ea,"l":da,"m":oa,"n":na,"o":ma,"p":la,"q":ka,"r":ba,"s":T,"t":$,"u":_}}return V(X)}
// EMSCRIPTEN_END_ASM




)(qa)}function ra(){return{then:function(a){a({instance:new pa})}}}var sa=Error,WebAssembly={};E=[];
"object"!==typeof WebAssembly&&C("no native wasm support detected");var F,ta=!1,ua="undefined"!==typeof TextDecoder?new TextDecoder("utf8"):void 0,G,I,oa=b.INITIAL_MEMORY||67108864;b.wasmMemory?F=b.wasmMemory:F=new na;F&&(G=F.buffer);oa=G.byteLength;var J=G;G=J;b.HEAP8=new Int8Array(J);b.HEAP16=new Int16Array(J);b.HEAP32=new Int32Array(J);b.HEAPU8=I=new Uint8Array(J);b.HEAPU16=new Uint16Array(J);b.HEAPU32=new Uint32Array(J);b.HEAPF32=new Float32Array(J);b.HEAPF64=new Float64Array(J);
var va,wa=[],xa=[],ya=[];function za(){var a=b.preRun.shift();wa.unshift(a)}Math.imul||(Math.imul=function(a,c){var d=a&65535,f=c&65535;return d*f+((a>>>16)*f+d*(c>>>16)<<16)|0});if(!Math.fround){var Aa=new Float32Array(1);Math.fround=function(a){Aa[0]=a;return Aa[0]}}Math.clz32||(Math.clz32=function(a){var c=32,d=a>>16;d&&(c-=16,a=d);if(d=a>>8)c-=8,a=d;if(d=a>>4)c-=4,a=d;if(d=a>>2)c-=2,a=d;return a>>1?c-2:c-a});Math.trunc||(Math.trunc=function(a){return 0>a?Math.ceil(a):Math.floor(a)});
var K=0,L=null,M=null;b.preloadedImages={};b.preloadedAudios={};function C(a){if(b.onAbort)b.onAbort(a);a="Aborted("+a+")";D(a);ta=!0;a=new sa(a+". Build with -s ASSERTIONS=1 for more info.");k(a);throw a;}var N="data:application/octet-stream;base64,",O;O="<<< WASM_BINARY_FILE >>>";if(!O.startsWith(N)){var Ba=O;O=b.locateFile?b.locateFile(Ba,r):r+Ba}
function Ca(){var a=O;try{if(a==O&&E)return new Uint8Array(E);var c=B(a);if(c)return c;if(w)return w(a);throw"both async and sync fetching of the wasm failed";}catch(d){C(d)}}
function Da(){if(!E&&(la||p)){if("function"===typeof fetch&&!O.startsWith("file://"))return fetch(O,{credentials:"same-origin"}).then(function(a){if(!a.ok)throw"failed to load wasm binary file at '"+O+"'";return a.arrayBuffer()}).catch(function(){return Ca()});if(v)return new Promise(function(a,c){v(O,function(d){a(new Uint8Array(d))},c)})}return Promise.resolve().then(function(){return Ca()})}
function P(a){for(;0<a.length;){var c=a.shift();if("function"==typeof c)c(b);else{var d=c.R;"number"===typeof d?void 0===c.G?Ea(d)():Ea(d)(c.G):d(void 0===c.G?null:c.G)}}}var S=[];function Ea(a){var c=S[a];c||(a>=S.length&&(S.length=a+1),S[a]=c=va.get(a));return c}
var ma=!1,Fa="function"===typeof atob?atob:function(a){var c="",d=0;a=a.replace(/[^A-Za-z0-9\+\/=]/g,"");do{var f="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".indexOf(a.charAt(d++));var h="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".indexOf(a.charAt(d++));var m="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".indexOf(a.charAt(d++));var y="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".indexOf(a.charAt(d++));f=f<<
2|h>>4;h=(h&15)<<4|m>>2;var Q=(m&3)<<6|y;c+=String.fromCharCode(f);64!==m&&(c+=String.fromCharCode(h));64!==y&&(c+=String.fromCharCode(Q))}while(d<a.length);return c};
function B(a){if(a.startsWith(N)){a=a.slice(N.length);if("boolean"===typeof q&&q){var c=Buffer.from(a,"base64");c=new Uint8Array(c.buffer,c.byteOffset,c.byteLength)}else try{var d=Fa(a),f=new Uint8Array(d.length);for(a=0;a<d.length;++a)f[a]=d.charCodeAt(a);c=f}catch(h){throw Error("Converting base64 string to bytes failed.");}return c}}var qa={c:function(){C("")},b:function(){C("OOM")},a:F};
(function(){function a(h){b.asm=h.exports;va=b.asm.s;xa.unshift(b.asm.d);K--;b.monitorRunDependencies&&b.monitorRunDependencies(K);0==K&&(null!==L&&(clearInterval(L),L=null),M&&(h=M,M=null,h()))}function c(h){a(h.instance)}function d(h){return Da().then(function(){return ra()}).then(function(m){return m}).then(h,function(m){D("failed to asynchronously prepare wasm: "+m);C(m)})}var f={a:qa};K++;b.monitorRunDependencies&&b.monitorRunDependencies(K);if(b.instantiateWasm)try{return b.instantiateWasm(f,
a)}catch(h){return D("Module.instantiateWasm callback failed with error: "+h),!1}(function(){return E||"function"!==typeof WebAssembly.instantiateStreaming||O.startsWith(N)||O.startsWith("file://")||"function"!==typeof fetch?d(c):fetch(O,{credentials:"same-origin"}).then(function(h){return WebAssembly.instantiateStreaming(h,f).then(c,function(m){D("wasm streaming compile failed: "+m);D("falling back to ArrayBuffer instantiation");return d(c)})})})().catch(k);return{}})();
b.___wasm_call_ctors=function(){return(b.___wasm_call_ctors=b.asm.d).apply(null,arguments)};
var Ga=b._emscripten_bind_VoidPtr___destroy___0=function(){return(Ga=b._emscripten_bind_VoidPtr___destroy___0=b.asm.e).apply(null,arguments)},Ha=b._emscripten_bind_b2Vec2_b2Vec2_0=function(){return(Ha=b._emscripten_bind_b2Vec2_b2Vec2_0=b.asm.f).apply(null,arguments)},Ia=b._emscripten_bind_b2Vec2_b2Vec2_2=function(){return(Ia=b._emscripten_bind_b2Vec2_b2Vec2_2=b.asm.g).apply(null,arguments)},Ja=b._emscripten_bind_b2Vec2_SetZero_0=function(){return(Ja=b._emscripten_bind_b2Vec2_SetZero_0=b.asm.h).apply(null,
arguments)},Ka=b._emscripten_bind_b2Vec2_Set_2=function(){return(Ka=b._emscripten_bind_b2Vec2_Set_2=b.asm.i).apply(null,arguments)},La=b._emscripten_bind_b2Vec2_Length_0=function(){return(La=b._emscripten_bind_b2Vec2_Length_0=b.asm.j).apply(null,arguments)},Na=b._emscripten_bind_b2Vec2_LengthSquared_0=function(){return(Na=b._emscripten_bind_b2Vec2_LengthSquared_0=b.asm.k).apply(null,arguments)},Oa=b._emscripten_bind_b2Vec2_Normalize_0=function(){return(Oa=b._emscripten_bind_b2Vec2_Normalize_0=b.asm.l).apply(null,
arguments)},Pa=b._emscripten_bind_b2Vec2_IsValid_0=function(){return(Pa=b._emscripten_bind_b2Vec2_IsValid_0=b.asm.m).apply(null,arguments)},Qa=b._emscripten_bind_b2Vec2_get_x_0=function(){return(Qa=b._emscripten_bind_b2Vec2_get_x_0=b.asm.n).apply(null,arguments)},Ra=b._emscripten_bind_b2Vec2_set_x_1=function(){return(Ra=b._emscripten_bind_b2Vec2_set_x_1=b.asm.o).apply(null,arguments)},Sa=b._emscripten_bind_b2Vec2_get_y_0=function(){return(Sa=b._emscripten_bind_b2Vec2_get_y_0=b.asm.p).apply(null,arguments)},
Ta=b._emscripten_bind_b2Vec2_set_y_1=function(){return(Ta=b._emscripten_bind_b2Vec2_set_y_1=b.asm.q).apply(null,arguments)},Ua=b._emscripten_bind_b2Vec2___destroy___0=function(){return(Ua=b._emscripten_bind_b2Vec2___destroy___0=b.asm.r).apply(null,arguments)};b._malloc=function(){return(b._malloc=b.asm.t).apply(null,arguments)};b._free=function(){return(b._free=b.asm.u).apply(null,arguments)};
b.UTF8ToString=function(a,c){if(a){var d=a+c;for(c=a;I[c]&&!(c>=d);)++c;if(16<c-a&&I.subarray&&ua)a=ua.decode(I.subarray(a,c));else{for(d="";a<c;){var f=I[a++];if(f&128){var h=I[a++]&63;if(192==(f&224))d+=String.fromCharCode((f&31)<<6|h);else{var m=I[a++]&63;f=224==(f&240)?(f&15)<<12|h<<6|m:(f&7)<<18|h<<12|m<<6|I[a++]&63;65536>f?d+=String.fromCharCode(f):(f-=65536,d+=String.fromCharCode(55296|f>>10,56320|f&1023))}}else d+=String.fromCharCode(f)}a=d}}else a="";return a};var T;
M=function Va(){T||U();T||(M=Va)};
function U(){function a(){if(!T&&(T=!0,b.calledRun=!0,!ta)){P(xa);ja(b);if(b.onRuntimeInitialized)b.onRuntimeInitialized();if(b.postRun)for("function"==typeof b.postRun&&(b.postRun=[b.postRun]);b.postRun.length;){var c=b.postRun.shift();ya.unshift(c)}P(ya)}}if(!(0<K)){if(b.preRun)for("function"==typeof b.preRun&&(b.preRun=[b.preRun]);b.preRun.length;)za();P(wa);0<K||(b.setStatus?(b.setStatus("Running..."),setTimeout(function(){setTimeout(function(){b.setStatus("")},1);a()},1)):a())}}b.run=U;
if(b.preInit)for("function"==typeof b.preInit&&(b.preInit=[b.preInit]);0<b.preInit.length;)b.preInit.pop()();U();function V(){}V.prototype=Object.create(V.prototype);V.prototype.constructor=V;V.prototype.D=V;V.F={};b.WrapperObject=V;function W(a){return(a||V).F}b.getCache=W;function X(a,c){var d=W(c),f=d[a];if(f)return f;f=Object.create((c||V).prototype);f.v=a;return d[a]=f}b.wrapPointer=X;b.castObject=function(a,c){return X(a.v,c)};b.NULL=X(0);
b.destroy=function(a){if(!a.__destroy__)throw"Error: Cannot destroy object. (Did you create it yourself?)";a.__destroy__();delete W(a.D)[a.v]};b.compare=function(a,c){return a.v===c.v};b.getPointer=function(a){return a.v};b.getClass=function(a){return a.D};function Y(){throw"cannot construct a VoidPtr, no constructor in IDL";}Y.prototype=Object.create(V.prototype);Y.prototype.constructor=Y;Y.prototype.D=Y;Y.F={};b.VoidPtr=Y;Y.prototype.__destroy__=function(){Ga(this.v)};
function Z(a,c){a&&"object"===typeof a&&(a=a.v);c&&"object"===typeof c&&(c=c.v);this.v=void 0===a?Ha():void 0===c?_emscripten_bind_b2Vec2_b2Vec2_1(a):Ia(a,c);W(Z)[this.v]=this}Z.prototype=Object.create(V.prototype);Z.prototype.constructor=Z;Z.prototype.D=Z;Z.F={};b.b2Vec2=Z;Z.prototype.SetZero=function(){Ja(this.v)};Z.prototype.Set=Z.prototype.Set=function(a,c){var d=this.v;a&&"object"===typeof a&&(a=a.v);c&&"object"===typeof c&&(c=c.v);Ka(d,a,c)};Z.prototype.Length=function(){return La(this.v)};
Z.prototype.LengthSquared=function(){return Na(this.v)};Z.prototype.Normalize=function(){return Oa(this.v)};Z.prototype.IsValid=function(){return!!Pa(this.v)};Z.prototype.get_x=Z.prototype.K=function(){return Qa(this.v)};Z.prototype.set_x=Z.prototype.O=function(a){var c=this.v;a&&"object"===typeof a&&(a=a.v);Ra(c,a)};Object.defineProperty(Z.prototype,"x",{get:Z.prototype.K,set:Z.prototype.O});Z.prototype.get_y=Z.prototype.L=function(){return Sa(this.v)};
Z.prototype.set_y=Z.prototype.P=function(a){var c=this.v;a&&"object"===typeof a&&(a=a.v);Ta(c,a)};Object.defineProperty(Z.prototype,"y",{get:Z.prototype.L,set:Z.prototype.P});Z.prototype.__destroy__=function(){Ua(this.v)};


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
