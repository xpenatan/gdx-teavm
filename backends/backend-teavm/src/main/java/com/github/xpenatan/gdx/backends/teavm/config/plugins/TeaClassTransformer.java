package com.github.xpenatan.gdx.backends.teavm.config.plugins;

import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.teavm.model.AnnotationContainer;
import org.teavm.model.AnnotationHolder;
import org.teavm.model.AnnotationValue;
import org.teavm.model.ClassHierarchy;
import org.teavm.model.ClassHolder;
import org.teavm.model.ClassHolderTransformer;
import org.teavm.model.ClassHolderTransformerContext;
import org.teavm.model.ClassReader;
import org.teavm.model.ClassReaderSource;
import org.teavm.model.ElementModifier;
import org.teavm.model.FieldHolder;
import org.teavm.model.FieldReader;
import org.teavm.model.MethodDescriptor;
import org.teavm.model.MethodHolder;
import org.teavm.model.MethodReader;
import org.teavm.model.ReferenceCache;
import org.teavm.model.emit.ProgramEmitter;
import org.teavm.model.util.ModelUtils;
import org.teavm.parsing.ClassRefsRenamer;

/**
 * @author xpenatan
 */
public class TeaClassTransformer implements ClassHolderTransformer {

    private boolean init = false;

    public static String applicationListener = "";
    public static String mainClass = "";
    public static Reflections reflections = new Reflections("emulate", "emu", "com", "org", "emulate/net");

    private HashMap<String, Class<?>> emulations = new HashMap<>();
    private HashMap<String, String> emulations2 = new HashMap<>();
    private HashMap<String, Class<?>> updateCode = new HashMap<>();
    private ReferenceCache referenceCache = new ReferenceCache();

