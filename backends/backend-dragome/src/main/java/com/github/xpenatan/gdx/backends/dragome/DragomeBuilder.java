package com.github.xpenatan.gdx.backends.dragome;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import com.dragome.commons.compiler.classpath.Classpath;
import com.dragome.services.ReflectionServiceImpl;
import com.dragome.services.ServiceLocator;
import com.dragome.web.helpers.serverside.StandaloneDragomeAppGenerator;

public class DragomeBuilder {

	public static void build(DragomeConfigurator configuration) {

//		URL[] urLs = ((URLClassLoader) (Thread.currentThread().getContextClassLoader())).getURLs();
//		ArrayList<URL> acceptedURL = new ArrayList<>();
//		ArrayList<URL> notAcceptedURL = new ArrayList<>();

		System.out.println("---------------Initializing---------------");

//		for (int i = 0; i < urLs.length; i++) {
//			URL url = urLs[i];
//			String path = url.getPath();
//			ACCEPT_STATE acceptState = acceptPath(path);
//			boolean accept = acceptState == ACCEPT_STATE.ACCEPT;
//			if(acceptState == ACCEPT_STATE.NO_MATCH)
//				accept = configuration.acceptClasspath(url);
//
//			if (accept)
//				acceptedURL.add(url);
//			else
//				notAcceptedURL.add(url);
//		}
		ServiceLocator serviceLocator = ServiceLocator.getInstance();
		serviceLocator.setReflectionService(new ReflectionServiceImpl());
		serviceLocator.setConfigurator(configuration);

		String path = new File("webapp").getAbsolutePath();

//		File destinationDirectory = new File(configuration.getTargetDirectory());
//		File webappDirectory = new File(configuration.getTargetDirectory());
		File destinationDirectory = new File(path);
		File webappDirectory = new File(path);
		StandaloneDragomeAppGenerator gen = new StandaloneDragomeAppGenerator(destinationDirectory, webappDirectory, true, true, false);
		gen.execute(false);


//		final Classpath classPath= new Classpath();
//		for(int i = 0; i < acceptedURL.size(); i++) {
//			URL url = acceptedURL.get(i);
//			String file = url.getFile();
//			classPath.addEntry(file);
//		}


	}

	private static ACCEPT_STATE acceptPath(String path) {
		ACCEPT_STATE isValid = ACCEPT_STATE.NO_MATCH;

		if(path.contains("junit"))
			isValid = ACCEPT_STATE.NOT_ACCEPT;
		if(path.contains("hamcrest"))
			isValid = ACCEPT_STATE.NOT_ACCEPT;
		if(path.contains("javaagent-shaded"))
			isValid = ACCEPT_STATE.NOT_ACCEPT;
		if(path.contains("dragome-bytecode-js-compiler"))
			isValid = ACCEPT_STATE.NOT_ACCEPT;
		if(path.contains("gdx-jnigen"))
			isValid = ACCEPT_STATE.NOT_ACCEPT;
		if(path.contains("commons-io"))
			isValid = ACCEPT_STATE.NOT_ACCEPT;
		return isValid;
	}


	enum ACCEPT_STATE {
		ACCEPT, NOT_ACCEPT, NO_MATCH
	}
}
