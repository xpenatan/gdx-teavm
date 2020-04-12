package com.github.xpenatan.gdx.backends.teavm;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import org.teavm.diagnostics.Problem;
import org.teavm.diagnostics.ProblemProvider;
import org.teavm.model.CallLocation;
import org.teavm.model.MethodReference;
import org.teavm.tooling.TeaVMTargetType;
import org.teavm.tooling.TeaVMTool;
import org.teavm.tooling.TeaVMToolException;


public class TeaBuilder {

	private static Logger LOGGER= Logger.getLogger(TeaBuilder.class.getName());

	private static final StringBuilder sb = new StringBuilder();

	public static void build(TeaConfiguration configuration) {

		URL[] urLs = ((URLClassLoader) (Thread.currentThread().getContextClassLoader())).getURLs();

		ArrayList<URL> acceptedURL = new ArrayList<>();
		ArrayList<URL> notAcceptedURL = new ArrayList<>();


		System.out.println("---------------Initializing---------------");

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

		logHeader("Accepted Libs ClassPath Order");

		for (int i = 0; i < acceptedURL.size(); i++) {
			sb.append(i + " true: " + acceptedURL.get(i).getPath());
			sb.append("\n");
		}

		logHeader("Not Accepted Libs ClassPath");

		for (int i = 0; i < notAcceptedURL.size(); i++) {
			sb.append(i + " false: " + notAcceptedURL.get(i).getPath());
			sb.append("\n");
		}

		int size = acceptedURL.size();

		if(size <= 0) {
			System.out.println("No urls found");
			return;
		}

		URL[] classPaths = new URL[size];
		acceptedURL.toArray(classPaths);
		ClassLoader classLoader = new URLClassLoader(classPaths, TeaBuilder.class.getClassLoader());

		TeaVMTool tool = new TeaVMTool();

		boolean setDebugInformationGenerated = false;
		boolean setSourceMapsFileGenerated = false;
		boolean setSourceFilesCopied = false;

		String targetDirectory = configuration.getTargetDirectory();

		System.out.println("targetDirectory: " + targetDirectory);

		File setTargetDirectory = new File(targetDirectory + "\\" + "teavm");
		String setTargetFileName = "app.js";
		boolean setMinifying = configuration.minifying();
		String mainClass = configuration.getMain();
		File setCacheDirectory = new File("C:\\TeaVMCache");;
		boolean setIncremental = false;

		tool.setClassLoader(classLoader);
		tool.setDebugInformationGenerated(setDebugInformationGenerated);
		tool.setSourceMapsFileGenerated(setSourceMapsFileGenerated);
		tool.setSourceFilesCopied(setSourceFilesCopied);
		tool.setTargetDirectory(setTargetDirectory);
		tool.setTargetFileName(setTargetFileName);
		tool.setMinifying(setMinifying);
//		tool.setRuntime(mapRuntime(configuration.getRuntime()));
		tool.setMainClass(mainClass);
		//		tool.getProperties().putAll(profile.getProperties());
		tool.setIncremental(setIncremental);
		tool.setCacheDirectory(setCacheDirectory);
		tool.setTargetType(TeaVMTargetType.JAVASCRIPT);
		Properties properties = tool.getProperties();

		properties.put("teavm.libgdx.fsJsonPath", targetDirectory + "\\" + "filesystem.json");
		properties.put("teavm.libgdx.warAssetsDirectory", targetDirectory + "\\" + "assets");


		String assetsOutputPath = targetDirectory + "\\assets";
		ArrayList<File> paths = new ArrayList<>();
		ArrayList<String> classPathFiles = new ArrayList<>();
		assetsDefaultClasspath(classPathFiles);
		configuration.assetsPath(paths);
//		AssetsCopy.copy(paths, classPathFiles, assetsOutputPath, false);


		try {
			tool.generate();
			ProblemProvider problemProvider = tool.getProblemProvider();
			List<Problem> problems = problemProvider.getProblems();


			if(problems.size() > 0) {

				logHeader("Compiler problems");

				for(int i = 0; i < problems.size(); i++) {
					Problem problem = problems.get(i);
					String text = problem.getText();
					CallLocation location = problem.getLocation();
					MethodReference method = location.getMethod();
					if(i > 0)
						sb.append("----\n");
					sb.append(i + " " + problem.getSeverity().toString() + "\n");
					sb.append("Class: " + method.getClassName() + "\n");
					sb.append("Method: " + method.getName() + "\n");
					sb.append("Text: " + text + "\n");
				}
			}
			if (!tool.wasCancelled()) {

			}
			else {

			}

		} catch (Throwable e) {
			e.printStackTrace();
		}

		sb.append("\n#################################################################");

		System.err.println(sb);
	}

	private static void logHeader(String text) {
		sb.append("\n" + "#################################################################\n");
		sb.append("#\n# " + text + "\n#");
		sb.append("\n" + "#################################################################\n\n");
	}

	private static void assetsDefaultClasspath (ArrayList<String> filePath) {

	}

	private static ACCEPT_STATE acceptPath(String path) {
		ACCEPT_STATE isValid = ACCEPT_STATE.NO_MATCH;

//		System.out.println("path 1: " + path);

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
		if(path.contains("/XpeTeaVM"))
			isValid = ACCEPT_STATE.NOT_ACCEPT;
		if(path.contains("test-"))
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
//		if(path.contains("gdx-"))
//			isValid = false;

//		isValid = true;

		return isValid;
	}

	enum ACCEPT_STATE {
		ACCEPT, NOT_ACCEPT, NO_MATCH
	}

//	private static RuntimeCopyOperation mapRuntime(TeaVMRunTime runtimeMode) {
//		switch (runtimeMode) {
//		case MERGE:
//			return RuntimeCopyOperation.MERGED;
//		case SEPARATE:
//			return RuntimeCopyOperation.SEPARATE;
//		default:
//			return RuntimeCopyOperation.NONE;
//		}
//	}

}
