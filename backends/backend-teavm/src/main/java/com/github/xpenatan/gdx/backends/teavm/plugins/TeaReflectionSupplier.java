package com.github.xpenatan.gdx.backends.teavm.plugins;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.teavm.classlib.ReflectionContext;
import org.teavm.classlib.ReflectionSupplier;
import org.teavm.model.ClassReader;
import org.teavm.model.FieldReader;
import org.teavm.model.MethodDescriptor;
import org.teavm.model.MethodReader;

import com.badlogic.gdx.assets.AssetManager;

public class TeaReflectionSupplier implements ReflectionSupplier {

	private static ArrayList<Class> clazzList = new ArrayList();

	public static void addReflectionClass(Class<?> type) {
		clazzList.add(type);
	}

	public TeaReflectionSupplier() {

		clazzList.add(AssetManager.class);
	}

	@Override
	public Collection<String> getAccessibleFields(ReflectionContext context, String className) {
		ClassReader cls = context.getClassSource().get(className);
		if(cls == null) {
			return Collections.emptyList();
		}
		Set<String> fields = new HashSet<>();

		if (cls != null) {
			String name = cls.getName();
			boolean flag = false;
			for (int i = 0; i < clazzList.size(); i++) {
				Class clazz = clazzList.get(i);
				String name2 = clazz.getName();
				if (name.equals(name2)) {
					flag = true;
					break;
				}
			}

			if(flag) {
				for (FieldReader field : cls.getFields()) {
					fields.add(field.getName());
				}
			}
		}
		return fields;
	}

	@Override
	public Collection<MethodDescriptor> getAccessibleMethods(ReflectionContext context, String className) {
		ClassReader cls = context.getClassSource().get(className);
		if(cls == null) {
			return Collections.emptyList();
		}
		Set<MethodDescriptor> methods = new HashSet<>();
		if (cls != null) {
			String name = cls.getName();
			boolean flag = false;
			for (int i = 0; i < clazzList.size(); i++) {
				Class clazz = clazzList.get(i);
				String name2 = clazz.getName();
				if (name.equals(name2)) {
					flag = true;
					break;
				}
			}

			if(flag) {
				Collection<? extends MethodReader> methods2 = cls.getMethods();
				for (MethodReader method : methods2) {
					methods.add(method.getDescriptor());
				}
			}
		}
		return methods;
	}
}