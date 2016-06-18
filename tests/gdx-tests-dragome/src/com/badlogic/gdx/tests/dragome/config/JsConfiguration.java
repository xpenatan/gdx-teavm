package com.badlogic.gdx.tests.dragome.config;

import com.badlogic.gdx.backends.dragome.DragomeConfiguration;
import com.dragome.commons.DragomeConfiguratorImplementor;

@DragomeConfiguratorImplementor(priority= 11)
public class JsConfiguration extends DragomeConfiguration{
	@Override
	public boolean filterClassPath(String aClassPathEntry) {
		boolean include = super.filterClassPath(aClassPathEntry);
		include|= aClassPathEntry.contains("bullet-dragome.jar") || aClassPathEntry.contains("bullet-dragome\\bin");
		return include;
	}
}
