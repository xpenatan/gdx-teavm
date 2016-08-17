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
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.XMLHttpRequest;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.KeyboardEvent;
import org.w3c.dom.events.MouseEvent;
import org.w3c.dom.events.ProgressEvent;
import org.w3c.dom.events.Touch;
import org.w3c.dom.events.TouchEvent;
import org.w3c.dom.events.TouchList;
import org.w3c.dom.html.CanvasRenderingContext2D;
import org.w3c.dom.html.HTMLCanvasElement;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.typedarray.ArrayBuffer;
import org.w3c.dom.typedarray.ArrayBufferView;
import org.w3c.dom.typedarray.Float32Array;
import org.w3c.dom.typedarray.Float64Array;
import org.w3c.dom.typedarray.Int16Array;
import org.w3c.dom.typedarray.Int32Array;
import org.w3c.dom.typedarray.Int8Array;
import org.w3c.dom.typedarray.Uint16Array;
import org.w3c.dom.typedarray.Uint32Array;
import org.w3c.dom.typedarray.Uint8Array;
import org.w3c.dom.webgl.WebGLActiveInfo;
import org.w3c.dom.webgl.WebGLBuffer;
import org.w3c.dom.webgl.WebGLContextAttributes;
import org.w3c.dom.webgl.WebGLFramebuffer;
import org.w3c.dom.webgl.WebGLObject;
import org.w3c.dom.webgl.WebGLProgram;
import org.w3c.dom.webgl.WebGLRenderbuffer;
import org.w3c.dom.webgl.WebGLRenderingContext;
import org.w3c.dom.webgl.WebGLShader;
import org.w3c.dom.webgl.WebGLTexture;
import org.w3c.dom.webgl.WebGLUniformLocation;
import org.w3c.dom.websocket.WebSocket;

import com.dragome.commons.ChainedInstrumentationDragomeConfigurator;
import com.dragome.commons.DragomeConfiguratorImplementor;
import com.dragome.commons.compiler.PrioritySolver;
import com.dragome.commons.compiler.classpath.Classpath;
import com.dragome.commons.compiler.classpath.ClasspathEntry;
import com.dragome.commons.compiler.classpath.ClasspathFile;
import com.dragome.commons.compiler.classpath.InMemoryClasspathFile;
import com.dragome.commons.compiler.classpath.serverside.VirtualFolderClasspathEntry;
import com.dragome.web.config.DomHandlerDelegateStrategy;
import com.dragome.web.enhancers.jsdelegate.serverside.JsDelegateGenerator;
import com.dragome.web.helpers.DefaultClasspathFileFilter;
import com.dragome.web.html.dom.w3c.ArrayBufferFactory;
import com.dragome.web.html.dom.w3c.HTMLCanvasElementExtension;
import com.dragome.web.html.dom.w3c.HTMLImageElementExtension;
import com.dragome.web.html.dom.w3c.MessageEventExtension;
import com.dragome.web.html.dom.w3c.TypedArraysFactory;
import com.dragome.web.html.dom.w3c.WebGLRenderingContextExtension;
import com.dragome.web.services.RequestExecutorImpl.XMLHttpRequestExtension;

/** @author xpenatan */
@DragomeConfiguratorImplementor(priority= 10)
public class DragomeConfiguration extends ChainedInstrumentationDragomeConfigurator
{
	String projName;
	
	protected JsDelegateGenerator jsDelegateGenerator;
	
	
	protected List<Class<?>> classes= new ArrayList<>(Arrays.asList(Document.class, HTMLDocument.class, Element.class, Attr.class, NodeList.class, 
			Node.class, NamedNodeMap.class, Text.class, HTMLCanvasElement.class, CanvasRenderingContext2D.class, EventTarget.class,  
			EventListener.class, HTMLImageElementExtension.class, HTMLCanvasElementExtension.class, 
			Event.class, TouchEvent.class, TouchList.class, Touch.class, MouseEvent.class, KeyboardEvent.class, ProgressEvent.class, MessageEventExtension.class,
			WebGLActiveInfo.class, WebGLBuffer.class, WebGLContextAttributes.class, WebGLFramebuffer.class,
			WebGLObject.class, WebGLProgram.class, WebGLRenderbuffer.class, WebGLRenderingContext.class, 
			WebGLShader.class, WebGLTexture.class, WebGLUniformLocation.class, WebGLRenderingContextExtension.class, 
			ArrayBuffer.class, ArrayBufferView.class, Float32Array.class, Float64Array.class, Int16Array.class,
			Int32Array.class, Int8Array.class, Uint16Array.class, Uint32Array.class, Uint8Array.class, 
			ArrayBufferFactory.class, TypedArraysFactory.class, XMLHttpRequest.class, Object.class, WebSocket.class, XMLHttpRequestExtension.class));
	
