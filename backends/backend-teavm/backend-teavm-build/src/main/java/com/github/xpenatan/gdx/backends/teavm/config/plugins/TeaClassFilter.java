package com.github.xpenatan.gdx.backends.teavm.config.plugins;

import java.util.ArrayList;
import org.teavm.model.FieldReference;
import org.teavm.model.MethodReference;
import org.teavm.vm.spi.ElementFilter;

public class TeaClassFilter implements ElementFilter {
    private static final ArrayList<String> classKeepFilter = new ArrayList();
    private static final ArrayList<String> classFilter = new ArrayList();
    private static final ArrayList<String> methodFilter = new ArrayList();
    private static final ArrayList<String> fieldFilter = new ArrayList();

    /**
     * my.package.ClassName
     */
    public static void addClassToKeep(String className) {
        classKeepFilter.add(className);
    }

    /**
     * my.package.ClassName or my.package
     */
    public static void addClassToExclude(String className, boolean excludeClass, boolean excludeMethod, boolean excludeField) {
        if(excludeClass) {
            classFilter.add(className);
        }
        if(excludeMethod) {
            methodFilter.add(className);
        }
        if(excludeField) {
            fieldFilter.add(className);
        }
    }

    /**
     * my.package.ClassName or my.package
     */
    public static void addMethodsToExclude(String className) {
        methodFilter.add(className);
    }

    /**
     * my.package.ClassName or my.package
     */
    public static void addFieldsToExclude(String className) {
        fieldFilter.add(className);
    }

    private static boolean containsClass(ArrayList<String> list, String className) {
        for(int i = 0; i < list.size(); i++) {
            String excludedClass = list.get(i);
            if(className.contains(excludedClass))
                return true;
        }
        return false;
    }

    @Override
    public boolean acceptClass(String fullClassName) {
        boolean accceptClass = true;
        if(containsClass(classFilter, fullClassName)) {
            if(!containsClass(classKeepFilter, fullClassName)) {
                accceptClass = false;
            }
        }
        return accceptClass;
    }

    @Override
    public boolean acceptMethod(MethodReference method) {
        String fullClassName = method.getClassName();
        if(containsClass(methodFilter, fullClassName)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean acceptField(FieldReference field) {
        String fullClassName = field.getClassName();
        if(containsClass(fieldFilter, fullClassName)) {
            return false;
        }
        return true;
    }
}
