package com.build;

import java.io.File;

import com.badlogic.gdx.jnigen.AntScriptGenerator;
import com.badlogic.gdx.jnigen.BuildConfig;
import com.badlogic.gdx.jnigen.BuildExecutor;
import com.badlogic.gdx.jnigen.BuildTarget;
import com.badlogic.gdx.jnigen.BuildTarget.TargetOs;
import com.badlogic.gdx.jnigen.parsing.RobustJavaMethodParser;
import com.badlogic.gdx.jnigen.NativeCodeGenerator;
import com.escplay.codegen.XpeCodeGen;

public class Build {
	
    static public void main (String[] args) throws Exception {
    	
    	boolean flag = true;
    	if(flag) {
    		// generate JS code
    		XpeCodeGen.generate("../gdx-bullet/src", "../gdx-bullet-dragome/src", new DragomeWrapper()); 
    		return;
    	}
    	
    	// generate C/C++ code
    	RobustJavaMethodParser.CustomIgnoreTag = "[";
		new NativeCodeGenerator().generate("../gdx-bullet/src", "../gdx-bullet/bin" + File.pathSeparator + "../../../../../../Libgdx/gdx/bin", "jni");

		String cppFlags = "";
		cppFlags += " -fno-strict-aliasing";
		cppFlags += " -fno-rtti";
		cppFlags += " -DBT_NO_PROFILE";

		String[] excludes = { "src/bullet/BulletMultiThreaded/GpuSoftBodySolvers/**", "emscripten/**"};
		String[] headers = { "src/bullet/", "src/custom/", "src/extras/Serialize/" };

		// generate build scripts
		BuildTarget win32home = BuildTarget.newDefaultTarget(TargetOs.Windows, false);
		win32home.compilerPrefix = "";
		win32home.buildFileName = "build-windows32home.xml";
		win32home.excludeFromMasterBuildFile = true;
		win32home.cExcludes = win32home.cppExcludes = excludes;
		win32home.headerDirs = headers;
		win32home.cppFlags += cppFlags;

		BuildTarget win32 = BuildTarget.newDefaultTarget(TargetOs.Windows, false);
		win32.cExcludes = win32.cppExcludes = excludes;
		win32.headerDirs = headers;
		win32.cppFlags += cppFlags;

		BuildTarget win64 = BuildTarget.newDefaultTarget(TargetOs.Windows, true);
		win64.cExcludes = win64.cppExcludes = excludes;
		win64.headerDirs = headers;
		win64.cppFlags += cppFlags;

		BuildTarget lin32 = BuildTarget.newDefaultTarget(TargetOs.Linux, false);
		lin32.cExcludes = lin32.cppExcludes = excludes;
		lin32.headerDirs = headers;
		lin32.cppFlags += cppFlags;

		BuildTarget lin64 = BuildTarget.newDefaultTarget(TargetOs.Linux, true);
		lin64.cExcludes = lin64.cppExcludes = excludes;
		lin64.headerDirs = headers;
		lin64.cppFlags += cppFlags;

		BuildTarget mac = BuildTarget.newDefaultTarget(TargetOs.MacOsX, false);
		mac.cExcludes = mac.cppExcludes = excludes;
		mac.headerDirs = headers;
		mac.cppFlags += cppFlags;

		BuildTarget mac64 = BuildTarget.newDefaultTarget(TargetOs.MacOsX, true);
		mac64.cExcludes = mac.cppExcludes = excludes;
		mac64.headerDirs = headers;
		mac64.cppFlags += cppFlags;

		BuildTarget android = BuildTarget.newDefaultTarget(TargetOs.Android, false);
		android.cExcludes = android.cppExcludes = excludes;
		android.headerDirs = headers;
		android.cppFlags += cppFlags + " -fexceptions";

		BuildTarget ios = BuildTarget.newDefaultTarget(TargetOs.IOS, false);
		ios.cExcludes = ios.cppExcludes = excludes;
		ios.headerDirs = headers;
		ios.cppFlags += cppFlags;

		new AntScriptGenerator().generate(new BuildConfig("gdx-bullet", "target", "natives", "jni"), win32home, win32, win64, lin32, lin64, mac, mac64, android, ios);
//		new FileHandle(new File("jni/Application.mk")).writeString("\nAPP_STL := stlport_static\n", true);
		
		boolean success = true;
		
//		if(success)
//			success = BuildExecutor.executeAnt("jni/build-windows64.xml", "-v -Dhas-compiler=true clean postcompile");
		if(success)
			success = BuildExecutor.executeAnt("jni/build-windows32.xml", "-v -Dhas-compiler=true postcompile");
		
		if(success)
			BuildExecutor.executeAnt("jni/build.xml", "-v pack-natives");
    }
}