	public DragomeConfiguration()
	{
		String projPath= System.getProperty("user.dir");
		File file= new File(projPath);
		projName = file.getName().replace("\\", "/");
		
		setClasspathFilter( new DefaultClasspathFileFilter() {
			
			@Override
			public boolean accept(ClasspathFile classpathFile) {
				if(!super.accept(classpathFile))
					return false;
				boolean flag = true;
				String fileName = classpathFile.getFilename();
				String path = classpathFile.getPath();
				path = path.replace("\\", "/");
				flag &= !path.contains("/apache/");
				flag &= !path.contains("/org/xmlvm/");
				flag &= !path.contains("/org/xml/");
				flag &= !path.contains("/org/mockito/");
				flag &= !path.contains("/org/atmosphere/");
				flag &= !path.contains("/org/objectweb/");
				flag &= !path.contains("/org/objenesis/");
				flag &= !path.contains("/org/hamcrest/");
				flag &= !path.contains("/org/dom4j/");
				flag &= !path.contains("/org/slf4j/");
				flag &= !path.contains("/org/jdom/");
				flag &= !path.contains("/org/reflections/");
				flag &= !path.contains("/javax/");
				flag |= path.contains("/javax/persistence/");
				flag |= path.contains("/javax/script/");
				flag |= path.contains("/javax/swing/");
				flag &= !path.contains("/proguard/");
				flag &= !path.contains("/google/");
				flag &= !path.contains("/javassist/");
				flag &= !path.contains("/ro/");
				flag &= !path.contains("/io/netty/");
				flag &= !path.contains("/net/sf/dexlib/");
				flag &= !path.contains("/net/jpountz/");
				flag &= !path.contains("/javolution/");
				flag &= !path.contains("/android/");
				flag &= !path.contains("/framework/");
				flag &= !path.contains("/runner/");
				flag &= !path.contains("/textui/");
				
				flag &= !path.contains("/dragome/compiler");
				flag &= !path.contains("com/dragome/web/helpers");
				flag &= !path.contains("/net/sf/");
				flag &= !path.contains("/JDOMAbout");
				flag &= !path.contains("/junit/");
				flag &= !path.contains("com/dragome/tests/");
				flag |= path.contains("net/sf/flexjson");
				flag &= !path.contains("/DragomeConfiguration");
				flag &= !path.contains("/JsConfiguration");
				flag &= !path.contains("com/dragome/commons");
				flag &= !path.contains("DragomeConfigurator");
				flag &= !path.contains("ApplicationConfigurator");
				flag |= path.contains("/commons/javascript");
				flag |= path.contains("/compiler/annotations/");
				flag |= path.contains("/commons/AbstractProxyRelatedInvocationHandler");
				flag |= path.contains("/commons/ProxyRelatedInvocationHandler");
				flag |= path.contains("/commons/DragomeConfiguratorImplementor");
				if(flag == false)
					flag = classClassPathFilter(fileName, path);
//				if(flag)
//					System.out.println("Flag: " + flag + " Path: " + path);
				return flag;
			}
		});
	}
	
