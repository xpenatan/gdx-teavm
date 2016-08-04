/*
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dragome.web.dispatcher;

import java.util.ArrayList;
import java.util.List;

import com.dragome.commons.compiler.annotations.AnnotationsHelper;
import com.dragome.commons.compiler.annotations.AnnotationsHelper.AnnotationContainer.AnnotationEntry;
import com.dragome.commons.compiler.annotations.MethodAlias;
import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.services.ServiceLocator;
import com.dragome.services.WebServiceLocator;
import com.dragome.services.interfaces.ParametersHandler;
import com.dragome.web.annotations.PageAlias;
import com.dragome.web.execution.DragomeApplicationLauncher;

public class EventDispatcherHelper
{
	public static Runnable applicationRunner;

	public static void alert(String message)
	{
		ScriptHelper.put("message", message, null);
		ScriptHelper.eval("alert(message)", null);
	}

	@MethodAlias(alias= "EventDispatcher.executeMainClass")
	public static void executeMainClass() throws Exception
	{
		try
		{
			WebServiceLocator.getInstance().setClientSideEnabled(true);

			ParametersHandler parametersHandler= ServiceLocator.getInstance().getParametersHandler();

			String className= parametersHandler.getParameter("class");
			if (className == null || className.trim().length() == 0)
			{
				String requestURL= parametersHandler.getRequestURL();
				List<AnnotationEntry> annotationEntries= new ArrayList<>(AnnotationsHelper.getAnnotationsByType(PageAlias.class).getEntries());

				className= findDiscovererPage(className, annotationEntries);

				if (className == null)
					for (AnnotationEntry annotationEntry : annotationEntries)
					{
						boolean isUnique= annotationEntries.size() == 1;
						boolean urlContainsAlias= requestURL.contains(annotationEntry.getAnnotationValue());
						String annotationKey = annotationEntry.getAnnotationKey();
						String[] split = annotationKey.split(":");
						boolean isAliasKey= split[4].equals("alias");

						if (isUnique || (isAliasKey && urlContainsAlias))
							className= annotationEntry.getType().getName();
					}
			}

			launch(className);
		}
		catch (Exception e)
		{
			alert("ERROR (more info on browser console):" + e.getMessage());
			throw e;
		}
	}

	private static String findDiscovererPage(String requestURL, List<AnnotationEntry> annotationEntries)
	{
		String className= null;

		for (AnnotationEntry annotationEntry : annotationEntries)
		{
			boolean isDiscoverPage= annotationEntry.getType().getSimpleName().equals("DiscovererPage");
			if (isDiscoverPage)
			{
				annotationEntries.remove(annotationEntry);

				if (requestURL.contains(annotationEntry.getAnnotationValue()))
					className= annotationEntry.getType().getName();
			}
		}
		return className;
	}

	private static void launch(String className) throws Exception
	{
		try
		{
			if (className == null || className.trim().length() == 0)
				System.out.println("Please specify activity class to execute in querystring parameter 'class'");
			else
				new DragomeApplicationLauncher().launch(className);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}

	@MethodAlias(alias= "EventDispatcher.runApplication")
	public static void runApplication()
	{
		if (applicationRunner != null)
			applicationRunner.run();
		else
			alert("Cannot find any activity to execute, please add annotation @PageAlias(alias= \"page-name\") to your activity class.");
	}

	public static void runApplication(Runnable runnable)
	{
		if (WebServiceLocator.getInstance().isRemoteDebugging())
			applicationRunner= runnable;
		else
			runnable.run();
	}

	public EventDispatcherHelper()
	{
	}
}
