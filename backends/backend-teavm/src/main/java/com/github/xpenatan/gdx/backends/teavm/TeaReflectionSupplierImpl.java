package com.github.xpenatan.gdx.backends.teavm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.teavm.classlib.ReflectionContext;
import org.teavm.classlib.ReflectionSupplier;
import org.teavm.model.ClassReader;
import org.teavm.model.FieldReader;
import org.teavm.model.MethodDescriptor;
import org.teavm.model.MethodReader;

import com.badlogic.gdx.assets.AssetManager;

public class TeaReflectionSupplierImpl implements ReflectionSupplier {

	public static ArrayList<Class> clazzList = new ArrayList();

	public TeaReflectionSupplierImpl() {

		clazzList.add(AssetManager.class);
	}

	@Override
	public Collection<String> getAccessibleFields(ReflectionContext context, String className) {
		ClassReader cls = context.getClassSource().get(className);
		Set<String> fields = new HashSet<>();
		System.out.println("className: " + className);
		if (cls != null) {
			for (FieldReader field : cls.getFields()) {

				for (int i = 0; i < clazzList.size(); i++) {
					Class clazz = clazzList.get(i);
					if (field.getAnnotations().get(clazz.getName()) != null) {
						fields.add(field.getName());
					}
				}
			}
		}
		return fields;
	}

	@Override
	public Collection<MethodDescriptor> getAccessibleMethods(ReflectionContext context, String className) {
		ClassReader cls = context.getClassSource().get(className);
		Set<MethodDescriptor> methods = new HashSet<>();
		System.out.println("className: " + className);
		if (cls != null) {
			Collection<? extends MethodReader> methods2 = cls.getMethods();
			for (MethodReader method : methods2) {
				for (int i = 0; i < clazzList.size(); i++) {
					Class clazz = clazzList.get(i);
					if (method.getAnnotations().get(clazz.getName()) != null) {
						methods.add(method.getDescriptor());
					}
				}
			}
		}
		return methods;
	}
}