package com.github.xpenatan.gdx.backends.teavm.plugins;

import org.teavm.model.FieldReference;
import org.teavm.model.MethodReference;
import org.teavm.vm.spi.ElementFilter;

import java.util.ArrayList;

public class TeaClassFilter implements ElementFilter {

	private static ArrayList<String> clazzList = new ArrayList();

	/**
	 * my.package.ClassName
	 * or
	 * my.package
	 */
	public static void addClassToExclude(String className) {
		clazzList.add(className);
	}

	private static boolean containsClass(String className) {
		for(int i = 0; i < clazzList.size(); i++) {
			String excludedClass = clazzList.get(i);
			if(className.contains(excludedClass))
				return true;
		}
		return false;
	}

	@Override
	public boolean acceptClass (String fullClassName) {
		if(containsClass(fullClassName)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean acceptMethod (MethodReference method) {
		return true;
	}

	@Override
	public boolean acceptField (FieldReference field) {
		return true;
	}
}
