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

package com.badlogic.gdx.tests.dragome;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import com.badlogic.gdx.backends.dragome.js.typedarrays.ArrayBuffer;
import com.badlogic.gdx.backends.dragome.js.typedarrays.ArrayBufferView;
import com.badlogic.gdx.backends.dragome.js.typedarrays.Float32Array;
import com.badlogic.gdx.backends.dragome.js.typedarrays.Float64Array;
import com.badlogic.gdx.backends.dragome.js.typedarrays.Int16Array;
import com.badlogic.gdx.backends.dragome.js.typedarrays.Int32Array;
import com.badlogic.gdx.backends.dragome.js.typedarrays.Int8Array;
import com.badlogic.gdx.backends.dragome.js.typedarrays.Uint16Array;
import com.badlogic.gdx.backends.dragome.js.typedarrays.Uint32Array;
import com.badlogic.gdx.backends.dragome.js.typedarrays.Uint8Array;
import com.badlogic.gdx.backends.dragome.js.typedarrays.Uint8ClampedArray;
import com.badlogic.gdx.backends.dragome.js.webgl.WebGLActiveInfo;
import com.badlogic.gdx.backends.dragome.js.webgl.WebGLBuffer;
import com.badlogic.gdx.backends.dragome.js.webgl.WebGLContextAttributes;
import com.badlogic.gdx.backends.dragome.js.webgl.WebGLFramebuffer;
import com.badlogic.gdx.backends.dragome.js.webgl.WebGLObject;
import com.badlogic.gdx.backends.dragome.js.webgl.WebGLProgram;
import com.badlogic.gdx.backends.dragome.js.webgl.WebGLRenderbuffer;
import com.badlogic.gdx.backends.dragome.js.webgl.WebGLRenderingContext;
import com.badlogic.gdx.backends.dragome.js.webgl.WebGLShader;
import com.badlogic.gdx.backends.dragome.js.webgl.WebGLTexture;
import com.badlogic.gdx.backends.dragome.js.webgl.WebGLUniformLocation;
import com.dragome.callbackevictor.CallbackEvictorConfigurator;
import com.dragome.commons.ChainedInstrumentationDragomeConfigurator;
import com.dragome.commons.DragomeConfiguratorImplementor;
import com.dragome.commons.ExecutionHandler;
import com.dragome.commons.compiler.annotations.CompilerType;
import com.dragome.methodlogger.MethodLoggerConfigurator;
import com.dragome.web.enhancers.jsdelegate.DefaultDelegateStrategy;
import com.dragome.web.enhancers.jsdelegate.JsDelegateGenerator;
import com.dragome.web.helpers.serverside.DefaultClasspathFilter;
import com.dragome.web.html.dom.html5canvas.interfaces.CanvasImageSource;
import com.dragome.web.html.dom.html5canvas.interfaces.CanvasRenderingContext2D;
import com.dragome.web.html.dom.html5canvas.interfaces.HTMLCanvasElement;
import com.dragome.web.html.dom.html5canvas.interfaces.ImageElement;

import javassist.CtMethod;
import javassist.NotFoundException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.events.Event;

import com.dragome.helpers.Utils;

/** @author xpenatan */
@DragomeConfiguratorImplementor
public class DragomeConfiguration extends ChainedInstrumentationDragomeConfigurator {
	private CallbackEvictorConfigurator callbackEvictorConfigurator;
	private MethodLoggerConfigurator methodLoggerConfigurator;
	HashSet<String> paths;
	String projName;
	List<File> result;
	private JsDelegateGenerator jsDelegateGenerator;