    public TeaClassTransformer() {
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Emulate.class);
        for(Class<?> type : annotated) {
            Emulate em = type.getAnnotation(Emulate.class);
            if(em == null) {
                continue;
            }
            Class<?> toEmulate = em.value();
            String typeName = null;
            if(toEmulate == Object.class) {
                typeName = em.valueStr();
            }
            else {
                typeName = toEmulate.getTypeName();
            }
            typeName = typeName.trim();
            if(!typeName.isEmpty()) {
                emulations2.put(type.getName(), typeName);
                if(em.updateCode()) {
                    updateCode.put(typeName, type);
                }
                else {
                    emulations.put(typeName, type);
                }
            }
        }
    }

    @Override
    public void transformClass(ClassHolder cls, ClassHolderTransformerContext context) {
        ClassReaderSource innerSource = context.getHierarchy().getClassSource();
        if(!init) {
            init = true;
            ClassHolder classHolder = null;
            if(!applicationListener.isEmpty()) {
                classHolder = findClassHolder(cls, context, TeaClassTransformer.mainClass);
                setGdxApplicationClass(classHolder, context, TeaClassTransformer.applicationListener);
            }
        }

        emulateClass(context, cls, innerSource);
    }

    private void emulateClass(ClassHolderTransformerContext context, ClassHolder cls, ClassReaderSource innerSource) {
        String className = cls.getName();
        if(updateCode.containsKey(className)) {
            ClassReader original = innerSource.get(className);
            Class<?> emulated = updateCode.get(className);
            ClassReader emulatedClassHolder = innerSource.get(emulated.getName());
            if(emulatedClassHolder != null) {
                replaceClassCode(innerSource, emulated, cls, emulatedClassHolder);

                // teavm makes bunch of classreader copies. We also need to update the original class
                replaceClassCode(innerSource, emulated, (ClassHolder)original, emulatedClassHolder);
            }
        }
        else if(emulations.containsKey(className)) {
            ClassReader original = innerSource.get(className);
            Class<?> emulated = emulations.get(className);
            ClassReader emulatedClassHolder = innerSource.get(emulated.getName());
            if(emulatedClassHolder != null) {
                replaceClass(innerSource, cls, emulatedClassHolder);

                // teavm makes bunch of classreader copies. We also need to update the original class
                replaceClass(innerSource, (ClassHolder)original, emulatedClassHolder);
            }
        }
    }

    private void setGdxApplicationClass(ClassHolder classHolder, ClassHolderTransformerContext context, String entryPoint) {
        MethodHolder method = null;
        for(MethodHolder methodHolder : classHolder.getMethods()) {
            MethodDescriptor descriptor = methodHolder.getDescriptor();
            String string = descriptor.toString();
            if(string.contains("getApplicationListener()")) {
                method = methodHolder;
                break;
            }
        }
        if(method != null) {
            EnumSet<ElementModifier> modifiers = method.getModifiers();
            modifiers.remove(ElementModifier.NATIVE);
            ClassHierarchy hierarchy = context.getHierarchy();
            ProgramEmitter pe = ProgramEmitter.create(method, hierarchy);
            pe.construct(entryPoint).cast(Object.class).returnValue();
        }
    }

    private MethodHolder getMethodHolder(ClassHolder classHolder, String matchText) {
        Collection<MethodHolder> methods = classHolder.getMethods();
        Iterator<MethodHolder> iterator = methods.iterator();
        while(iterator.hasNext()) {
            MethodHolder methodHolder = iterator.next();
            MethodDescriptor descriptor = methodHolder.getDescriptor();
            String string = descriptor.toString();
            if(string.contains(matchText)) {
                return methodHolder;
            }
        }
        return null;
    }

    private ClassHolder findClassHolder(ClassHolder cur, ClassHolderTransformerContext context, Class clazz) {
        return findClassHolder(cur, context, clazz.getName());
    }

    private ClassHolder findClassHolder(ClassHolder cur, ClassHolderTransformerContext context, String clazz) {
        if(cur.getName().equals(clazz))
            return cur;
        ClassReaderSource innerSource = context.getHierarchy().getClassSource();
        ClassReader classReader = innerSource.get(clazz);
        ClassHolder classHolder = (ClassHolder)classReader;
        return classHolder;
    }

    private void setFieldAnnotation(ClassHolder classHolder, Class annotationClass, String fieldStr, String value) {
        for(FieldHolder field : classHolder.getFields().toArray(new FieldHolder[0])) {
            String name = field.getName();
            if(name.equals(fieldStr)) {
                AnnotationContainer annotations = field.getAnnotations();
                AnnotationHolder annotation = new AnnotationHolder(annotationClass.getName());
                if(value != null) {
                    annotation.getValues().put("value", new AnnotationValue(value));
                }
                annotations.add(annotation);
            }
        }
    }

    private void setMethodAnnotation(ClassHolder classHolder, Class annotationClass, String methodStr, String value) {
        for(MethodHolder method : classHolder.getMethods().toArray(new MethodHolder[0])) {
            String name = method.getName();
            if(name.equals(methodStr)) {
                AnnotationContainer annotations = method.getAnnotations();
                AnnotationHolder annotation = new AnnotationHolder(annotationClass.getName());
                if(value != null) {
                    annotation.getValues().put("value", new AnnotationValue(value));
                }
                annotations.add(annotation);
            }
        }
    }

    private void setClassInterface(ClassHolder classHolder, Class annotationClass) {
        Set<String> interfaces = classHolder.getInterfaces();
        interfaces.add(annotationClass.getName());
    }

    private void setClassAnnotation(ClassHolder classHolder, Class annotationClass) {
        AnnotationContainer annotations = classHolder.getAnnotations();
        AnnotationHolder annotation = new AnnotationHolder(annotationClass.getName());
        annotations.add(annotation);
    }

    private void replaceClassCode(ClassReaderSource innerSource, Class<?> emulated, final ClassHolder cls, final ClassReader emuCls) {
        Predicate<AnnotatedElement> annotatedElementPredicate = ReflectionUtils.withAnnotation(Emulate.class);
        Set<Field> fields = ReflectionUtils.getFields(emulated, annotatedElementPredicate);
        Set<Method> methods = ReflectionUtils.getMethods(emulated, annotatedElementPredicate);

        ClassRefsRenamer renamer = new ClassRefsRenamer(referenceCache, preimage -> {
            String newName = emulations2.get(preimage);
            if(newName != null) {
                return newName;
            }
            else {
                return preimage;
            }
        });

        Set<String> interfaces = emuCls.getInterfaces();
        Iterator<String> interfaceIt = interfaces.iterator();
        while(interfaceIt.hasNext()) {
            String interfaceStr = interfaceIt.next();
            ClassReader interfaceClassHolder = innerSource.get(interfaceStr);
            if(interfaceClassHolder != null) {
                ClassHolder classHolder = ModelUtils.copyClass(interfaceClassHolder);
                ClassHolder interfaceRename = renamer.rename(classHolder);
                String name = interfaceRename.getName();
                cls.getInterfaces().add(name);
            }
        }

//
//        if(emulatedClassName.contains("FloatBufferDirect")) {
//
//            String parent = emuCls.getParent();
//            if(parent != null && !parent.isEmpty()) {
//                ClassReader parentClassHolder = innerSource.get(parent);
//                if(parentClassHolder != null) {
//                    ClassHolder classHolder = ModelUtils.copyClass(parentClassHolder);
//                    ClassHolder parentClassHolderRename = renamer.rename(classHolder);
//                    String name = parentClassHolderRename.getName();
//                    cls.setParent(name);
//                }
//            }
//
//        }
        for(Field field : fields) {
            String emuFieldName = field.getName();
            FieldReader emulatedField = emuCls.getField(emuFieldName);
            FieldHolder originalField = cls.getField(emuFieldName);
            if(originalField != null) {
                cls.removeField(originalField);
            }
            FieldHolder fieldHolder = ModelUtils.copyField(emulatedField);
            FieldHolder fieldRename = renamer.rename(fieldHolder);
            cls.addField(fieldRename);
        }

        for(Method method : methods) {
            Class[] classes = new Class[method.getParameterTypes().length + 1];
            classes[classes.length - 1] = method.getReturnType();
            System.arraycopy(method.getParameterTypes(), 0, classes, 0, method.getParameterTypes().length);
            MethodDescriptor methodDescriptor = new MethodDescriptor(method.getName(), classes);
            MethodReader emulatedMethodReader = emuCls.getMethod(methodDescriptor);
            MethodHolder methodHolderCopy = ModelUtils.copyMethod(emulatedMethodReader);
            MethodHolder methodRename = renamer.rename(methodHolderCopy);
            MethodDescriptor descriptor = methodRename.getDescriptor();
            MethodHolder originalMethod = cls.getMethod(descriptor);
            if(originalMethod != null) {
                cls.removeMethod(originalMethod);
            }
            cls.addMethod(methodRename);
        }
    }

    private void replaceClass(ClassReaderSource innerSource, final ClassHolder cls, final ClassReader emuCls) {
        ClassRefsRenamer renamer = new ClassRefsRenamer(referenceCache, preimage -> {
            String newName = emulations2.get(preimage);
            if(newName != null) {
                return newName;
            }
            else {
                return preimage;
            }
        });

        cls.getInterfaces().clear();
        Set<String> interfaces = emuCls.getInterfaces();
        Iterator<String> interfaceIt = interfaces.iterator();
        while(interfaceIt.hasNext()) {
            String interfaceStr = interfaceIt.next();
            ClassReader interfaceClassHolder = innerSource.get(interfaceStr);
            if(interfaceClassHolder != null) {
                ClassHolder classHolder = ModelUtils.copyClass(interfaceClassHolder);
                ClassHolder interfaceRename = renamer.rename(classHolder);
                String name = interfaceRename.getName();
                cls.getInterfaces().add(name);
            }
        }

        String parent = emuCls.getParent();
        if(parent != null && !parent.isEmpty()) {
            ClassReader parentClassHolder = innerSource.get(parent);
            if(parentClassHolder != null) {
                ClassHolder classHolder = ModelUtils.copyClass(parentClassHolder);
                ClassHolder parentClassHolderRename = renamer.rename(classHolder);
                String name = parentClassHolderRename.getName();
                cls.setParent(name);
            }
        }

        for(FieldHolder field : cls.getFields().toArray(new FieldHolder[0])) {
            cls.removeField(field);
        }
        for(MethodHolder method : cls.getMethods().toArray(new MethodHolder[0])) {
            cls.removeMethod(method);
        }
        for(FieldReader field : emuCls.getFields()) {
            FieldHolder newfieldHolder = ModelUtils.copyField(field);
            FieldHolder renamedField = renamer.rename(newfieldHolder);
            cls.addField(renamedField);
        }
        for(MethodReader method : emuCls.getMethods()) {
            MethodHolder newMethodHolder = ModelUtils.copyMethod(method);
            MethodHolder renamedMethod = renamer.rename(newMethodHolder);
            cls.addMethod(renamedMethod);
        }
    }
}
