package com.github.xpenatan.gdx.backends.teavm.plugins;

import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.backends.teavm.CustomPreOptimizingClassHolderSource;
import com.github.xpenatan.gdx.backends.teavm.TeaLauncher;
import com.github.xpenatan.gdx.backends.web.WebAgentInfo;
import com.github.xpenatan.gdx.backends.web.dom.CanvasRenderingContext2DWrapper;
import com.github.xpenatan.gdx.backends.web.dom.DocumentWrapper;
import com.github.xpenatan.gdx.backends.web.dom.ElementWrapper;
import com.github.xpenatan.gdx.backends.web.dom.EventHandlerWrapper;
import com.github.xpenatan.gdx.backends.web.dom.EventListenerWrapper;
import com.github.xpenatan.gdx.backends.web.dom.EventTargetWrapper;
import com.github.xpenatan.gdx.backends.web.dom.EventWrapper;
import com.github.xpenatan.gdx.backends.web.dom.HTMLCanvasElementWrapper;
import com.github.xpenatan.gdx.backends.web.dom.HTMLDocumentWrapper;
import com.github.xpenatan.gdx.backends.web.dom.HTMLElementWrapper;
import com.github.xpenatan.gdx.backends.web.dom.HTMLImageElementWrapper;
import com.github.xpenatan.gdx.backends.web.dom.HTMLVideoElementWrapper;
import com.github.xpenatan.gdx.backends.web.dom.ImageDataWrapper;
import com.github.xpenatan.gdx.backends.web.dom.KeyboardEventWrapper;
import com.github.xpenatan.gdx.backends.web.dom.LocationWrapper;
import com.github.xpenatan.gdx.backends.web.dom.MouseEventWrapper;
import com.github.xpenatan.gdx.backends.web.dom.NodeWrapper;
import com.github.xpenatan.gdx.backends.web.dom.ProgressEventWrapper;
import com.github.xpenatan.gdx.backends.web.dom.StorageWrapper;
import com.github.xpenatan.gdx.backends.web.dom.TouchEventWrapper;
import com.github.xpenatan.gdx.backends.web.dom.TouchListWrapper;
import com.github.xpenatan.gdx.backends.web.dom.TouchWrapper;
import com.github.xpenatan.gdx.backends.web.dom.WebJSObject;
import com.github.xpenatan.gdx.backends.web.dom.WheelEventWrapper;
import com.github.xpenatan.gdx.backends.web.dom.XMLHttpRequestEventTargetWrapper;
import com.github.xpenatan.gdx.backends.web.dom.XMLHttpRequestWrapper;
import com.github.xpenatan.gdx.backends.web.dom.typedarray.ArrayBufferViewWrapper;
import com.github.xpenatan.gdx.backends.web.dom.typedarray.ArrayBufferWrapper;
import com.github.xpenatan.gdx.backends.web.dom.typedarray.Float32ArrayWrapper;
import com.github.xpenatan.gdx.backends.web.dom.typedarray.Float64ArrayWrapper;
import com.github.xpenatan.gdx.backends.web.dom.typedarray.FloatArrayWrapper;
import com.github.xpenatan.gdx.backends.web.dom.typedarray.Int16ArrayWrapper;
import com.github.xpenatan.gdx.backends.web.dom.typedarray.Int32ArrayWrapper;
import com.github.xpenatan.gdx.backends.web.dom.typedarray.Int8ArrayWrapper;
import com.github.xpenatan.gdx.backends.web.dom.typedarray.LongArrayWrapper;
import com.github.xpenatan.gdx.backends.web.dom.typedarray.ObjectArrayWrapper;
import com.github.xpenatan.gdx.backends.web.dom.typedarray.Uint8ArrayWrapper;
import com.github.xpenatan.gdx.backends.web.dom.typedarray.Uint8ClampedArrayWrapper;
import com.github.xpenatan.gdx.backends.web.emu.Emulate;
import com.github.xpenatan.gdx.backends.web.gl.WebGLActiveInfoWrapper;
import com.github.xpenatan.gdx.backends.web.gl.WebGLBufferWrapper;
import com.github.xpenatan.gdx.backends.web.gl.WebGLContextAttributesWrapper;
import com.github.xpenatan.gdx.backends.web.gl.WebGLFramebufferWrapper;
import com.github.xpenatan.gdx.backends.web.gl.WebGLProgramWrapper;
import com.github.xpenatan.gdx.backends.web.gl.WebGLRenderbufferWrapper;
import com.github.xpenatan.gdx.backends.web.gl.WebGLRenderingContextWrapper;
import com.github.xpenatan.gdx.backends.web.gl.WebGLShaderWrapper;
import com.github.xpenatan.gdx.backends.web.gl.WebGLTextureWrapper;
import com.github.xpenatan.gdx.backends.web.gl.WebGLUniformLocationWrapper;
import com.github.xpenatan.gdx.backends.web.soundmanager.SMSoundCallbackWrapper;
import com.github.xpenatan.gdx.backends.web.soundmanager.SoundManagerCallbackWrapper;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.reflections.Reflections;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSIndexer;
import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;
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

    private HashMap<String, Class<?>> emulations = new HashMap<>();
    private HashMap<String, String> emulations2 = new HashMap<>();
    private ReferenceCache referenceCache = new ReferenceCache();

    public TeaClassTransformer() {
        Reflections reflections = new Reflections();

        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Emulate.class);
        for(Class<?> type : annotated){
            Emulate em = type.getAnnotation(Emulate.class);
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
                emulations.put(typeName, type);
                emulations2.put(type.getName(), typeName);
            }
        }
    }

    @Override
    public void transformClass(ClassHolder cls, ClassHolderTransformerContext context) {
        CustomPreOptimizingClassHolderSource innerSource = (CustomPreOptimizingClassHolderSource)context.getHierarchy().getClassSource();
        String className = cls.getName();
        if(!init) {
            init = true;
            ClassHolder classHolder = null;

            classHolder = findClassHolder(cls, context, TeaLauncher.class);
            setGdxApplicationClass(classHolder, context);

            classHolder = findClassHolder(cls, context, Float32ArrayWrapper.class);
            setClassInterface(classHolder, JSObject.class);
            setMethodAnnotation(classHolder, JSIndexer.class, "set", null);
            setMethodAnnotation(classHolder, JSIndexer.class, "get", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getLength", null);

            classHolder = findClassHolder(cls, context, Int32ArrayWrapper.class);
            setClassInterface(classHolder, JSObject.class);
            setMethodAnnotation(classHolder, JSIndexer.class, "set", null);
            setMethodAnnotation(classHolder, JSIndexer.class, "get", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getLength", null);

            classHolder = findClassHolder(cls, context, Int16ArrayWrapper.class);
            setClassInterface(classHolder, JSObject.class);
            setMethodAnnotation(classHolder, JSIndexer.class, "set", null);
            setMethodAnnotation(classHolder, JSIndexer.class, "get", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getLength", null);

            classHolder = findClassHolder(cls, context, Uint8ArrayWrapper.class);
            setClassInterface(classHolder, JSObject.class);
            setMethodAnnotation(classHolder, JSIndexer.class, "set", null);
            setMethodAnnotation(classHolder, JSIndexer.class, "get", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getLength", null);

            classHolder = findClassHolder(cls, context, Uint8ClampedArrayWrapper.class);
            setClassInterface(classHolder, JSObject.class);
            setMethodAnnotation(classHolder, JSIndexer.class, "set", null);
            setMethodAnnotation(classHolder, JSIndexer.class, "get", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getLength", null);

            classHolder = findClassHolder(cls, context, Int8ArrayWrapper.class);
            setClassInterface(classHolder, JSObject.class);
            setMethodAnnotation(classHolder, JSIndexer.class, "set", null);
            setMethodAnnotation(classHolder, JSIndexer.class, "get", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getLength", null);

            classHolder = findClassHolder(cls, context, Float64ArrayWrapper.class);
            setClassInterface(classHolder, JSObject.class);
            setMethodAnnotation(classHolder, JSIndexer.class, "set", null);

            classHolder = findClassHolder(cls, context, WebGLTextureWrapper.class);
            setClassInterface(classHolder, JSObject.class);

            classHolder = findClassHolder(cls, context, WebGLUniformLocationWrapper.class);
            setClassInterface(classHolder, JSObject.class);

            classHolder = findClassHolder(cls, context, WebGLShaderWrapper.class);
            setClassInterface(classHolder, JSObject.class);

            classHolder = findClassHolder(cls, context, WebGLRenderbufferWrapper.class);
            setClassInterface(classHolder, JSObject.class);

            classHolder = findClassHolder(cls, context, WebGLProgramWrapper.class);
            setClassInterface(classHolder, JSObject.class);

            classHolder = findClassHolder(cls, context, WebGLFramebufferWrapper.class);
            setClassInterface(classHolder, JSObject.class);

            classHolder = findClassHolder(cls, context, WebGLContextAttributesWrapper.class);
            setClassInterface(classHolder, JSObject.class);

            classHolder = findClassHolder(cls, context, WebGLBufferWrapper.class);
            setClassInterface(classHolder, JSObject.class);

            classHolder = findClassHolder(cls, context, WebGLActiveInfoWrapper.class);
            setClassInterface(classHolder, JSObject.class);
            setMethodAnnotation(classHolder, JSProperty.class, "getSize", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getType", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getName", null);

            classHolder = findClassHolder(cls, context, WebGLRenderingContextWrapper.class);
            setClassInterface(classHolder, JSObject.class);
            setMethodAnnotation(classHolder, JSMethod.class, "getParameterInt", "getParameter");
            setMethodAnnotation(classHolder, JSMethod.class, "getParameterFloat", "getParameter");
            setMethodAnnotation(classHolder, JSMethod.class, "getParameterString", "getParameter");
            setMethodAnnotation(classHolder, JSMethod.class, "getProgramParameterInt", "getProgramParameter");
            setMethodAnnotation(classHolder, JSMethod.class, "getProgramParameterBoolean", "getProgramParameter");
            setMethodAnnotation(classHolder, JSMethod.class, "getShaderParameterBoolean", "getShaderParameter");
            setMethodAnnotation(classHolder, JSMethod.class, "getShaderParameterInt", "getShaderParameter");

            classHolder = findClassHolder(cls, context, ArrayBufferViewWrapper.class);
            setMethodAnnotation(classHolder, JSProperty.class, "getBuffer", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getByteOffset", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getByteLength", null);
            setClassInterface(classHolder, JSObject.class);

            classHolder = findClassHolder(cls, context, ArrayBufferWrapper.class);
            setClassInterface(classHolder, JSObject.class);

            classHolder = findClassHolder(cls, context, ObjectArrayWrapper.class);
            setClassInterface(classHolder, JSObject.class);

            classHolder = findClassHolder(cls, context, LongArrayWrapper.class);
            setClassInterface(classHolder, JSObject.class);

            classHolder = findClassHolder(cls, context, ImageDataWrapper.class);
            setMethodAnnotation(classHolder, JSProperty.class, "getData", null);
            setClassInterface(classHolder, JSObject.class);

            classHolder = findClassHolder(cls, context, HTMLImageElementWrapper.class);
            setClassInterface(classHolder, JSObject.class);
            setMethodAnnotation(classHolder, JSProperty.class, "setSrc", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getWidth", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getHeight", null);

            classHolder = findClassHolder(cls, context, HTMLVideoElementWrapper.class);
            setClassInterface(classHolder, JSObject.class);

            classHolder = findClassHolder(cls, context, HTMLCanvasElementWrapper.class);
            setClassInterface(classHolder, JSObject.class);
            setMethodAnnotation(classHolder, JSProperty.class, "getWidth", null);
            setMethodAnnotation(classHolder, JSProperty.class, "setWidth", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getHeight", null);
            setMethodAnnotation(classHolder, JSProperty.class, "setHeight", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getOwnerDocument", null);

            classHolder = findClassHolder(cls, context, FloatArrayWrapper.class);
            setClassInterface(classHolder, JSObject.class);

            classHolder = findClassHolder(cls, context, EventTargetWrapper.class);
            setClassInterface(classHolder, JSObject.class);

            classHolder = findClassHolder(cls, context, EventListenerWrapper.class);
            setClassInterface(classHolder, JSObject.class);
            setClassAnnotation(classHolder, JSFunctor.class);

            classHolder = findClassHolder(cls, context, EventWrapper.class);
            setClassInterface(classHolder, JSObject.class);
            setMethodAnnotation(classHolder, JSProperty.class, "getTarget", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getType", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getDetail", null);

            classHolder = findClassHolder(cls, context, WheelEventWrapper.class);
            setClassInterface(classHolder, JSObject.class);
            setMethodAnnotation(classHolder, JSProperty.class, "getDeltaX", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getDeltaY", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getDeltaZ", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getWheelDelta", null);

            classHolder = findClassHolder(cls, context, MouseEventWrapper.class);
            setClassInterface(classHolder, JSObject.class);
            setMethodAnnotation(classHolder, JSProperty.class, "getClientX", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getClientY", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getMovementX", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getMovementY", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getButton", null);

            classHolder = findClassHolder(cls, context, KeyboardEventWrapper.class);
            setClassInterface(classHolder, JSObject.class);
            setMethodAnnotation(classHolder, JSProperty.class, "getCharCode", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getKeyCode", null);

            classHolder = findClassHolder(cls, context, TouchEventWrapper.class);
            setClassInterface(classHolder, JSObject.class);
            setMethodAnnotation(classHolder, JSProperty.class, "getChangedTouches", null);

            classHolder = findClassHolder(cls, context, TouchListWrapper.class);
            setClassInterface(classHolder, JSObject.class);
            setMethodAnnotation(classHolder, JSProperty.class, "getLength", null);

            classHolder = findClassHolder(cls, context, TouchWrapper.class);
            setClassInterface(classHolder, JSObject.class);
            setMethodAnnotation(classHolder, JSProperty.class, "getIdentifier", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getClientX", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getClientY", null);

            classHolder = findClassHolder(cls, context, WebAgentInfo.class);
            setClassInterface(classHolder, JSObject.class);
            setMethodAnnotation(classHolder, JSProperty.class, "isFirefox", null);
            setMethodAnnotation(classHolder, JSProperty.class, "isChrome", null);
            setMethodAnnotation(classHolder, JSProperty.class, "isSafari", null);
            setMethodAnnotation(classHolder, JSProperty.class, "isOpera", null);
            setMethodAnnotation(classHolder, JSProperty.class, "isIE", null);
            setMethodAnnotation(classHolder, JSProperty.class, "isMacOS", null);
            setMethodAnnotation(classHolder, JSProperty.class, "isLinux", null);
            setMethodAnnotation(classHolder, JSProperty.class, "isWindows", null);

            classHolder = findClassHolder(cls, context, DocumentWrapper.class);
            setClassInterface(classHolder, JSObject.class);
            setMethodAnnotation(classHolder, JSProperty.class, "getCompatMode", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getDocumentElement", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getBody", null);

            classHolder = findClassHolder(cls, context, ElementWrapper.class);
            setClassInterface(classHolder, JSObject.class);
            setMethodAnnotation(classHolder, JSProperty.class, "getScrollTop", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getScrollLeft", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getClientWidth", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getClientHeight", null);

            classHolder = findClassHolder(cls, context, HTMLElementWrapper.class);
            setClassInterface(classHolder, JSObject.class);
            setMethodAnnotation(classHolder, JSProperty.class, "getOffsetParent", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getOffsetTop", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getOffsetLeft", null);

            classHolder = findClassHolder(cls, context, NodeWrapper.class);
            setClassInterface(classHolder, JSObject.class);
            setMethodAnnotation(classHolder, JSProperty.class, "getParentNode", null);

            classHolder = findClassHolder(cls, context, HTMLDocumentWrapper.class);
            setClassInterface(classHolder, JSObject.class);
            setMethodAnnotation(classHolder, JSProperty.class, "getParentNode", null);

            classHolder = findClassHolder(cls, context, EventHandlerWrapper.class);
            setClassInterface(classHolder, JSObject.class);
            setClassAnnotation(classHolder, JSFunctor.class);

            classHolder = findClassHolder(cls, context, XMLHttpRequestEventTargetWrapper.class);
            setMethodAnnotation(classHolder, JSProperty.class, "setOnprogress", null);
            setClassInterface(classHolder, JSObject.class);

            classHolder = findClassHolder(cls, context, XMLHttpRequestWrapper.class);
            setClassInterface(classHolder, JSObject.class);
            setMethodAnnotation(classHolder, JSProperty.class, "setOnreadystatechange", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getReadyState", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getStatus", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getResponse", null);
            setMethodAnnotation(classHolder, JSProperty.class, "setResponseType", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getResponseText", null);

            classHolder = findClassHolder(cls, context, ProgressEventWrapper.class);
            setClassInterface(classHolder, JSObject.class);
            setMethodAnnotation(classHolder, JSProperty.class, "getLoaded", null);

            classHolder = findClassHolder(cls, context, LocationWrapper.class);
            setClassInterface(classHolder, JSObject.class);
            setMethodAnnotation(classHolder, JSProperty.class, "getHref", null);

            classHolder = findClassHolder(cls, context, WebJSObject.class);
            setClassInterface(classHolder, JSObject.class);

            classHolder = findClassHolder(cls, context, StorageWrapper.class);
            setClassInterface(classHolder, JSObject.class);
            setMethodAnnotation(classHolder, JSProperty.class, "getLength", null);

            classHolder = findClassHolder(cls, context, SoundManagerCallbackWrapper.class);
            setClassInterface(classHolder, JSObject.class);

            classHolder = findClassHolder(cls, context, SMSoundCallbackWrapper.class);
            setClassInterface(classHolder, JSObject.class);

            classHolder = findClassHolder(cls, context, CanvasRenderingContext2DWrapper.class);
            setClassInterface(classHolder, JSObject.class);
            setMethodAnnotation(classHolder, JSProperty.class, "getGlobalAlpha", null);
            setMethodAnnotation(classHolder, JSProperty.class, "setGlobalAlpha", null);
            setMethodAnnotation(classHolder, JSProperty.class, "setFillStyle", null);
            setMethodAnnotation(classHolder, JSProperty.class, "setStrokeStyle", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getStrokeStyle", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getGlobalCompositeOperation", null);
            setMethodAnnotation(classHolder, JSProperty.class, "setGlobalCompositeOperation", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getLineWidth", null);
            setMethodAnnotation(classHolder, JSProperty.class, "setLineWidth", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getLineCap", null);
            setMethodAnnotation(classHolder, JSProperty.class, "setLineCap", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getLineJoin", null);
            setMethodAnnotation(classHolder, JSProperty.class, "setLineJoin", null);
            setMethodAnnotation(classHolder, JSProperty.class, "getMiterLimit", null);
            setMethodAnnotation(classHolder, JSProperty.class, "setMiterLimit", null);

            // Hack to make it compile. For some reason teavm add MapNode reference class but it does not even exist in js file
            fixHack(cls, context);
        }
        String name = cls.getName();
        if(emulations.containsKey(name)){
            Class<?> emulated = emulations.get(cls.getName());
            ClassHolder emulatedClassHolder = innerSource.get(emulated.getName());
            replaceClass(innerSource, cls, emulatedClassHolder);
        }
    }

    private void fixHack(ClassHolder cur, ClassHolderTransformerContext context) {
        ClassHolder classHolder = findClassHolder(cur, context, FileHandle.class);
        MethodHolder methodHolder = getMethodHolder(classHolder, "FileChannel$MapMode");
        if(methodHolder != null) {
            classHolder.removeMethod(methodHolder);
        }
    }

    private void setGdxApplicationClass(ClassHolder classHolder, ClassHolderTransformerContext context) {
        String entryPoint = TeaClassTransformer.applicationListener;
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

    private void replaceClass(CustomPreOptimizingClassHolderSource innerSource, final ClassHolder cls, final ClassHolder emuCls){
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
            ClassHolder interfaceClassHolder = innerSource.get(interfaceStr);
            if(interfaceClassHolder != null) {
                ClassHolder classHolder = ModelUtils.copyClass(interfaceClassHolder);
                ClassHolder interfaceRename = renamer.rename(classHolder);
                String name = interfaceRename.getName();
                cls.getInterfaces().add(name);
            }
        }

        String parent = emuCls.getParent();
        if(parent != null && !parent.isEmpty()) {
            ClassHolder parentClassHolder = innerSource.get(parent);
            if(parentClassHolder != null) {
                ClassHolder classHolder = ModelUtils.copyClass(parentClassHolder);
                ClassHolder parentClassHolderRename = renamer.rename(classHolder);
                String name = parentClassHolderRename.getName();
                cls.setParent(name);
            }
        }

        for(FieldHolder field : cls.getFields().toArray(new FieldHolder[0])){
            cls.removeField(field);
        }
        for(MethodHolder method : cls.getMethods().toArray(new MethodHolder[0])){
            cls.removeMethod(method);
        }
        for(FieldReader field : emuCls.getFields()){
            FieldHolder newfieldHolder = ModelUtils.copyField(field);
            FieldHolder renamedField = renamer.rename(newfieldHolder);
            cls.addField(renamedField);
        }
        for(MethodReader method : emuCls.getMethods()){
            MethodHolder newMethodHolder = ModelUtils.copyMethod(method);
            MethodHolder renamedMethod = renamer.rename(newMethodHolder);
            cls.addMethod(renamedMethod);
        }
    }
}