	public DragomeConfiguration () {

// System.setProperty("dragome-compile-mode", CompilerMode.Production.toString());
// System.setProperty("dragome-compile-mode", CompilerMode.Debug.toString());

		result = new ArrayList<File>();
		String projPath = System.getProperty("user.dir");
		File file = new File(projPath);
		projName = file.getName();
		paths = new HashSet<String>();
		setClasspathFilter(new DefaultClasspathFilter() {
			public boolean accept (File pathname) {
				boolean accept = super.accept(pathname);
				String absolutePath = pathname.getAbsolutePath();

				String className = pathname.getName().replace(".class", "");
				if (paths.contains(className)) return false;

				if (absolutePath.contains("gdx-backend-dragome")) {
					paths.add(className);
				}
				return accept;
			}

		});

		callbackEvictorConfigurator = new CallbackEvictorConfigurator();
		callbackEvictorConfigurator.setEnabled(true);

		methodLoggerConfigurator = new MethodLoggerConfigurator();
		methodLoggerConfigurator.setEnabled(true);

		this.setClasspathFilter(new DefaultClasspathFilter() {
			
			@Override
			public boolean accept (File pathname) {
				boolean accept = super.accept(pathname);
				
				String absolutePath = pathname.getAbsolutePath();
				
				if(absolutePath.contains("utils\\Json"))
					return false;
				
//				System.out.println("absolutePath: " + absolutePath);
				return accept;
			}
		});
		
		init(callbackEvictorConfigurator, methodLoggerConfigurator);
		


	}

	public void add (Class<?> clazz) {
		byte[] bytecode = jsDelegateGenerator.generate(clazz);
		addClassBytecode(bytecode, JsDelegateGenerator.createDelegateClassName(clazz.getName()));
	}

	private void createJsDelegateGenerator (String classpath) {
		jsDelegateGenerator = new JsDelegateGenerator(Utils.createTempDir("jsdelegate"), classpath.replace(";", ":"),
			new DefaultDelegateStrategy() {
				@Override
				public String createMethodCall (CtMethod method, StringBuffer code, String params) throws NotFoundException {
					if(params == null)
						params = "";
					String longName = method.getLongName();
					String name = method.getName();
					if (longName.contains("Int32Array.set(int,int)") || longName.contains("Int16Array.set(int,int)")
						|| longName.contains("Int8Array.set(int,int)") || longName.contains("Uint8ClampedArray.set(int,int)")
						|| longName.contains("Uint32Array.set(int,int)") || longName.contains("Uint16Array.set(int,int)")
						|| longName.contains("Uint8Array.set(int,int)") || longName.contains("Float32Array.set(int,float)")
						|| longName.contains("Float64Array.set(int,float)"))
						return "this.node[$1] = $2;";
					else if (longName.contains("Int32Array.get(int)") || longName.contains("Int16Array.get(int)")
						|| longName.contains("Int8Array.get(int)") || longName.contains("Uint8ClampedArray.get(int)")
						|| longName.contains("Uint32Array.get(int)") || longName.contains("Uint16Array.get(int)")
						|| longName.contains("Uint8Array.get(int)") || longName.contains("Float32Array.get(int)")
						|| longName.contains("Float64Array.get(int)"))
						return "this.node[$1];";
					else{
						return super.createMethodCall(method, code, params);
					}
//					else if(name.startsWith("get"))
//					{
//						String replaceName = name.replace("get", "").toLowerCase();
//						
//						if(replaceName.length() == 0)
//							throw new UnsupportedOperationException("Method cannot match a generic implementation. Method:  " + longName);
//						
//						String func1 = "this.node." + name;
//						String func2 = "this.node." + replaceName;
//						String exception = "dragomeJs.createException('java.lang.UnsupportedOperationException', Method cannot match a generic implementation'');";
//						
//						
//						String cond1 ="if(typeof(" + func1 + ") === typeof(Function)){ return "+ func1 +"("+ params +")}";
//						String cond2 = " else { return " + func2  + "}";
//						String code3 = "else {"+ exception + "}";
//						String finalCond = cond1 + cond2;
//						return finalCond;
//					}
//					else if(name.startsWith("set"))
//					{
//						String replaceName = name.replace("set", "").toLowerCase();
//						
//						String func1 = "this.node." + name;
//						String func2 = "this.node." + replaceName;
//						
//						String exception = "dragomeJs.createException('java.lang.UnsupportedOperationException', Method cannot match a generic implementation'');";
//						
//						String cond1 ="if(typeof(" + func1 + ") === typeof(Function)){ return "+ func1 +"("+ params +")}";
//						String cond2 = "";
//						String code3 = "else {"+ exception + "}";
//						if(replaceName.length() == 1)
//						{
//							
//						}
//						cond2 = " else { return " + func2  + " = " + params +  "}";
//						String finalCond = cond1 + cond2 + code3;
//						return finalCond;
//					}
//					return null;
				}
			});

		result.add(jsDelegateGenerator.getBaseDir());
	}

