package com.github.xpenatan.gdx.backends.teavm;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.teavm.diagnostics.Problem;
import org.teavm.diagnostics.ProblemProvider;
import org.teavm.model.CallLocation;
import org.teavm.model.MethodReference;
import org.teavm.tooling.TeaVMTargetType;
import org.teavm.tooling.TeaVMTool;

import com.badlogic.gdx.utils.Array;
import com.github.xpenatan.gdx.backend.web.WebBuildConfiguration;
import com.github.xpenatan.gdx.backend.web.WebClassLoader;
import com.github.xpenatan.gdx.backend.web.preloader.AssetsCopy;


/**
 * @author xpenatan
 */
public class TeaBuilder {

	public static void build(TeaBuildConfiguration configuration) {

		URL[] urLs = ((URLClassLoader) (Thread.currentThread().getContextClassLoader())).getURLs();

		ArrayList<URL> acceptedURL = new ArrayList<>();
		ArrayList<URL> notAcceptedURL = new ArrayList<>();

		for (int i = 0; i < urLs.length; i++) {
			URL url = urLs[i];
			String path = url.getPath();
			ACCEPT_STATE acceptState = acceptPath(path);
			boolean accept = acceptState == ACCEPT_STATE.ACCEPT;
			if(acceptState == ACCEPT_STATE.NO_MATCH)
				accept = configuration.acceptClasspath(url);

			if (accept)
				acceptedURL.add(url);
			else
				notAcceptedURL.add(url);
		}

		for(int i = 0; i < acceptedURL.size(); i++) {
			URL url = acceptedURL.get(i);
			String string = url.toString();
			if(string.contains("backend-web")) {
				acceptedURL.remove(i);
				acceptedURL.add(0, url);
				break;
			}
		}

		WebBuildConfiguration.logHeader("Accepted Libs ClassPath Order");

		for (int i = 0; i < acceptedURL.size(); i++) {
			WebBuildConfiguration.log(i + " true: " + acceptedURL.get(i).getPath());
		}

		WebBuildConfiguration.logHeader("Not Accepted Libs ClassPath");

		for (int i = 0; i < notAcceptedURL.size(); i++) {
			WebBuildConfiguration.log(i + " false: " + notAcceptedURL.get(i).getPath());
		}

		int size = acceptedURL.size();

		if(size <= 0) {
			System.out.println("No urls found");
			return;
		}

		URL[] classPaths = new URL[size];
		acceptedURL.toArray(classPaths);
		WebClassLoader classLoader = new WebClassLoader(classPaths, TeaBuilder.class.getClassLoader());

		TeaVMTool tool = new TeaVMTool();

		boolean setDebugInformationGenerated = false;
		boolean setSourceMapsFileGenerated = false;
		boolean setSourceFilesCopied = false;

		String targetDirectory = configuration.getWebAppPath();

		System.out.println("targetDirectory: " + targetDirectory);

		File setTargetDirectory = new File(targetDirectory + "\\" + "teavm");
		String setTargetFileName = "app.js";
		boolean setMinifying = configuration.minifying();
		String mainClass = configuration.getMainClass().getName();
		File setCacheDirectory = new File("C:\\TeaVMCache");;
		boolean setIncremental = false;

		tool.setClassLoader(classLoader);
		tool.setDebugInformationGenerated(setDebugInformationGenerated);
		tool.setSourceMapsFileGenerated(setSourceMapsFileGenerated);
		tool.setSourceFilesCopied(setSourceFilesCopied);
		tool.setTargetDirectory(setTargetDirectory);
		tool.setTargetFileName(setTargetFileName);
		tool.setObfuscated(setMinifying);
//		tool.setRuntime(mapRuntime(configuration.getRuntime()));
		tool.setMainClass(mainClass);
		//		tool.getProperties().putAll(profile.getProperties());
		tool.setIncremental(setIncremental);
		tool.setCacheDirectory(setCacheDirectory);
		tool.setTargetType(TeaVMTargetType.JAVASCRIPT);
		Properties properties = tool.getProperties();

		properties.put("teavm.libgdx.fsJsonPath", targetDirectory + "\\" + "filesystem.json");
		properties.put("teavm.libgdx.warAssetsDirectory", targetDirectory + "\\" + "assets");


		WebBuildConfiguration.logHeader("Copying Assets");
		
		String assetsOutputPath = targetDirectory + "\\assets";
		Array<File> paths = new Array<>();
		Array<String> classPathFiles = new Array<>();
		assetsDefaultClasspath(classPathFiles);
		boolean generateAssetPaths = configuration.assetsPath(paths);
		AssetsCopy.copy(classLoader, paths, classPathFiles, assetsOutputPath, generateAssetPaths);

		try {
			tool.generate();
			ProblemProvider problemProvider = tool.getProblemProvider();
			List<Problem> problems = problemProvider.getProblems();


			if(problems.size() > 0) {

				WebBuildConfiguration.logHeader("Compiler problems");

				for(int i = 0; i < problems.size(); i++) {
					Problem problem = problems.get(i);
					String text = problem.getText();
					CallLocation location = problem.getLocation();
					MethodReference method = location != null ? location.getMethod() : null;
					if(i > 0)
						WebBuildConfiguration.log("----\n");
					WebBuildConfiguration.log(i + " " + problem.getSeverity().toString() + "\n");
					if(method != null) {
						WebBuildConfiguration.log("Class: " + method.getClassName() + "\n");
						WebBuildConfiguration.log("Method: " + method.getName() + "\n");
					}
					WebBuildConfiguration.log("Text: " + text + "\n");
				}
				WebBuildConfiguration.logEnd();
			}
			else {
				WebBuildConfiguration.logHeader("Build Complete");
			}

		} catch (Throwable e) {
			e.printStackTrace();
		}

		WebBuildConfiguration.flush();
	}

	private static void assetsDefaultClasspath (Array<String> filePath) {
		filePath.add("com/badlogic/gdx/graphics/g3d/particles/");
		filePath.add("com/badlogic/gdx/graphics/g3d/shaders/");
		filePath.add("com/badlogic/gdx/utils/arial-15.fnt"); // Cannot be utils folder for now because its trying to copy from emu folder and not core gdx classpath
		filePath.add("com/badlogic/gdx/utils/arial-15.png");
//		filePath.add("soundmanager2-jsmin.js");
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
