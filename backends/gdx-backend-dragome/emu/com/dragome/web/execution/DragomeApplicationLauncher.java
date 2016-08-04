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
package com.dragome.web.execution;

import java.util.List;

import com.dragome.services.ServiceInvocation;
import com.dragome.services.ServiceLocator;
import com.dragome.services.WebServiceLocator;
import com.dragome.view.VisualActivity;
import com.dragome.web.debugging.ServiceInvocationResult;
import com.dragome.web.debugging.messages.Receiver;
import com.dragome.web.debugging.temp.TempHelper;
import com.dragome.web.dispatcher.EventDispatcherHelper;
import com.dragome.web.html.dom.DragomeJsException;
import com.dragome.web.html.dom.w3c.BrowserDomHandler;
import com.dragome.web.services.RequestExecutorImpl;

public class DragomeApplicationLauncher
{
	protected int responseMessagesCounter= 0;

	private ApplicationExecutor prepareLaunch()
	{
		WebServiceLocator.getInstance().setDomHandler(new BrowserDomHandler());
		WebServiceLocator.getInstance().setClientSideEnabled(true);

		if (WebServiceLocator.getInstance().isRemoteDebugging())
			return createDebuggingApplicationExecutor();
		else
			return createProductionApplicationExecutor();
	}

	private ApplicationExecutor createProductionApplicationExecutor()
	{
		return new ApplicationExecutor()
		{
			public void pushResult(ServiceInvocationResult result)
			{
			}

			public void executeByClassName(String typeName)
			{
				execute((Class<?>) ServiceLocator.getInstance().getReflectionService().forName(typeName));
			}

			public void execute(Class<?> type)
			{
				VisualActivity visualActivity= (VisualActivity) ServiceLocator.getInstance().getReflectionService().createClassInstance(type);
				visualActivity.onCreate();
			}

			public void pushException(DragomeJsException exception)
			{
				throw exception;
			}
		};
	}

	private ApplicationExecutor createDebuggingApplicationExecutor()
	{
		final ApplicationExecutor applicationExecutor= RequestExecutorImpl.createRemoteServiceByWebSocket(ApplicationExecutor.class);
		WebServiceLocator.getInstance().getClientToServerMessageChannel().setReceiver(new Receiver()
		{
			public void reset()
			{
			}

			public void messageReceived(String aMessage)
			{
				List<ServiceInvocation> serviceInvocations= (List<ServiceInvocation>) TempHelper.getObjectFromMessage(aMessage);
				for (ServiceInvocation serviceInvocation : serviceInvocations)
				{
					try
					{
						Object result= serviceInvocation.invoke();
						if (!WebServiceLocator.getInstance().isMethodVoid(serviceInvocation.getMethod()))
						{
							applicationExecutor.pushResult(new ServiceInvocationResult(serviceInvocation, result));
						}
					}
					catch (Exception e)
					{
						applicationExecutor.pushResult(new ServiceInvocationResult(serviceInvocation, new DragomeJsException(e, "Execution failed in browser: " + e.getMessage())));
					}
				}
			}
		});
		return applicationExecutor;
	}

	public void launch(final String typeName)
	{
		final ApplicationExecutor applicationExecutor= prepareLaunch();
		Runnable runnable= new Runnable()
		{
			public void run()
			{
				applicationExecutor.executeByClassName(typeName);
			}
		};
		EventDispatcherHelper.runApplication(runnable);
	}
}