	public List<File> getExtraClasspath (String classpath) {
		if (jsDelegateGenerator == null) createJsDelegateGenerator(classpath);

		add(Document.class);
		add(Element.class);
		add(Attr.class);
		add(NodeList.class);
		add(Node.class);
		add(NamedNodeMap.class);
		add(Text.class);
		add(HTMLCanvasElement.class);
		add(CanvasRenderingContext2D.class);
		add(CanvasImageSource.class);
		add(ImageElement.class);
		add(Event.class);

		add(WebGLActiveInfo.class);
		add(WebGLBuffer.class);
		add(WebGLContextAttributes.class);
		add(WebGLFramebuffer.class);
		add(WebGLObject.class);
		add(WebGLProgram.class);
		add(WebGLRenderbuffer.class);
		add(WebGLRenderingContext.class);
		add(WebGLShader.class);
		add(WebGLTexture.class);
		add(WebGLUniformLocation.class);

		add(ArrayBuffer.class);
		add(ArrayBufferView.class);

// add(DataView.class);
// add(DataViewStream.class);
		add(Float32Array.class);
		add(Float64Array.class);
		add(Int16Array.class);
		add(Int32Array.class);
		add(Int8Array.class);
		add(Uint16Array.class);
		add(Uint32Array.class);
		add(Uint8Array.class);
		add(Uint8ClampedArray.class);

// add(Storage.class);
		return result;
	}

	@Override
	public boolean filterClassPath (String aClassPathEntry) {

		boolean flag = super.filterClassPath(aClassPathEntry);

		if (aClassPathEntry.contains(projName)) flag = true;
		if (aClassPathEntry.contains("gdx-") || aClassPathEntry.contains("gdx\\bin")) flag = true;
		if (aClassPathEntry.contains("gdx-") || aClassPathEntry.contains("gdx\\bin")) flag = true;
		if (aClassPathEntry.contains("dragome.jar") || aClassPathEntry.contains("gdx-backend-dragome\\bin")) flag = true;
		if (aClassPathEntry.contains("dragome-js-commons")) flag = true;
		if (aClassPathEntry.contains("dragome-js-jre")) flag = true;
		if (aClassPathEntry.contains("dragome-callback-evictor")) flag = true;
		if (aClassPathEntry.contains("dragome-form-bindings")) flag = true;
		if (aClassPathEntry.contains("dragome-core")) flag = true;
		if (aClassPathEntry.contains("dragome-method-logger")) flag = true;
		if (aClassPathEntry.contains("dragome-web")) flag = true;
		if (aClassPathEntry.contains("dragome-guia-web")) flag = true;
		if (aClassPathEntry.contains("dragome-guia")) flag = true;

// System.out.println("flag: " + flag + " path: " + aClassPathEntry);
		return flag;
	}

	public ExecutionHandler getExecutionHandler () {
		return callbackEvictorConfigurator.getExecutionHandler();
	}

	public CompilerType getDefaultCompilerType () {
		return CompilerType.Standard;
	}

	@Override
	public boolean isCheckingCast () {
		return true;
	}
}
