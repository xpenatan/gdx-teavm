/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 * This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Public License v3.0 which accompanies
 * this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

function stylesheet(url) {
    var s = document.createElement('link');
    s.type = 'text/css';
    s.rel = "stylesheet";
    s.async = false;
    s.href = url;
    var x = document.getElementsByTagName('head')[0];
    x.appendChild(s);
}

function script(url, defer) {
    var s = document.createElement('script');
    s.type = 'text/javascript';
    s.async = false;
    s.src = url;
    if (defer)
        s.defer = "defer";
    var x = document.getElementsByTagName('head')[0];
    x.appendChild(s);
}

function loadScript(url, callback) {
    var script = document.createElement("script")
    script.type = "text/javascript";

    if (script.readyState) { // IE
        script.onreadystatechange = function () {
            if (script.readyState == "loaded"
                    || script.readyState == "complete") {
                script.onreadystatechange = null;
                callback();
            }
        };
    } else { // Others
        script.onload = function () {
            callback();
        };
    }

    script.src = url;
    document.getElementsByTagName("head")[0].appendChild(script);
}

function Uint8ToString(u8a) {
    var CHUNK_SZ = 0x8000;
    var c = [];
    for (var i = 0; i < u8a.length; i += CHUNK_SZ) {
        c.push(String.fromCharCode.apply(null, u8a.subarray(i, i + CHUNK_SZ)));
    }
    return c.join("");
}

(function () {
		stylesheet("dragome-resources/css/dragome.css");
		script("dragome-resources/js/hashtable.js");
		script("dragome-resources/js/deflate.js");
		script("dragome-resources/js/helpers.js");
		script("dragome-resources/js/string.js");

		loadScript("dragome-resources/js/deflate-main.js", function() {
			loadScript("dragome-resources/js/qx-oo-5.0.1.min.js", function() {
				var ajax = new XMLHttpRequest();
				ajax.open("GET", "compiled-js/webapp-1.js", true);
				ajax.responseType = "arraybuffer";
				ajax.onload = function() {
					var code= Uint8ToString(deflate.decompress(new Uint8Array(ajax.response)));
					var newScriptTag = document.createElement('script');
					newScriptTag.appendChild(document.createTextNode(code));
					document.body.appendChild(newScriptTag);
				};
				ajax.send();
			});
		});
})();
