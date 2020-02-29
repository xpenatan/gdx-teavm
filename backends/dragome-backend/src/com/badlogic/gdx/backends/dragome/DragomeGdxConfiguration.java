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
import java.util.logging.Logger;

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
import org.w3c.dom.html.CanvasPixelArray;
import org.w3c.dom.html.CanvasRenderingContext2D;
import org.w3c.dom.html.HTMLCanvasElement;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.ImageData;
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
import org.w3c.dom.typedarray.Uint8ClampedArray;
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

import com.badlogic.gdx.backends.dragome.preloader.AssetsCopy;
import com.badlogic.gdx.utils.Array;
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
public abstract class DragomeGdxConfiguration extends ChainedInstrumentationDragomeConfigurator {
	private static Logger LOGGER= Logger.getLogger(DragomeGdxConfiguration.class.getName());
	private static final String DRAGOME_ADDITIONAL_SHRINK_CODE_KEEP_CONF = "/com/badlogic/gdx/backends/dragome/additional-shrink-code-keep.conf";
	private static final String DRAGOME_ADDITIONAL_OBFUSCATE_CODE_KEEP = "/com/badlogic/gdx/backends/dragome/additional-obfuscate-code-keep.conf";

	String projName;

	protected JsDelegateGenerator jsDelegateGenerator;

	final StringBuilder sb = new StringBuilder();

	protected List<Class<?>> classes= new ArrayList<>(Arrays.asList(Document.class, HTMLDocument.class, Element.class, Attr.class, NodeList.class,
			Node.class, NamedNodeMap.class, Text.class, HTMLCanvasElement.class, CanvasRenderingContext2D.class, EventTarget.class,
			EventListener.class, HTMLImageElementExtension.class, HTMLCanvasElementExtension.class, ImageData.class, CanvasPixelArray.class,
			Event.class, TouchEvent.class, TouchList.class, Touch.class, MouseEvent.class, KeyboardEvent.class, ProgressEvent.class, MessageEventExtension.class,
			WebGLActiveInfo.class, WebGLBuffer.class, WebGLContextAttributes.class, WebGLFramebuffer.class,
			WebGLObject.class, WebGLProgram.class, WebGLRenderbuffer.class, WebGLRenderingContext.class,
			WebGLShader.class, WebGLTexture.class, WebGLUniformLocation.class, WebGLRenderingContextExtension.class,
			ArrayBuffer.class, ArrayBufferView.class, Float32Array.class, Float64Array.class, Int16Array.class,
			Int32Array.class, Int8Array.class, Uint8ClampedArray.class, Uint16Array.class, Uint32Array.class, Uint8Array.class,
			ArrayBufferFactory.class, TypedArraysFactory.class, XMLHttpRequest.class, Object.class, WebSocket.class, XMLHttpRequestExtension.class));

