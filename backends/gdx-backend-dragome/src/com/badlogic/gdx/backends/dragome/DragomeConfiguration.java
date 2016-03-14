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

package com.badlogic.gdx.backends.dragome;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.events.Event;

import com.badlogic.gdx.backends.dragome.js.storage.Storage;
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
import com.dragome.commons.ChainedInstrumentationDragomeConfigurator;
import com.dragome.commons.DragomeConfiguratorImplementor;
import com.dragome.commons.compiler.annotations.CompilerType;
import com.dragome.web.config.NodeSubTypeFactory;
import com.dragome.web.enhancers.jsdelegate.DefaultDelegateStrategy;
import com.dragome.web.enhancers.jsdelegate.JsDelegateGenerator;
import com.dragome.web.enhancers.jsdelegate.interfaces.SubTypeFactory;
import com.dragome.web.helpers.serverside.DefaultClasspathFilter;
import com.dragome.web.html.dom.html5canvas.interfaces.CanvasImageSource;
import com.dragome.web.html.dom.html5canvas.interfaces.CanvasRenderingContext2D;
import com.dragome.web.html.dom.html5canvas.interfaces.HTMLCanvasElement;
import com.dragome.web.html.dom.html5canvas.interfaces.ImageElement;

import javassist.CtMethod;
import javassist.NotFoundException;

/** @author xpenatan */
@DragomeConfiguratorImplementor(priority = 10)
public class DragomeConfiguration extends ChainedInstrumentationDragomeConfigurator {
	HashSet<String> paths;
	String projName;
	String projPath;
	List<File> result;
	private JsDelegateGenerator jsDelegateGenerator;

	boolean cache = false;
	
	public DragomeConfiguration () {

// System.setProperty("dragome-compile-mode", CompilerMode.Production.toString());
// System.setProperty("dragome-compile-mode", CompilerMode.Debug.toString());

		result = new ArrayList<File>();
		projPath = System.getProperty("user.dir");
		File file = new File(projPath);
		projName = file.getName();
		paths = new HashSet<String>();
		setClasspathFilter(new DefaultClasspathFilter() {
			public boolean accept (File pathname) {
				boolean accept = super.accept(pathname);
				String absolutePath = pathname.getAbsolutePath();

				if(absolutePath.contains("utils\\Json"))
					return false;

				String className = pathname.getName().replace(".class", "");
				if (paths.contains(className)) return false;

				if (absolutePath.contains("gdx-backend-dragome")) {
					paths.add(className);
				}
				
				
				System.out.println("ClassPathFilter: " + accept + " - " + absolutePath);
				
				return accept;
			}

		});

	}

	public void add (Class<?> clazz) {
		byte[] bytecode = jsDelegateGenerator.generate(clazz);
		addClassBytecode(bytecode, JsDelegateGenerator.createDelegateClassName(clazz.getName()));
	}

	private void createJsDelegateGenerator (String classpath) {
		
		if(cache == false) {
			File file2 = new File(projPath + "\\target\\dragome.cache");
			if (file2.exists()) 
				file2.delete();
		}
		File file1 = new File(projPath + "\\target\\jsdelegate");
		try {

			if (file1.exists()) 
				FileUtils.deleteDirectory(file1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		jsDelegateGenerator = new JsDelegateGenerator(file1, classpath.replace(";", ":"),
			new DefaultDelegateStrategy() {
				@Override
				public String createMethodCall (CtMethod method, StringBuffer code, String params) throws NotFoundException {
					if (params == null) params = "";
					String longName = method.getLongName();
					String name = method.getName();

//					System.out.println("Interfaces: " + longName);

					if (longName.contains(".js.")) {

						String codeStr = null;
						
						if(name.startsWith("set_") && method.getParameterTypes().length == 1) {
							String variableName = name.replace("set_", "");
							codeStr = "this.node." + variableName + "=" + params;
						}
						else if(method.getName().startsWith("get_") && method.getParameterTypes().length == 0) {
							String variableName = name.replace("get_", "");
							codeStr = "this.node." + variableName;
						}
						else
							codeStr = "this.node." + name + "(" + params + ")";
						return codeStr;
					} else {
						return super.createMethodCall(method, code, params);
					}
				}
				
				public String getSubTypeExtractorFor(Class<?> interface1, String methodName)
				{
					if (methodName.equals("item") || methodName.equals("cloneNode"))
						return "temp.nodeType";

					return null;
				}

				public Class<? extends SubTypeFactory> getSubTypeFactoryClassFor(Class<?> interface1, String methodName)
				{
					if (methodName.equals("item") || methodName.equals("cloneNode"))
						return NodeSubTypeFactory.class;

					return null;
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

		add(Storage.class);
		return result;
	}

	@Override
	public boolean filterClassPath (String aClassPathEntry) {
		boolean flag = false;
		if (aClassPathEntry.contains(projName)) flag = true;
		else if (aClassPathEntry.contains("repository") && aClassPathEntry.contains("gdx") || aClassPathEntry.contains("gdx.jar") || aClassPathEntry.contains("gdx\\bin")) flag = true;
		else if (aClassPathEntry.contains("repository\\gdx-backend-dragome") || aClassPathEntry.contains("gdx-backend-dragome\\bin")) flag = true;
		else if (aClassPathEntry.contains("dragome-js-commons-") || aClassPathEntry.contains("dragome-js-commons\\bin")) flag = true;
		else if (aClassPathEntry.contains("dragome-js-jre-") || aClassPathEntry.contains("dragome-js-jre\\bin")) flag = true;
		else if (aClassPathEntry.contains("dragome-callback-evictor-") || aClassPathEntry.contains("dragome-callback-evictor\\bin")) flag = true;
		else if (aClassPathEntry.contains("dragome-form-bindings-") || aClassPathEntry.contains("dragome-form-bindings\\bin")) flag = true;
		else if (aClassPathEntry.contains("dragome-core-") || aClassPathEntry.contains("dragome-core\\bin")) flag = true;
		else if (aClassPathEntry.contains("dragome-method-logger-") || aClassPathEntry.contains("dragome-method-logger\\bin")) flag = true;
		else if (aClassPathEntry.contains("dragome-web-") || aClassPathEntry.contains("dragome-web\\bin")) flag = true;
		else if (aClassPathEntry.contains("dragome-guia-web-") || aClassPathEntry.contains("dragome-guia-web\\bin")) flag = true;
		else if (aClassPathEntry.contains("dragome-guia-") || aClassPathEntry.contains("dragome-guia\\bin")) flag = true;
		System.out.println("flag: " + flag + " path: " + aClassPathEntry);
		return flag;
	}

	public CompilerType getDefaultCompilerType () {
		return CompilerType.Standard;
	}

	@Override
	public boolean isCheckingCast () {
		return true;
	}
}
