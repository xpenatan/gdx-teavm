package com.github.xpenatan.gdx.backends.dragome;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import com.badlogic.gdx.utils.Array;
import com.dragome.services.ReflectionServiceImpl;
import com.dragome.services.ServiceLocator;
import com.dragome.web.helpers.serverside.StandaloneDragomeAppGenerator;
import com.github.xpenatan.gdx.backend.web.WebClassLoader;
import com.github.xpenatan.gdx.backend.web.preloader.AssetsCopy;

/**
 * @author xpenatan
 */
public class DragomeBuilder {

	public static void build(DragomeBuildConfiguration configuration) {
		DragomeGdxConfigurator gdxConfiguration = new DragomeGdxConfigurator(configuration);

		System.out.println("---------------Initializing---------------");

		URL[] urLs = ((URLClassLoader) (Thread.currentThread().getContextClassLoader())).getURLs();
//
//		ArrayList<URL> acceptedURL = new ArrayList<>();
//		ArrayList<URL> notAcceptedURL = new ArrayList<>();
//
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
//
//		for(int i = 0; i < acceptedURL.size(); i++) {
//			URL url = acceptedURL.get(i);
//			String string = url.toString();
//			if(string.contains("backend-web")) {
//				acceptedURL.remove(i);
//				acceptedURL.add(0, url);
//				break;
//			}
//		}
//
//		for(int i = 0; i < acceptedURL.size(); i++) {
//			URL url = acceptedURL.get(i);
//			String string = url.toString();
//			if(string.contains("backend-teavm")) {
//				acceptedURL.remove(i);
//				acceptedURL.add(0, url);
//				break;
//			}
//		}

		String targetDirectory = configuration.getWebAppPath();
		boolean setMinifying = configuration.minifying();
		String mainClass = configuration.getMainClass().getName();

		URL[] classPaths = new URL[0];
//		acceptedURL.toArray(classPaths);
		WebClassLoader classLoader = new WebClassLoader(urLs, DragomeBuilder.class.getClassLoader());

		String assetsOutputPath = targetDirectory + "\\assets";
		Array<File> paths = new Array<>();
		Array<String> classPathFiles = new Array<>();
		assetsDefaultClasspath(classPathFiles);
		boolean generateAssetPaths = configuration.assetsPath(paths);
		AssetsCopy.copy(classLoader, paths, classPathFiles, assetsOutputPath, generateAssetPaths);


		ServiceLocator serviceLocator = ServiceLocator.getInstance();
		serviceLocator.setReflectionService(new ReflectionServiceImpl());
		serviceLocator.setConfigurator(gdxConfiguration);

		File destinationDirectory = new File(targetDirectory);
		File webappDirectory = new File(targetDirectory);
		DragomeStandaloneAppGenerator gen = new DragomeStandaloneAppGenerator(destinationDirectory, webappDirectory, true, true, false);
		gen.execute(configuration.getMainClass());
	}

	private static void assetsDefaultClasspath (Array<String> filePath) {
		filePath.add("com/badlogic/gdx/graphics/g3d/particles/");
		filePath.add("com/badlogic/gdx/graphics/g3d/shaders/");
		filePath.add("com/badlogic/gdx/utils/arial-15.fnt"); // Cannot be utils folder for now because its trying to copy from emu folder and not core gdx classpath
		filePath.add("com/badlogic/gdx/utils/arial-15.png");
		filePath.add("scripts/soundmanager2-jsmin.js");
	}

	private static ACCEPT_STATE acceptPath(String path) {
		ACCEPT_STATE isValid = ACCEPT_STATE.NO_MATCH;
		if(path.contains("teavm-") && path.contains(".jar"))
			isValid = ACCEPT_STATE.NOT_ACCEPT;
		if(path.contains("junit"))
			isValid = ACCEPT_STATE.NOT_ACCEPT;
		if(path.contains("hamcrest"))
			isValid = ACCEPT_STATE.NOT_ACCEPT;
		if(path.contains("jackson-"))
			isValid = ACCEPT_STATE.NOT_ACCEPT;
		if(path.contains("gdx-jnigen"))
			isValid = ACCEPT_STATE.NOT_ACCEPT;
		if(path.contains("Java/jdk"))
			isValid = ACCEPT_STATE.NOT_ACCEPT;
		if(path.contains("commons-io"))
			isValid = ACCEPT_STATE.NOT_ACCEPT;
		if(path.contains("org/ow2"))
			isValid = ACCEPT_STATE.NOT_ACCEPT;
		if(path.contains("carrotsearch"))
			isValid = ACCEPT_STATE.NOT_ACCEPT;
		if(path.contains("google/code"))
			isValid = ACCEPT_STATE.NOT_ACCEPT;
		if(path.contains("jcraft"))
			isValid = ACCEPT_STATE.NOT_ACCEPT;
		if(path.contains("joda-time"))
			isValid = ACCEPT_STATE.NOT_ACCEPT;
		if(path.contains("mozilla"))
			isValid = ACCEPT_STATE.NOT_ACCEPT;
		return isValid;
	}

	enum ACCEPT_STATE {
		ACCEPT, NOT_ACCEPT, NO_MATCH
	}
}
