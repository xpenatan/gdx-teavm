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
import java.net.URL;

import com.dragome.commons.DragomeConfiguratorImplementor;
import com.dragome.commons.compiler.PrioritySolver;
import com.dragome.commons.compiler.classpath.Classpath;
import com.dragome.commons.compiler.classpath.ClasspathEntry;
import com.dragome.web.config.DomHandlerApplicationConfigurator;

/** @author xpenatan */
@DragomeConfiguratorImplementor(priority= 10)
public class DragomeConfiguration extends DomHandlerApplicationConfigurator
{
	String projName;
	
	boolean deleteCache = true;

	public DragomeConfiguration()
	{
		String projPath= System.getProperty("user.dir");
		File file= new File(projPath);
		projName= file.getName();
		
		if(deleteCache) {
			File file2 = new File(projPath + "\\target\\dragome.cache");
			if (file2.exists()) 
				file2.delete();
		}
	}

	public boolean filterClassPath(String aClassPathEntry)
	{
		boolean include= false;

		include|= aClassPathEntry.contains(projName);
		include|= aClassPathEntry.contains("repository") && aClassPathEntry.contains("gdx") || aClassPathEntry.contains("gdx.jar") || aClassPathEntry.contains("gdx\\bin");
		include|= aClassPathEntry.contains("repository\\gdx-backend-dragome") || aClassPathEntry.contains("gdx-backend-dragome\\bin");
		include|= aClassPathEntry.contains("dragome-js-commons-") || aClassPathEntry.contains("dragome-js-commons\\bin");
		include|= aClassPathEntry.contains("dragome-js-jre-") || aClassPathEntry.contains("dragome-js-jre\\bin");
		include|= aClassPathEntry.contains("dragome-w3c-standards-") || aClassPathEntry.contains("dragome-w3c-standards\\bin");
		include|= aClassPathEntry.contains("dragome-core-") || aClassPathEntry.contains("dragome-core\\bin");
		include|= aClassPathEntry.contains("dragome-web-") || aClassPathEntry.contains("dragome-web\\bin");

		System.out.println("flag: " + include + " path: " + aClassPathEntry);

		return include;
	}

	public void sortClassPath(Classpath classPath)
	{
		classPath.sortByPriority(new PrioritySolver()
		{
			public int getPriorityOf(ClasspathEntry string)
			{
				if (string.getName().contains("/classes"))
					return 2;
				else if (string.getName().contains("dragome-"))
					return 1;
				else
					return 0;
			}
		});
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
}