	public DragomeGdxConfiguration() {
		String projPath= System.getProperty("user.dir");
		File file= new File(projPath);
		projName = file.getName().replace("\\", "/");

		final String thisClassName = getClass().getSimpleName();

		setClasspathFilter( new DefaultClasspathFileFilter() {

			@Override
			public boolean accept(ClasspathFile classpathFile) {
				if(!super.accept(classpathFile))
					return false;
				boolean flag = true;
				String fileName = classpathFile.getFilename();
				String path = classpathFile.getPath();
				path = path.replace("\\", "/");
				flag = toAccept(path, "/apache/", flag, true);
				flag = toAccept(path, "org/xmlvm/", flag, true);
				flag = toAccept(path, "org/xml/", flag, true);
				flag = toAccept(path, "org/mockito/", flag, true);
				flag = toAccept(path, "org/atmosphere/", flag, true);
				flag = toAccept(path, "org/objectweb/", flag, true);
				flag = toAccept(path, "org/objenesis/", flag, true);
				flag = toAccept(path, "org/hamcrest/", flag, true);
				flag = toAccept(path, "org/dom4j/", flag, true);
				flag = toAccept(path, "org/slf4j/", flag, true);
				flag = toAccept(path, "org/jdom/", flag, true);
				flag = toAccept(path, "org/reflections/", flag, true);
				flag = toAccept(path, "javax/", flag, true);

//				flag = toAccept(path, "javax/persistence/", flag, false);
//				flag = toAccept(path, "javax/script/", flag, false);
//				flag = toAccept(path, "javax/swing/", flag, false);

				flag = toAccept(path, "javax/persistence/Entity.class", flag, false);
				flag = toAccept(path, "javax/persistence/GeneratedValue.class", flag, false);
				flag = toAccept(path, "javax/persistence/GenerationType.class", flag, false);
				flag = toAccept(path, "javax/persistence/Id.class", flag, false);
				flag = toAccept(path, "javax/script/ScriptEngine.class", flag, false);
				flag = toAccept(path, "javax/script/ScriptEngineImpl.class", flag, false);
				flag = toAccept(path, "javax/script/ScriptEngineManager.class", flag, false);
				flag = toAccept(path, "javax/swing/event/TreeExpansionEvent.class", flag, false);
				flag = toAccept(path, "javax/swing/event/TreePath.class", flag, false);

				flag = toAccept(path, "META-INF/c-1_0-rt.tld", flag, true);
				flag = toAccept(path, "META-INF/c-1_0.tld", flag, true);
				flag = toAccept(path, "META-INF/c.tld", flag, true);
				flag = toAccept(path, "META-INF/fmt-1_0-rt.tld", flag, true);
				flag = toAccept(path, "META-INF/fmt-1_0.tld", flag, true);
				flag = toAccept(path, "META-INF/fmt.tld", flag, true);
				flag = toAccept(path, "META-INF/fn.tld", flag, true);
				flag = toAccept(path, "META-INF/html_basic.tld", flag, true);
				flag = toAccept(path, "META-INF/jsf_core.tld", flag, true);
				flag = toAccept(path, "META-INF/mojarra_ext.tld", flag, true);
				flag = toAccept(path, "META-INF/permittedTaglibs.tld", flag, true);
				flag = toAccept(path, "META-INF/scriptfree.tld", flag, true);
				flag = toAccept(path, "META-INF/sql-1_0-rt.tld", flag, true);
				flag = toAccept(path, "META-INF/sql-1_0.tld", flag, true);
				flag = toAccept(path, "META-INF/sql.tld", flag, true);
				flag = toAccept(path, "META-INF/x-1_0-rt.tld", flag, true);
				flag = toAccept(path, "META-INF/x-1_0.tld", flag, true);
				flag = toAccept(path, "META-INF/x.tld", flag, true);

				flag = toAccept(path, "proguard/", flag, true);
				flag = toAccept(path, "/google/", flag, true);
				flag = toAccept(path, "javassist/", flag, true);
				flag = toAccept(path, "ro/", flag, true);
				flag = toAccept(path, "io/netty/", flag, true);
				flag = toAccept(path, "net/sf/dexlib/", flag, true);
				flag = toAccept(path, "net/jpountz/", flag, true);
				flag = toAccept(path, "javolution/", flag, true);
				flag = toAccept(path, "/android/", flag, true);
				flag = toAccept(path, "/framework/", flag, true);
				flag = toAccept(path, "/runner/", flag, true);
				flag = toAccept(path, "/textui/", flag, true);

				flag = toAccept(path, "/dragome/compiler/", flag, true);
				flag = toAccept(path, "com/dragome/web/helpers/", flag, true);
				flag = toAccept(path, "dragome/web/serverside/", flag, true);
				flag = toAccept(path, "jsdelegate/serverside/", flag, true);
				flag = toAccept(path, "net/sf/", flag, true);
				flag = toAccept(path, "JDOMAbout", flag, true);
				flag = toAccept(path, "junit/", flag, true);
				flag = toAccept(path, "com/dragome/tests/", flag, true);

				flag = toAccept(path, "net/sf/flexjson", flag, false);

				flag = toAccept(path, "/DragomeGdxConfiguration", flag, true);
				flag = toAccept(path, "com/dragome/commons", flag, true);
				flag = toAccept(path, "DragomeConfigurator", flag, true);
				flag = toAccept(path, "dragome/services/serverside/", flag, true);
				flag = toAccept(path, "ApplicationConfigurator", flag, true);
//				flag = toAccept(path, "com/dragome/commons/AbstractProxyRelatedInvocationHandler", flag, false);
//				flag = toAccept(path, "com/dragome/commons/ChainedInstrumentationDragomeConfigurator", flag, false);
				flag = toAccept(path, "/commons/javascript", flag, false);
				flag = toAccept(path, "/commons/compiler/annotations", flag, false);
				flag = toAccept(path, "/commons/AbstractProxyRelatedInvocationHandler", flag, false);
				flag = toAccept(path, "/commons/ProxyRelatedInvocationHandler", flag, false);
				flag = toAccept(path, "/commons/DragomeConfiguratorImplementor", flag, false);
				flag = toAccept(path, "IntersectorTest.class", flag, true);
				flag = toAccept(path, "MathUtilsTest.class", flag, true);
				flag = toAccept(path, "RectangleTest.class", flag, true);
				flag = toAccept(path, "Shape2DTest.class", flag, true);
				flag = toAccept(path, "Vector2Test.class", flag, true);
				flag = toAccept(path, "Vector3Test.class", flag, true);
				flag = toAccept(path, "CollisionTest.class", flag, true);
				flag = toAccept(path, "BitsTest.class", flag, true);
				flag = toAccept(path, "PooledLinkedListTest.class", flag, true);
				flag = toAccept(path, "QueueTest.class", flag, true);
				if(path.endsWith("/"))
					flag = false;
				if(flag == false)
					flag = classClassPathFilter(fileName, path);

				flag = toAccept(path, "dragome/preloader/AssetFilter", flag, true);
				flag = toAccept(path, "dragome/preloader/DefaultAssetFilter", flag, true);
				flag = toAccept(path, "dragome/preloader/AssetsCopy", flag, true);
				flag = toAccept(path, thisClassName, flag, true);

				int filterClassLog = filterClassLog();
				if((filterClassLog == 1 && flag) || filterClassLog == 2)
					System.out.println("Allow Class: " + flag + " Path: " + path);
				return flag;
			}
		});


		if(!skipAssetCopy()) {
			String compiledPath = getCompiledPath();
			if(compiledPath != null && compiledPath.isEmpty() == false) {
				System.out.println("WebApp path " + compiledPath);
				String assetsOutputPath = compiledPath + "\\assets";
				Array<File> paths = new Array<>();
				Array<String> classPathFiles = new Array<>();
				assetsDefaultClasspath(classPathFiles);
				assetsPath(paths);
				AssetsCopy.copy(paths, classPathFiles, assetsOutputPath, generateAssetsTextFile());
			}
		}
	}

