/*******************************************************************************
 * Copyright 2016 Natan Guilherme.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.backends.dragome.js;

import org.w3c.dom.typedarray.ArrayBuffer;

import com.dragome.commons.compiler.annotations.MethodAlias;
import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.web.enhancers.jsdelegate.JsCast;

/** @author xpenatan */
public class XMLHttpRequest {
	Object instance;

	/** When constructed, the XMLHttpRequest object must be in the UNSENT state. */
	public static final int UNSENT = 0;

	/** The OPENED state is the state of the object when the open() method has been successfully invoked. During this state request
	 * headers can be set using setRequestHeader() and the request can be made using send(). */
	public static final int OPENED = 1;

	/** The HEADERS_RECEIVED state is the state of the object when all response headers have been received. */
	public static final int HEADERS_RECEIVED = 2;

	/** The LOADING state is the state of the object when the response entity body is being received. */
	public static final int LOADING = 3;

	/** The DONE state is the state of the object when either the data transfer has been completed or something went wrong during
	 * the transfer (infinite redirects for instance). */
	public static final int DONE = 4;

	public XMLHttpRequest (Object instance) {
		this.instance = instance;
	}

	public static XMLHttpRequest create () {
		return new XMLHttpRequest(ScriptHelper.eval("new XMLHttpRequest()", null));
	}

	public void setOnReadyStateChange (ReadyStateChangeHandler handler) {
		ScriptHelper.put("instance", instance, this);
		ScriptHelper.put("handler", handler, this);
		ScriptHelper.put("httpRequest", this, this);
		String code = "instance.onreadystatechange = function() {handler.onReadyStateChange(httpRequest);}";
		ScriptHelper.evalNoResult(code, this);
	}

	public int getReadyState () {
		ScriptHelper.put("instance", instance, this);
		return ScriptHelper.evalInt("instance.readyState", this);
	}

	public int getStatus () {
		ScriptHelper.put("instance", instance, this);
		return ScriptHelper.evalInt("instance.status", this);
	}

	/** Gets the response text.
	 * <p>
	 * See <a href="http://www.w3.org/TR/XMLHttpRequest/#the-responsetext-attribute" >http://www.w3.org/TR/XMLHttpRequest/#the-
	 * responsetext-attribute</a>.
	 *
	 * @return the response text */
	public String getResponseText () {
		ScriptHelper.put("instance", instance, this);
		return (String)ScriptHelper.eval("instance.responseText", this);
	}

	public ArrayBuffer getResponseArrayBuffer () {
		ScriptHelper.put("instance", instance, this);
		Object instance = ScriptHelper.eval("instance.response", this);
		return JsCast.castTo(instance, ArrayBuffer.class);
	}

	public String getResponseHeader (String header) {
		ScriptHelper.put("instance", instance, this);
		ScriptHelper.put("header", header, this);
		String instance = (String)ScriptHelper.eval("instance.getResponseHeader(header)", this);
		return instance;
	}

	/** Opens an asynchronous connection.
	 * <p>
	 * See <a href="http://www.w3.org/TR/XMLHttpRequest/#the-open-method" >http://
	 * www.w3.org/TR/XMLHttpRequest/#the-open-method</a>.
	 *
	 * @param httpMethod the HTTP method to use
	 * @param url the URL to be opened */
	public void open (String httpMethod, String url) {
		ScriptHelper.put("instance", instance, this);
		ScriptHelper.put("httpMethod", httpMethod, this);
		ScriptHelper.put("url", url, this);
		ScriptHelper.evalNoResult("instance.open(httpMethod, url, true)", this);
	}

	/** Opens an asynchronous connection.
	 * <p>
	 * See <a href="http://www.w3.org/TR/XMLHttpRequest/#the-open-method" >http://
	 * www.w3.org/TR/XMLHttpRequest/#the-open-method</a>.
	 *
	 * @param httpMethod the HTTP method to use
	 * @param url the URL to be opened
	 * @param user user to use in the URL */
	public void open (String httpMethod, String url, String user) {
		ScriptHelper.put("instance", instance, this);
		ScriptHelper.put("httpMethod", httpMethod, this);
		ScriptHelper.put("url", url, this);
		ScriptHelper.put("user", user, this);
		ScriptHelper.evalNoResult("instance.open(httpMethod, url, true, user)", this);
	}

