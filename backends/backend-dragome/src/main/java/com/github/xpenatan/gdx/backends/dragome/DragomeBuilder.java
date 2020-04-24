package com.github.xpenatan.gdx.backends.dragome;

import java.io.File;
import com.dragome.services.ReflectionServiceImpl;
import com.dragome.services.ServiceLocator;
import com.dragome.web.helpers.serverside.StandaloneDragomeAppGenerator;

/**
 * @author xpenatan
 */
public class DragomeBuilder {

	public static void build(DragomeBuildConfigurator configurator) {
		DragomeGdxConfigurator gdxConfiguration = new DragomeGdxConfigurator(configurator);

		System.out.println("---------------Initializing---------------");

		ServiceLocator serviceLocator = ServiceLocator.getInstance();
		serviceLocator.setReflectionService(new ReflectionServiceImpl());
		serviceLocator.setConfigurator(gdxConfiguration);

		File destinationDirectory = new File(configurator.getWebAppPath());
		File webappDirectory = new File(configurator.getWebAppPath());
		StandaloneDragomeAppGenerator gen = new StandaloneDragomeAppGenerator(destinationDirectory, webappDirectory, true, true, false);
		gen.execute(false);
	}
}