	public boolean toAccept(String path, String condition, boolean flag, boolean useNegAnd) {
		if(useNegAnd)
			flag &= !path.contains(condition);
		else
			flag |= path.contains(condition);
		return flag;
	}

	@Override
	public List<ClasspathEntry> getExtraClasspath(Classpath classpath) {
		List<ClasspathEntry> result= new ArrayList<>();
		List<ClasspathFile> classpathFiles= new ArrayList<>();

		result.add(new VirtualFolderClasspathEntry(classpathFiles));

		if (jsDelegateGenerator == null)
			createJsDelegateGenerator(classpath);

		for (Class<?> class1 : classes) {
			InMemoryClasspathFile inMemoryClasspathFile= jsDelegateGenerator.generateAsClasspathFile(class1);
			addClassBytecode(inMemoryClasspathFile.getBytecode(), inMemoryClasspathFile.getClassname());
			classpathFiles.add(inMemoryClasspathFile);
		}

		return result;
	}
	private void createJsDelegateGenerator(Classpath classpath) {
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

	public DragomeGdxConfiguration(List<? extends Class<?>> additionalDelegates) {
		classes.addAll(additionalDelegates);
	}

	@Override
	public boolean filterClassPath(String classpathEntry) {
		boolean include= super.filterClassPath(classpathEntry);
		classpathEntry = classpathEntry.replace("\\", "/");
		include |= classpathEntry.contains(projName + "/") && classpathEntry.endsWith("/bin");
//		include |= classpathEntry.contains(projName + "/") && classpathEntry.endsWith("/bin/main");
		include |= classpathEntry.contains(projName + "/") && classpathEntry.endsWith("/classes/java/main");
		include |= classpathEntry.contains("dragome-js-jre");
		include |= classpathEntry.contains("dragome-web");
		include |= classpathEntry.contains("dragome-core");
		include |= classpathEntry.contains("dragome-js-commons");
		include |= classpathEntry.contains("dragome-w3c-standards");
		include |= classpathEntry.contains("dragome-bytecode-js-compiler");
		if(classpathEntry.contains("gdx")) {
			include =true;
			include|= classpathEntry.contains("/gdx-") && classpathEntry.contains(".jar");
			include|= classpathEntry.contains("gdx/bin");
			include|= classpathEntry.contains("gdx/classes");

			if(classpathEntry.contains("gdx-box2d")) {
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
			else if(classpathEntry.contains("gdx-freetype-dragome")) {
				include|= classpathEntry.contains("gdx-freetype-dragome-");
				include|= classpathEntry.contains("/bin");
				include|= classpathEntry.contains("/classes");
			}
			else if(include == false) {
				try {// check if its gdx-X.X.X.jar
					String[] split1 = classpathEntry.split("/");
					String gdxPath = split1[split1.length-1];
					String[] split = gdxPath.split("-");
					if(split.length == 2)
						include |= (split[0].toLowerCase().endsWith("gdx") && Character.isDigit(split[1].charAt(0)));
				} catch (Exception e) {
				}

			}
		}

		if(!include && classpathEntry.contains("/bin"))
			include = true;
		include &= !classpathEntry.contains("/resources/");
		if(classpathEntry.contains("-sources."))
			include = false;

		if(include == false)
			include = projectClassPathFilter(classpathEntry);
		if(filterClassPathLog())
		{
			String text = "Compile: " + include + " path: " + classpathEntry + "\n";
			if(include)
				sb.insert(0, text);
			else
				sb.append(text);
		}
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

	public boolean generateAssetsTextFile() {
		return false;
	}

	private void assetsDefaultClasspath (Array<String> filePath) {
		filePath.add("com/badlogic/gdx/graphics/g3d/particles/");
		filePath.add("com/badlogic/gdx/graphics/g3d/shaders/");
		filePath.add("com/badlogic/gdx/utils/arial-15.fnt"); // Cannot be utils folder for now because its trying to copy from emu folder and not core gdx classpath
		filePath.add("com/badlogic/gdx/utils/arial-15.png");
		filePath.add("soundmanager2-jsmin.js");
		assetsClasspath(filePath);
	}

	@Override
	public String getCompiledPath () {
		File file = new File("." + File.separatorChar + "webapp");
		if(!file.exists()) // may be false if build was not inside dragome project
			return null;
		return file.getAbsolutePath();
	}

	public abstract void assetsPath (Array<File> paths);

	public abstract void assetsClasspath (Array<String> classPaths);

	@Override
	public void sortClassPath(Classpath classPath) {
		classPath.sortByPriority(new PrioritySolver()
		{
			@Override
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

		if(sb.length() > 0)
			sb.insert(0, "\n" + "########### Libs/Classes PATH to allow Dragome to compile ###########" + "\n");

		Iterator<ClasspathEntry> iterator = classPath.getEntries().iterator();
		int i = 0;
		sb.append("\n" + "######################## Libs ClassPath Order ########################\n");
		while(iterator.hasNext()) {
			ClasspathEntry next = iterator.next();
			String name = next.getName();
			if(next instanceof VirtualFolderClasspathEntry)
				name = "Delegate Virtual Path";
			sb.append(i + ": " + name);
			sb.append("\n");
			i++;
		}
		sb.append("#################################################################");
		LOGGER.info(sb.toString());
		sb.setLength(0);
	}

	@Override
	public List<URL> getAdditionalCodeKeepConfigFile() {
		ArrayList<URL> urls = new ArrayList<>();
		final URL resource = DragomeGdxConfiguration.class.getResource(DRAGOME_ADDITIONAL_SHRINK_CODE_KEEP_CONF);
		if (resource != null)
			urls.add(resource);
		else
			LOGGER.warning("Can not find: " + DRAGOME_ADDITIONAL_SHRINK_CODE_KEEP_CONF);
		return urls;
	}

	@Override
	public List<URL> getAdditionalObfuscateCodeKeepConfigFile () {
		ArrayList<URL> urls = new ArrayList<>();
		final URL resource = DragomeGdxConfiguration.class.getResource(DRAGOME_ADDITIONAL_OBFUSCATE_CODE_KEEP);
		if (resource != null) {
			urls.add(resource);
		} else {
			LOGGER.warning("Can not find: " + DRAGOME_ADDITIONAL_OBFUSCATE_CODE_KEEP);
		}
		return urls;
	}

	/**
	 * Returning true will show which classpath/project is allowed to compile. (Default: false)
	 */
	public boolean filterClassPathLog() {
		return false;
	}

	/**
	 * Returning 1 will show only classes that is allowed to compile. <br>
	 * Returning 2 will show all classes that is filtered. <br>
	 * Reutrning 0 will disable log;
	 */
	public int filterClassLog() {
		return 0;
	}

	/**
	 * Skip asset copying. (Default: false)
	 */
	public boolean skipAssetCopy() {
		return false;
	}

	@Override
	public boolean isCheckingCast() {
		return false;
	}

	/**
	 * Makes proguard to shrink and remove unused classes. <br>
	 * Use {@link #getAdditionalCodeKeepConfigFile()} to append more proguard shrink options <br>
	 * @return true to remove code (Default: true)
	 */
	@Override
	public boolean isRemoveUnusedCode() {
		return true;
	}

	/**
	 * Makes proguard to obfuscate and have shorter class names. This will reduce javascript size. <br>
	 * Use {@link #getAdditionalObfuscateCodeKeepConfigFile()} to append more proguard obfuscate options <br>
	 * @return true to obfuscate code (Default: true)
	 */
	@Override
	public boolean isObfuscateCode () {
		return true;
	}

	@Override
	public boolean isCaching () {
		return false;
	}
}
