package com.badlogic.gdx.backends.dragome.js.webgl;

import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.web.enhancers.jsdelegate.JsDelegateFactory;

public class WebGLFactory
{
	public static WebGLContextAttributes create()
	{
		WebGLContextAttributes attr= null;
		Object instance= ScriptHelper.eval("{premultipliedAlpha:false}", null);
		attr= JsDelegateFactory.createFrom(instance, WebGLContextAttributes.class);
		return attr;
	}
}
