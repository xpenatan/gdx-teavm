package com.github.xpenatan.gdx.backends.teavm;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.utils.Array;
import com.github.xpenatan.gdx.backend.web.WebBuildConfiguration;
import com.github.xpenatan.gdx.backend.web.WebClassLoader;
import com.github.xpenatan.gdx.backend.web.preloader.AssetsCopy;
import com.github.xpenatan.gdx.backends.teavm.plugins.TeaClassTransformer;
import com.github.xpenatan.gdx.backends.teavm.plugins.TeaReflectionSupplier;
import org.teavm.diagnostics.Problem;
import org.teavm.diagnostics.ProblemProvider;
import org.teavm.model.CallLocation;
import org.teavm.model.MethodReference;
import org.teavm.tooling.TeaVMTargetType;
import org.teavm.tooling.TeaVMTool;
import org.teavm.vm.TeaVMOptimizationLevel;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Properties;


/**
 * @author xpenatan
 */
public class TeaBuilder {

	public static void build(WebBuildConfiguration configuration) {
		addDefaultReflectionClasses();

		URL[] urLs = ((URLClassLoader) (Thread.currentThread().getContextClassLoader())).getURLs();

		Array<URL> acceptedURL = new Array<>();
		Array<URL> notAcceptedURL = new Array<>();

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

		for(int i = 0; i < acceptedURL.size; i++) {
			URL url = acceptedURL.get(i);
			String string = url.toString();
			if(string.contains("backend-web")) {
				acceptedURL.removeIndex(i);
				acceptedURL.insert(0, url);
				break;
			}
		}

		// Make backend-teavm first so some classes are replaced by emulated classes
		for(int i = 0; i < acceptedURL.size; i++) {
			URL url = acceptedURL.get(i);
			String string = url.toString();
			if(string.contains("backend-teavm")) {
				acceptedURL.removeIndex(i);
				acceptedURL.insert(0, url);
				break;
			}
		}

		acceptedURL.addAll(configuration.getAdditionalClasspath());

		WebBuildConfiguration.logHeader("Accepted Libs ClassPath Order");

		for (int i = 0; i < acceptedURL.size; i++) {
			WebBuildConfiguration.log(i + " true: " + acceptedURL.get(i).getPath());
		}

		WebBuildConfiguration.logHeader("Not Accepted Libs ClassPath");

		for (int i = 0; i < notAcceptedURL.size; i++) {
			WebBuildConfiguration.log(i + " false: " + notAcceptedURL.get(i).getPath());
		}

		int size = acceptedURL.size;

		if(size <= 0) {
			System.out.println("No urls found");
			return;
		}

		URL[] classPaths = acceptedURL.toArray(URL.class);
		WebClassLoader classLoader = new WebClassLoader(classPaths, TeaBuilder.class.getClassLoader());

		TeaVMTool tool = new TeaVMTool();

		boolean setDebugInformationGenerated = false;
		boolean setSourceMapsFileGenerated = false;
		boolean setSourceFilesCopied = false;

		String webappDirectory = configuration.getWebAppPath();

		String webappName = "webapp";

		System.out.println("targetDirectory: " + webappDirectory);

		File setTargetDirectory = new File(webappDirectory + "\\" + webappName + "\\" + "teavm");
		String setTargetFileName = "app.js";
		boolean setMinifying = configuration.minifying();
		String mainClass = configuration.getMainClass();
		TeaClassTransformer.applicationListener = configuration.getApplicationListenerClass();

		File setCacheDirectory = new File("C:\\TeaVMCache");;
		boolean setIncremental = false;

		tool.setClassLoader(classLoader);
		tool.setDebugInformationGenerated(setDebugInformationGenerated);
		tool.setSourceMapsFileGenerated(setSourceMapsFileGenerated);
		tool.setSourceFilesCopied(setSourceFilesCopied);
		tool.setTargetDirectory(setTargetDirectory);
		tool.setTargetFileName(setTargetFileName);
		tool.setObfuscated(setMinifying);
		tool.setOptimizationLevel(TeaVMOptimizationLevel.SIMPLE);
//		tool.setRuntime(mapRuntime(configuration.getRuntime()));
		tool.setMainClass(mainClass);
		//		tool.getProperties().putAll(profile.getProperties());
		tool.setIncremental(setIncremental);
		tool.setCacheDirectory(setCacheDirectory);
		tool.setStrict(false);
		tool.setTargetType(TeaVMTargetType.JAVASCRIPT);
		Properties properties = tool.getProperties();

		properties.put("teavm.libgdx.fsJsonPath", webappDirectory + "\\" + webappName + "\\" + "filesystem.json");
		properties.put("teavm.libgdx.warAssetsDirectory", webappDirectory + "\\" + webappName + "\\" + "assets");

		Array<String> webappAssetsFiles = new Array<>();
		webappAssetsFiles.add(webappName);
		AssetsCopy.copy(classLoader, webappAssetsFiles, new Array<>(), webappDirectory, false);

		WebBuildConfiguration.logHeader("Copying Assets");

		String assetsOutputPath = webappDirectory + "\\" + webappName + "\\assets";
		Array<File> assetsPaths = new Array<>();
		Array<String> classPathAssetsFiles = new Array<>();
		assetsDefaultClasspath(classPathAssetsFiles);
		boolean generateAssetPaths = configuration.assetsPath(assetsPaths);
		AssetsCopy.copy(classLoader, classPathAssetsFiles, assetsPaths, assetsOutputPath, generateAssetPaths);

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

	private static void addDefaultReflectionClasses() {
		TeaReflectionSupplier.addReflectionClass(Button.ButtonStyle.class);
		TeaReflectionSupplier.addReflectionClass(TextButton.TextButtonStyle.class);
		TeaReflectionSupplier.addReflectionClass(ScrollPane.ScrollPaneStyle.class);
		TeaReflectionSupplier.addReflectionClass(SelectBox.SelectBoxStyle.class);
		TeaReflectionSupplier.addReflectionClass(com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle.class);
		TeaReflectionSupplier.addReflectionClass(SplitPane.SplitPaneStyle.class);
		TeaReflectionSupplier.addReflectionClass(Window.WindowStyle.class);
		TeaReflectionSupplier.addReflectionClass(ProgressBar.ProgressBarStyle.class);
		TeaReflectionSupplier.addReflectionClass(Label.LabelStyle.class);
		TeaReflectionSupplier.addReflectionClass(TextField.TextFieldStyle.class);
		TeaReflectionSupplier.addReflectionClass(CheckBox.CheckBoxStyle.class);
		TeaReflectionSupplier.addReflectionClass(Touchpad.TouchpadStyle.class);
		TeaReflectionSupplier.addReflectionClass(Tree.TreeStyle.class);
		TeaReflectionSupplier.addReflectionClass(TextTooltip.TextTooltipStyle.class);
		TeaReflectionSupplier.addReflectionClass(InputEvent.class);
		TeaReflectionSupplier.addReflectionClass(GlyphLayout.GlyphRun.class);
		TeaReflectionSupplier.addReflectionClass(Color.class);
		TeaReflectionSupplier.addReflectionClass(Array.class);
		TeaReflectionSupplier.addReflectionClass(ChangeListener.ChangeEvent.class);
		TeaReflectionSupplier.addReflectionClass(GlyphLayout.class);
		TeaReflectionSupplier.addReflectionClass(FocusListener.FocusEvent.class);
		TeaReflectionSupplier.addReflectionClass(Rectangle.class);
		TeaReflectionSupplier.addReflectionClass(Stage.TouchFocus.class);

		TeaReflectionSupplier.addReflectionClass(VisibleAction.class);
		TeaReflectionSupplier.addReflectionClass(TouchableAction.class);
		TeaReflectionSupplier.addReflectionClass(TimeScaleAction.class);
		TeaReflectionSupplier.addReflectionClass(TemporalAction.class);
		TeaReflectionSupplier.addReflectionClass(SizeToAction.class);
		TeaReflectionSupplier.addReflectionClass(SizeByAction.class);
		TeaReflectionSupplier.addReflectionClass(SequenceAction.class);
		TeaReflectionSupplier.addReflectionClass(ScaleToAction.class);
		TeaReflectionSupplier.addReflectionClass(ScaleByAction.class);
		TeaReflectionSupplier.addReflectionClass(RunnableAction.class);
		TeaReflectionSupplier.addReflectionClass(RotateToAction.class);
		TeaReflectionSupplier.addReflectionClass(RotateByAction.class);
		TeaReflectionSupplier.addReflectionClass(RepeatAction.class);
		TeaReflectionSupplier.addReflectionClass(RemoveListenerAction.class);
		TeaReflectionSupplier.addReflectionClass(RemoveActorAction.class);
		TeaReflectionSupplier.addReflectionClass(RemoveAction.class);
		TeaReflectionSupplier.addReflectionClass(RelativeTemporalAction.class);
		TeaReflectionSupplier.addReflectionClass(ParallelAction.class);
		TeaReflectionSupplier.addReflectionClass(MoveToAction.class);
		TeaReflectionSupplier.addReflectionClass(MoveByAction.class);
		TeaReflectionSupplier.addReflectionClass(LayoutAction.class);
		TeaReflectionSupplier.addReflectionClass(IntAction.class);
		TeaReflectionSupplier.addReflectionClass(FloatAction.class);
		TeaReflectionSupplier.addReflectionClass(EventAction.class);
		TeaReflectionSupplier.addReflectionClass(DelegateAction.class);
		TeaReflectionSupplier.addReflectionClass(DelayAction.class);
		TeaReflectionSupplier.addReflectionClass(CountdownEventAction.class);
		TeaReflectionSupplier.addReflectionClass(ColorAction.class);
		TeaReflectionSupplier.addReflectionClass(AlphaAction.class);
		TeaReflectionSupplier.addReflectionClass(AfterAction.class);
		TeaReflectionSupplier.addReflectionClass(AddListenerAction.class);
		TeaReflectionSupplier.addReflectionClass(AddAction.class);
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
		if(path.contains("jutils"))
			isValid = ACCEPT_STATE.NOT_ACCEPT;
		if(path.contains("jutils"))
			isValid = ACCEPT_STATE.NOT_ACCEPT;
		if(path.contains("jinput-"))
			isValid = ACCEPT_STATE.NOT_ACCEPT;
		if(path.contains("lwjgl"))
			isValid = ACCEPT_STATE.NOT_ACCEPT;
		if(path.contains("jlayer-"))
			isValid = ACCEPT_STATE.NOT_ACCEPT;
		if(path.contains("gdx-platform"))
			isValid = ACCEPT_STATE.NOT_ACCEPT;
		if(path.contains("imgui-"))
			isValid = ACCEPT_STATE.NOT_ACCEPT;
		if(path.contains("imgui-gdx"))
			isValid = ACCEPT_STATE.NOT_ACCEPT;
		if(path.contains("/classes"))
			isValid = ACCEPT_STATE.NOT_ACCEPT;
		if(path.contains("/resources"))
			isValid = ACCEPT_STATE.NOT_ACCEPT;
		if(path.contains("javax"))
			isValid = ACCEPT_STATE.NOT_ACCEPT;

		if(path.contains("backend-teavm-"))
			isValid = ACCEPT_STATE.ACCEPT;

		return isValid;
	}

	enum ACCEPT_STATE {
		ACCEPT, NOT_ACCEPT, NO_MATCH
	}
}
