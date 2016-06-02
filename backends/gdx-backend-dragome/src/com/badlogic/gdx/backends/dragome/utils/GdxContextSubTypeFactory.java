package com.badlogic.gdx.backends.dragome.utils;

import java.util.Arrays;
import java.util.List;

import org.w3c.dom.html.CanvasRenderingContext2D;

import com.badlogic.gdx.backends.dragome.js.webgl.WebGLRenderingContext;
import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.web.enhancers.jsdelegate.interfaces.SubTypeFactory;

public class GdxContextSubTypeFactory implements SubTypeFactory
{
	protected List<String> contextNames= Arrays.asList("experimental-webgl", "webgl", "moz-webgl", "webkit-webgl", "webkit-3d");

	public Class<?> getSubTypeWith(Object instance)
	{
		ScriptHelper.put("contextType", instance, this);
		boolean is2d= ScriptHelper.evalBoolean("contextType == '2d'", this);
		boolean isGL= contextNames.contains(instance.toString());

		if (is2d)
			return CanvasRenderingContext2D.class;
		else if (isGL)
			return WebGLRenderingContext.class;

		return null;
	}
}