	public void open (String httpMethod, String url, String user, String password) {
		ScriptHelper.put("instance", instance, this);
		ScriptHelper.put("httpMethod", httpMethod, this);
		ScriptHelper.put("url", url, this);
		ScriptHelper.put("user", user, this);
		ScriptHelper.put("password", password, this);
		ScriptHelper.evalNoResult("instance.open(httpMethod, url, true, user, password)", this);
	}

	/** Sets the response type.
	 * <p>
	 * See <a href="http://www.w3.org/TR/XMLHttpRequest/#the-responsetype-attribute" >http://www.w3.org/TR/XMLHttpRequest/#the-
	 * responsetype-attribute</a>
	 *
	 * @param responseType the type of response desired. See {@link ResponseType} for limitations on using the different values */
	public final void setResponseType (ResponseType responseType) {
		this.setResponseType(responseType.getResponseTypeString());
	}

	/** Sets the response type.
	 * <p>
	 * See <a href="http://www.w3.org/TR/XMLHttpRequest/#the-responsetype-attribute" >http://www.w3.org/TR/XMLHttpRequest/#the-
	 * responsetype-attribute</a>
	 *
	 * @param responseType the type of response desired. See {@link ResponseType} for limitations on using the different values */

	public void setResponseType (String responseType) {
		ScriptHelper.put("instance", instance, this);
		ScriptHelper.put("responseType", responseType, this);
		ScriptHelper.evalNoResult("instance.responseType = responseType", this);
	}

	public void setRequestHeader (String header, String value) {
		ScriptHelper.put("instance", instance, this);
		ScriptHelper.put("header", header, this);
		ScriptHelper.put("value", value, this);
		ScriptHelper.evalNoResult("instance.setRequestHeader(header, value)", this);
	}

	/** Initiates a request with no request data. This simply calls {@link #send(String)} with <code>null</code> as an argument,
	 * because the no-argument <code>send()</code> method is unavailable on Firefox. */
	public final void send () {
		send(null);
	}

	/** Initiates a request with data. If there is no data, specify null.
	 * <p>
	 * See <a href="http://www.w3.org/TR/XMLHttpRequest/#the-send-method" >http://
	 * www.w3.org/TR/XMLHttpRequest/#the-send-method</a>.
	 *
	 * @param requestData the data to be sent with the request */
	public void send (String requestData) {
		ScriptHelper.put("instance", instance, this);
		ScriptHelper.put("requestData", requestData, this);
		ScriptHelper.evalNoResult("instance.send(requestData)", this);
	}

	/** The type of response expected from the XHR. */
	public enum ResponseType {
		/** The default response type -- use {@link XMLHttpRequest#getResponseText()} for the return value. */
		Default(""),

		/** The default response type -- use {@link XMLHttpRequest#getResponseArrayBuffer()} for the return value. This value may
		 * only be used if {@link com.google.gwt.typedarrays.shared.TypedArrays#isSupported()} returns true. */
		ArrayBuffer("arraybuffer");

		private final String responseTypeString;

		private ResponseType (String responseTypeString) {
			this.responseTypeString = responseTypeString;
		}

		public String getResponseTypeString () {
			return responseTypeString;
		}
	}

	/** A ready-state callback for an {@link XMLHttpRequest} object. */
	public interface ReadyStateChangeHandler {

		/** This is called whenever the state of the XMLHttpRequest changes. See {@link XMLHttpRequest#setOnReadyStateChange}.
		 *
		 * @param xhr the object whose state has changed. */
		@MethodAlias(local_alias = "onReadyStateChange")
		void onReadyStateChange (XMLHttpRequest xhr);
	}
}
