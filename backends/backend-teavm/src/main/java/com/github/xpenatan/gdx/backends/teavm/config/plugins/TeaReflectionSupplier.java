package com.github.xpenatan.gdx.backends.teavm.config.plugins;

import com.github.xpenatan.gdx.backends.teavm.config.TeaBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.teavm.classlib.ReflectionContext;
import org.teavm.classlib.ReflectionSupplier;
import org.teavm.model.ClassReader;
import org.teavm.model.FieldReader;
import org.teavm.model.MethodDescriptor;
import org.teavm.model.MethodReader;

public class TeaReflectionSupplier implements ReflectionSupplier {

    private static ArrayList<String> clazzList = new ArrayList();

    private static HashSet<String> REFLECTION_CLASSES = new HashSet<>();

    public static void addReflectionClass(Class<?> type) {
        addReflectionClass(type.getName());
    }

    public static List<String> getReflectionClasses() {
        return clazzList;
    }

    public static boolean containsReflection(String className) {
        for(int i = 0; i < clazzList.size(); i++) {
            String reflectionClass = clazzList.get(i);
            if(className.contains(reflectionClass))
                return true;
        }
        return false;
    }

    /**
     * package path or package path with class name
     */
    public static void addReflectionClass(String className) {
        if(!clazzList.contains(className)) {
            clazzList.add(className);
        }
    }

    /**
     * Must be called after TeaBuilder.build
     */
    public static void printReflectionClasses() {
        TeaBuilder.logHeader("REFLECTION CLASSES: " + REFLECTION_CLASSES.size());
        for(String reflectionClass : REFLECTION_CLASSES) {
            TeaBuilder.log(reflectionClass);
        }
        TeaBuilder.logEnd();
    }

    public TeaReflectionSupplier() {
    }

    @Override
    public Collection<String> getAccessibleFields(ReflectionContext context, String className) {
        ClassReader cls = context.getClassSource().get(className);
        if(cls == null) {
            return Collections.emptyList();
        }
        Set<String> fields = new HashSet<>();

        if(cls != null) {
            if(canHaveReflection(className)) {
                REFLECTION_CLASSES.add(className);
                for(FieldReader field : cls.getFields()) {
                    String name = field.getName();
                    fields.add(name);
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
        if(canHaveReflection(className)) {
            REFLECTION_CLASSES.add(className);
            Collection<? extends MethodReader> methods2 = cls.getMethods();
            for(MethodReader method : methods2) {
                MethodDescriptor descriptor = method.getDescriptor();
                methods.add(descriptor);
            }
        }
        return methods;
    }

    @Override
    public boolean isClassFoundByName(ReflectionContext context, String name) {
        return canHaveReflection(name);
    }

    private boolean canHaveReflection(String className) {
        for(int i = 0; i < clazzList.size(); i++) {
            String name = clazzList.get(i);
            if(className.contains(name)) {
                return true;
            }
        }
        return false;
    }
}