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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.w3c.dom.events.Event;
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

import com.dragome.commons.DragomeConfiguratorImplementor;
import com.dragome.commons.compiler.ClasspathFile;
import com.dragome.commons.compiler.annotations.CompilerType;
import com.dragome.web.config.DomHandlerApplicationConfigurator;
import com.dragome.web.helpers.serverside.DefaultClasspathFilter;
import com.dragome.web.html.dom.w3c.ArrayBufferFactory;
import com.dragome.web.html.dom.w3c.HTMLCanvasElementExtension;
import com.dragome.web.html.dom.w3c.HTMLImageElementExtension;
import com.dragome.web.html.dom.w3c.TypedArraysFactory;
import com.dragome.web.html.dom.w3c.WebGLRenderingContextExtension;

/** @author xpenatan */
@DragomeConfiguratorImplementor(priority= 10)
public class DragomeConfiguration extends DomHandlerApplicationConfigurator
{
	HashSet<String> paths;
	String projName;
	String projPath;
	List<ClasspathFile> extraClasspathFiles;
	static List<Class<?>> additonalClasses= Arrays.asList(HTMLImageElementExtension.class, HTMLCanvasElementExtension.class, Event.class, //
			WebGLActiveInfo.class, WebGLBuffer.class, WebGLContextAttributes.class, WebGLFramebuffer.class, //
			WebGLObject.class, WebGLProgram.class, WebGLRenderbuffer.class, WebGLRenderingContext.class, //
			WebGLShader.class, WebGLTexture.class, WebGLUniformLocation.class, WebGLRenderingContextExtension.class, //
			ArrayBuffer.class, ArrayBufferView.class, Float32Array.class, Float64Array.class, Int16Array.class, //
			Int32Array.class, Int8Array.class, Uint16Array.class, Uint32Array.class, Uint8Array.class, //
			ArrayBufferFactory.class, TypedArraysFactory.class, Object.class);

	boolean cache= false;

	public DragomeConfiguration()
	{
		super(additonalClasses);
		// System.setProperty("dragome-compile-mode", CompilerMode.Production.toString());
		// System.setProperty("dragome-compile-mode", CompilerMode.Debug.toString());

		extraClasspathFiles= new ArrayList<ClasspathFile>();
		projPath= System.getProperty("user.dir");
		File file= new File(projPath);
		projName= file.getName();
		paths= new HashSet<String>();
		setClasspathFilter(new DefaultClasspathFilter()
		{
			public boolean accept(File pathname, File folder)
			{
				boolean accept= super.accept(pathname, folder);

				String relativePath= folder.toURI().relativize(pathname.toURI()).getPath();

				String className= relativePath.replace(".class", "");
				if (paths.contains(className))
					return false;

				if (relativePath.contains("gdx-backend-dragome"))
					paths.add(className);

				System.out.println("ClassPathFilter: " + accept + " - " + relativePath);

				return accept;
			}

		});

	}

	@Override
	public boolean filterClassPath(String aClassPathEntry)
	{
		boolean flag= false;
		if (aClassPathEntry.contains(projName))
			flag= true;
		else if (aClassPathEntry.contains("repository") && aClassPathEntry.contains("gdx") || aClassPathEntry.contains("gdx.jar") || aClassPathEntry.contains("gdx\\bin"))
			flag= true;
		else if (aClassPathEntry.contains("repository\\gdx-backend-dragome") || aClassPathEntry.contains("gdx-backend-dragome\\bin"))
			flag= true;
		else if (aClassPathEntry.contains("dragome-js-commons-") || aClassPathEntry.contains("dragome-js-commons\\bin"))
			flag= true;
		else if (aClassPathEntry.contains("dragome-js-jre-") || aClassPathEntry.contains("dragome-js-jre\\bin"))
			flag= true;
		else if (aClassPathEntry.contains("dragome-w3c-standards-") || aClassPathEntry.contains("dragome-w3c-standards\\bin"))
			flag= true;
		else if (aClassPathEntry.contains("dragome-callback-evictor-") || aClassPathEntry.contains("dragome-callback-evictor\\bin"))
			flag= true;
		else if (aClassPathEntry.contains("dragome-form-bindings-") || aClassPathEntry.contains("dragome-form-bindings\\bin"))
			flag= true;
		else if (aClassPathEntry.contains("dragome-core-") || aClassPathEntry.contains("dragome-core\\bin"))
			flag= true;
		else if (aClassPathEntry.contains("dragome-method-logger-") || aClassPathEntry.contains("dragome-method-logger\\bin"))
			flag= true;
		else if (aClassPathEntry.contains("dragome-web-") || aClassPathEntry.contains("dragome-web\\bin"))
			flag= true;
		else if (aClassPathEntry.contains("dragome-guia-web-") || aClassPathEntry.contains("dragome-guia-web\\bin"))
			flag= true;
		else if (aClassPathEntry.contains("dragome-guia-") || aClassPathEntry.contains("dragome-guia\\bin"))
			flag= true;
		System.out.println("flag: " + flag + " path: " + aClassPathEntry);
		return flag;
	}

	public CompilerType getDefaultCompilerType()
	{
		return CompilerType.Standard;
	}

	public boolean isCheckingCast()
	{
		return false;
	}
}