	public List<ClasspathEntry> getExtraClasspath(Classpath classpath)
	{
		List<ClasspathEntry> result= new ArrayList<ClasspathEntry>();
		List<ClasspathFile> classpathFiles= new ArrayList<ClasspathFile>();

		result.add(new VirtualFolderClasspathEntry(classpathFiles));

		if (jsDelegateGenerator == null)
			createJsDelegateGenerator(classpath);

		for (Class<?> class1 : classes)
		{
			InMemoryClasspathFile inMemoryClasspathFile= jsDelegateGenerator.generateAsClasspathFile(class1);
			addClassBytecode(inMemoryClasspathFile.getBytecode(), inMemoryClasspathFile.getClassname());
			classpathFiles.add(inMemoryClasspathFile);
		}

		return result;
	}
	private void createJsDelegateGenerator(Classpath classpath)
	{
		jsDelegateGenerator= new JsDelegateGenerator(classpath.toString().replace(";", System.getProperty("path.separator")), new DomHandlerDelegateStrategy()
		{
			@Override
			public String createMethodCall(Method method, String params) {
				String longName = method.toGenericString();
				String name = method.getName();
				if (longName.contains(".js.")) {
					if(params == null)
						params = "";
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
				}
				else
					return super.createMethodCall(method, params);
			}
		});
	}
	
	public DragomeConfiguration(List<? extends Class<?>> additionalDelegates)
	{
		classes.addAll(additionalDelegates);
	}

	public boolean filterClassPath(String classpathEntry)
	{
		boolean include= super.filterClassPath(classpathEntry);
		classpathEntry = classpathEntry.replace("\\", "/");
		include |= classpathEntry.contains(projName + "/") && classpathEntry.contains("/bin");
		include |= classpathEntry.contains(projName + "/") && classpathEntry.contains("/classes");
		if(classpathEntry.contains("gdx")) {
			include|= classpathEntry.contains("/gdx.jar");
			include|= classpathEntry.contains("gdx/bin");
			include|= classpathEntry.contains("gdx/classes");
			
			if(classpathEntry.contains("gdx-box2d-")) {
				include|= classpathEntry.contains("gdx-box2d-dragome-");
				include|= classpathEntry.contains("gdx-box2d-gwt-");
				include|= classpathEntry.contains("/bin");
				include|= classpathEntry.contains("/classes");
			}
			else if(classpathEntry.contains("gdx-bullet-dragome")) {
				include|= classpathEntry.contains("gdx-bullet-dragome-");
				include|= classpathEntry.contains("/bin");
				include|= classpathEntry.contains("/classes");
			}
			else if(classpathEntry.contains("gdx-backend-dragome")) {
				include|= classpathEntry.contains("gdx-backend-dragome-");
				include|= classpathEntry.contains("/bin");
				include|= classpathEntry.contains("/classes");
			}
			else if(include == false) {
				String[] split = classpathEntry.split("-");
				if(split.length == 2)
					include |= (split[0].toLowerCase().endsWith("gdx") && Character.isDigit(split[1].charAt(0)));
			}
		}
		include &= !classpathEntry.contains("/resources/");
		if(include == false)
			include = projectClassPathFilter(classpathEntry);
//		System.out.println("Allow Project: " + include + " path: " + classpathEntry);
		return include;
	}
	
	/**
	 * Return true to allow a project to be compiled to javascript.
	 */
	public boolean projectClassPathFilter(String projectPath) {return false;}
	/**
	 * Return true to allow a class in allowed project to be compiled to javascript.
	 */
	public boolean classClassPathFilter(String fileName, String path) {return false;}
	
	public void sortClassPath(Classpath classPath)
	{
		classPath.sortByPriority(new PrioritySolver()
		{
			public int getPriorityOf(ClasspathEntry string)
			{
				String name = string.getName();
				if (name.contains("gdx-backend-dragome"))
					return 4; // dragome backend is first so it can override any dragome classes
				else if (name.contains("dragome-js-jre"))
					return 3;
				else if (name.contains("dragome-"))
					return 2;
				else
					return 0;
			}
		});
		Iterator<ClasspathEntry> iterator = classPath.getEntries().iterator();
		System.out.println("######################## ClassPath Order ########################");
		while(iterator.hasNext())
			System.out.println(iterator.next());
		System.out.println("#################################################################");
	}

	public URL getAdditionalCodeKeepConfigFile()
	{
		return DragomeConfiguration.class.getResource("/additional-code-keep.conf");
	}

	public boolean isCheckingCast()
	{
		return false;
	}

	public boolean isRemoveUnusedCode()
	{
		return true;
	}
	
	@Override
	public boolean isCaching () {
		return false;
	}
	
	@Override
	public String getCompiledPath () {
		String compiledPath = new File("").getAbsolutePath() + "\\webapp";
		System.out.println("Generating javascript to " + compiledPath);
		return compiledPath;
	}
}
