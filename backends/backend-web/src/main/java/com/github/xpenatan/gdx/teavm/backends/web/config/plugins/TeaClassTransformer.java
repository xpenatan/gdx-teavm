package com.github.xpenatan.gdx.teavm.backends.web.config.plugins;

import com.github.xpenatan.gdx.teavm.backends.web.gen.Emulate;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
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

    public static Reflections reflections = new Reflections("emulate", "emu", "com", "org", "emu/net");

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
            }
        }
        else if(emulations.containsKey(className)) {
            ClassReader original = innerSource.get(className);
            Class<?> emulated = emulations.get(className);
            ClassReader emulatedClassHolder = innerSource.get(emulated.getName());
            if(emulatedClassHolder != null) {
                replaceClass(innerSource, cls, emulatedClassHolder);
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

    private ClassHolder findClassHolder(ClassHolder cur, ClassHolderTransformerContext context, String clazz) {
        if(cur.getName().equals(clazz))
            return cur;
        ClassReaderSource innerSource = context.getHierarchy().getClassSource();
        ClassReader classReader = innerSource.get(clazz);
        ClassHolder classHolder = (ClassHolder)classReader;
        return classHolder;
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