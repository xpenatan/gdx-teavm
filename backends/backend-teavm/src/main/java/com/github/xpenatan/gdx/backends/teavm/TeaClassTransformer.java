package com.github.xpenatan.gdx.backends.teavm;

import java.util.Set;

import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSIndexer;
import org.teavm.jso.JSMethod;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;
import org.teavm.model.AnnotationContainer;
import org.teavm.model.AnnotationHolder;
import org.teavm.model.AnnotationValue;
import org.teavm.model.ClassHolder;
import org.teavm.model.ClassHolderTransformer;
import org.teavm.model.ClassHolderTransformerContext;
import org.teavm.model.ClassReaderSource;
import org.teavm.model.FieldHolder;
import org.teavm.model.MethodHolder;
import com.github.xpenatan.gdx.backend.web.WebAgentInfo;
import com.github.xpenatan.gdx.backend.web.dom.CanvasPixelArrayWrapper;
import com.github.xpenatan.gdx.backend.web.dom.CanvasRenderingContext2DWrapper;
import com.github.xpenatan.gdx.backend.web.dom.DocumentWrapper;
import com.github.xpenatan.gdx.backend.web.dom.ElementWrapper;
import com.github.xpenatan.gdx.backend.web.dom.EventHandlerWrapper;
import com.github.xpenatan.gdx.backend.web.dom.EventListenerWrapper;
import com.github.xpenatan.gdx.backend.web.dom.EventTargetWrapper;
import com.github.xpenatan.gdx.backend.web.dom.EventWrapper;
import com.github.xpenatan.gdx.backend.web.dom.HTMLCanvasElementWrapper;
import com.github.xpenatan.gdx.backend.web.dom.HTMLDocumentWrapper;
import com.github.xpenatan.gdx.backend.web.dom.HTMLElementWrapper;
import com.github.xpenatan.gdx.backend.web.dom.HTMLImageElementWrapper;
import com.github.xpenatan.gdx.backend.web.dom.HTMLVideoElementWrapper;
import com.github.xpenatan.gdx.backend.web.dom.ImageDataWrapper;
import com.github.xpenatan.gdx.backend.web.dom.KeyboardEventWrapper;
import com.github.xpenatan.gdx.backend.web.dom.LocationWrapper;
import com.github.xpenatan.gdx.backend.web.dom.MouseEventWrapper;
import com.github.xpenatan.gdx.backend.web.dom.NodeWrapper;
import com.github.xpenatan.gdx.backend.web.dom.ProgressEventWrapper;
import com.github.xpenatan.gdx.backend.web.dom.StorageWrapper;
import com.github.xpenatan.gdx.backend.web.dom.TouchEventWrapper;
import com.github.xpenatan.gdx.backend.web.dom.TouchListWrapper;
import com.github.xpenatan.gdx.backend.web.dom.TouchWrapper;
import com.github.xpenatan.gdx.backend.web.dom.WebJSObject;
import com.github.xpenatan.gdx.backend.web.dom.WheelEventWrapper;
import com.github.xpenatan.gdx.backend.web.dom.XMLHttpRequestEventTargetWrapper;
import com.github.xpenatan.gdx.backend.web.dom.XMLHttpRequestWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.ArrayBufferViewWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.ArrayBufferWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.Float32ArrayWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.Float64ArrayWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.FloatArrayWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.Int16ArrayWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.Int32ArrayWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.LongArrayWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.ObjectArrayWrapper;
import com.github.xpenatan.gdx.backend.web.dom.typedarray.Uint8ArrayWrapper;
import com.github.xpenatan.gdx.backend.web.gl.WebGLActiveInfoWrapper;
import com.github.xpenatan.gdx.backend.web.gl.WebGLBufferWrapper;
import com.github.xpenatan.gdx.backend.web.gl.WebGLContextAttributesWrapper;
import com.github.xpenatan.gdx.backend.web.gl.WebGLFramebufferWrapper;
import com.github.xpenatan.gdx.backend.web.gl.WebGLProgramWrapper;
import com.github.xpenatan.gdx.backend.web.gl.WebGLRenderbufferWrapper;
import com.github.xpenatan.gdx.backend.web.gl.WebGLRenderingContextWrapper;
import com.github.xpenatan.gdx.backend.web.gl.WebGLShaderWrapper;
import com.github.xpenatan.gdx.backend.web.gl.WebGLTextureWrapper;
import com.github.xpenatan.gdx.backend.web.gl.WebGLUniformLocationWrapper;
import com.github.xpenatan.gdx.backend.web.soundmanager.SMSoundCallbackWrapper;
import com.github.xpenatan.gdx.backend.web.soundmanager.SMSoundWrapper;
import com.github.xpenatan.gdx.backend.web.soundmanager.SoundManagerCallbackWrapper;
import com.github.xpenatan.gdx.backend.web.soundmanager.SoundManagerWrapper;

/**
 * @author xpenatan
 */
public class TeaClassTransformer implements ClassHolderTransformer {

	private boolean init = false;

	@Override
	public void transformClass(ClassHolder cls, ClassHolderTransformerContext context) {
		if(!init) {
			init = true;

			ClassHolder classHolder = null;
			classHolder = findClassHolder(context, Float32ArrayWrapper.class);
			setClassInterface(classHolder, JSObject.class);
			setMethodAnnotation(classHolder,  JSIndexer.class, "set", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getLength", null);

			classHolder = findClassHolder(context, Int32ArrayWrapper.class);
			setClassInterface(classHolder, JSObject.class);
			setMethodAnnotation(classHolder,  JSIndexer.class, "set", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getLength", null);

			classHolder = findClassHolder(context, Int16ArrayWrapper.class);
			setClassInterface(classHolder, JSObject.class);
			setMethodAnnotation(classHolder,  JSIndexer.class, "set", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getLength", null);

			classHolder = findClassHolder(context, Uint8ArrayWrapper.class);
			setClassInterface(classHolder, JSObject.class);
			setMethodAnnotation(classHolder,  JSIndexer.class, "set", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getLength", null);

			classHolder = findClassHolder(context, Float64ArrayWrapper.class);
			setClassInterface(classHolder, JSObject.class);
			setMethodAnnotation(classHolder,  JSIndexer.class, "set", null);

			classHolder = findClassHolder(context, WebGLTextureWrapper.class);
			setClassInterface(classHolder, JSObject.class);

			classHolder = findClassHolder(context, WebGLUniformLocationWrapper.class);
			setClassInterface(classHolder, JSObject.class);

			classHolder = findClassHolder(context, WebGLShaderWrapper.class);
			setClassInterface(classHolder, JSObject.class);

			classHolder = findClassHolder(context, WebGLRenderbufferWrapper.class);
			setClassInterface(classHolder, JSObject.class);

			classHolder = findClassHolder(context, WebGLProgramWrapper.class);
			setClassInterface(classHolder, JSObject.class);

			classHolder = findClassHolder(context, WebGLFramebufferWrapper.class);
			setClassInterface(classHolder, JSObject.class);

			classHolder = findClassHolder(context, WebGLContextAttributesWrapper.class);
			setClassInterface(classHolder, JSObject.class);

			classHolder = findClassHolder(context, WebGLBufferWrapper.class);
			setClassInterface(classHolder, JSObject.class);

			classHolder = findClassHolder(context, WebGLActiveInfoWrapper.class);
			setClassInterface(classHolder, JSObject.class);
			setMethodAnnotation(classHolder,  JSProperty.class, "getSize", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getType", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getName", null);

			classHolder = findClassHolder(context, WebGLRenderingContextWrapper.class);
			setClassInterface(classHolder, JSObject.class);
			setMethodAnnotation(classHolder,  JSMethod.class, "getParameterInt", "getParameter");
			setMethodAnnotation(classHolder,  JSMethod.class, "getParameterFloat", "getParameter");
			setMethodAnnotation(classHolder,  JSMethod.class, "getParameterString", "getParameter");
			setMethodAnnotation(classHolder,  JSMethod.class, "getProgramParameterInt", "getProgramParameter");
			setMethodAnnotation(classHolder,  JSMethod.class, "getProgramParameterBoolean", "getProgramParameter");
			setMethodAnnotation(classHolder,  JSMethod.class, "getShaderParameterBoolean", "getShaderParameter");
			setMethodAnnotation(classHolder,  JSMethod.class, "getShaderParameterInt", "getShaderParameter");

			classHolder = findClassHolder(context, ArrayBufferViewWrapper.class);
			setClassInterface(classHolder, JSObject.class);

			classHolder = findClassHolder(context, ArrayBufferWrapper.class);
			setClassInterface(classHolder, JSObject.class);

			classHolder = findClassHolder(context, ObjectArrayWrapper.class);
			setClassInterface(classHolder, JSObject.class);

			classHolder = findClassHolder(context, LongArrayWrapper.class);
			setClassInterface(classHolder, JSObject.class);

			classHolder = findClassHolder(context, ImageDataWrapper.class);
			setMethodAnnotation(classHolder,  JSProperty.class, "getData", null);
			setClassInterface(classHolder, JSObject.class);

			classHolder = findClassHolder(context, HTMLImageElementWrapper.class);
			setClassInterface(classHolder, JSObject.class);
			setMethodAnnotation(classHolder,  JSProperty.class, "setSrc", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getWidth", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getHeight", null);

			classHolder = findClassHolder(context, HTMLVideoElementWrapper.class);
			setClassInterface(classHolder, JSObject.class);

			classHolder = findClassHolder(context, HTMLCanvasElementWrapper.class);
			setClassInterface(classHolder, JSObject.class);
			setMethodAnnotation(classHolder,  JSProperty.class, "getWidth", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "setWidth", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getHeight", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "setHeight", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getOwnerDocument", null);

			classHolder = findClassHolder(context, FloatArrayWrapper.class);
			setClassInterface(classHolder, JSObject.class);

			classHolder = findClassHolder(context, EventTargetWrapper.class);
			setClassInterface(classHolder, JSObject.class);

			classHolder = findClassHolder(context, EventListenerWrapper.class);
			setClassInterface(classHolder, JSObject.class);
			setClassAnnotation(classHolder, JSFunctor.class);

			classHolder = findClassHolder(context, EventWrapper.class);
			setClassInterface(classHolder, JSObject.class);
			setMethodAnnotation(classHolder,  JSProperty.class, "getTarget", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getType", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getDetail", null);

			classHolder = findClassHolder(context, WheelEventWrapper.class);
			setClassInterface(classHolder, JSObject.class);
			setMethodAnnotation(classHolder,  JSProperty.class, "getDeltaX", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getDeltaY", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getDeltaZ", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getWheelDelta", null);

			classHolder = findClassHolder(context, MouseEventWrapper.class);
			setClassInterface(classHolder, JSObject.class);
			setMethodAnnotation(classHolder,  JSProperty.class, "getClientX", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getClientY", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getMovementX", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getMovementY", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getButton", null);

			classHolder = findClassHolder(context, KeyboardEventWrapper.class);
			setClassInterface(classHolder, JSObject.class);
			setMethodAnnotation(classHolder,  JSProperty.class, "getCharCode", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getKeyCode", null);

			classHolder = findClassHolder(context, TouchEventWrapper.class);
			setClassInterface(classHolder, JSObject.class);
			setMethodAnnotation(classHolder,  JSProperty.class, "getChangedTouches", null);

			classHolder = findClassHolder(context, TouchListWrapper.class);
			setClassInterface(classHolder, JSObject.class);
			setMethodAnnotation(classHolder,  JSProperty.class, "getLength", null);

			classHolder = findClassHolder(context, TouchWrapper.class);
			setClassInterface(classHolder, JSObject.class);
			setMethodAnnotation(classHolder,  JSProperty.class, "getIdentifier", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getClientX", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getClientY", null);

			classHolder = findClassHolder(context, WebAgentInfo.class);
			setClassInterface(classHolder, JSObject.class);
			setMethodAnnotation(classHolder,  JSProperty.class, "isFirefox", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "isChrome", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "isSafari", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "isOpera", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "isIE", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "isMacOS", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "isLinux", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "isWindows", null);

			classHolder = findClassHolder(context, DocumentWrapper.class);
			setClassInterface(classHolder, JSObject.class);
			setMethodAnnotation(classHolder,  JSProperty.class, "getCompatMode", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getDocumentElement", null);

			classHolder = findClassHolder(context, ElementWrapper.class);
			setClassInterface(classHolder, JSObject.class);
			setMethodAnnotation(classHolder,  JSProperty.class, "getScrollTop", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getScrollLeft", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getClientWidth", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getClientHeight", null);

			classHolder = findClassHolder(context, HTMLElementWrapper.class);
			setClassInterface(classHolder, JSObject.class);
			setMethodAnnotation(classHolder,  JSProperty.class, "getOffsetParent", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getOffsetTop", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getOffsetLeft", null);

			classHolder = findClassHolder(context, NodeWrapper.class);
			setClassInterface(classHolder, JSObject.class);
			setMethodAnnotation(classHolder,  JSProperty.class, "getParentNode", null);

			classHolder = findClassHolder(context, HTMLDocumentWrapper.class);
			setClassInterface(classHolder, JSObject.class);
			setMethodAnnotation(classHolder,  JSProperty.class, "getParentNode", null);

			classHolder = findClassHolder(context, EventHandlerWrapper.class);
			setClassInterface(classHolder, JSObject.class);
			setClassAnnotation(classHolder, JSFunctor.class);

			classHolder = findClassHolder(context, XMLHttpRequestEventTargetWrapper.class);
			setMethodAnnotation(classHolder,  JSProperty.class, "setOnprogress", null);
			setClassInterface(classHolder, JSObject.class);

			classHolder = findClassHolder(context, XMLHttpRequestWrapper.class);
			setClassInterface(classHolder, JSObject.class);
			setMethodAnnotation(classHolder,  JSProperty.class, "setOnreadystatechange", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getReadyState", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getStatus", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getResponse", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "setResponseType", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getResponseText", null);

			classHolder = findClassHolder(context, ProgressEventWrapper.class);
			setClassInterface(classHolder, JSObject.class);
			setMethodAnnotation(classHolder,  JSProperty.class, "getLoaded", null);

			classHolder = findClassHolder(context, LocationWrapper.class);
			setClassInterface(classHolder, JSObject.class);
			setMethodAnnotation(classHolder,  JSProperty.class, "getHref", null);

			classHolder = findClassHolder(context, WebJSObject.class);
			setClassInterface(classHolder, JSObject.class);

			classHolder = findClassHolder(context, StorageWrapper.class);
			setClassInterface(classHolder, JSObject.class);
			setMethodAnnotation(classHolder,  JSProperty.class, "getLength", null);

			classHolder = findClassHolder(context, SoundManagerCallbackWrapper.class);
			setClassInterface(classHolder, JSObject.class);

			classHolder = findClassHolder(context, SMSoundCallbackWrapper.class);
			setClassInterface(classHolder, JSObject.class);

			classHolder = findClassHolder(context, SMSoundWrapper.class);
			setClassInterface(classHolder, JSObject.class);

			classHolder = findClassHolder(context, SoundManagerWrapper.class);
			setClassInterface(classHolder, JSObject.class);

			classHolder = findClassHolder(context, CanvasPixelArrayWrapper.class);
			setClassInterface(classHolder, JSObject.class);

			classHolder = findClassHolder(context, CanvasRenderingContext2DWrapper.class);
			setClassInterface(classHolder, JSObject.class);
			setMethodAnnotation(classHolder,  JSProperty.class, "getGlobalAlpha", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "setGlobalAlpha", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "setFillStyle", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "setStrokeStyle", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getStrokeStyle", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getGlobalCompositeOperation", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "setGlobalCompositeOperation", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getLineWidth", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "setLineWidth", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getLineCap", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "setLineCap", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getLineJoin", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "setLineJoin", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "getMiterLimit", null);
			setMethodAnnotation(classHolder,  JSProperty.class, "setMiterLimit", null);
		}
	}

	private ClassHolder findClassHolder(ClassHolderTransformerContext context, Class clazz) {
		return findClassHolder(context, clazz.getName());
	}

	private ClassHolder findClassHolder(ClassHolderTransformerContext context, String clazz) {
		ClassReaderSource innerSource = context.getHierarchy().getClassSource();
		ClassHolder classHolder = (ClassHolder)innerSource.get(clazz);
		return classHolder;
	}

	private void setFieldAnnotation(ClassHolder classHolder, Class annotationClass, String fieldStr, String value) {
		for (FieldHolder field : classHolder.getFields().toArray(new FieldHolder[0])) {
			String name = field.getName();
			if (name.equals(fieldStr)) {
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
		for (MethodHolder method : classHolder.getMethods().toArray(new MethodHolder[0])) {
			String name = method.getName();
			if (name.equals(methodStr)) {
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
		interfaces.add(JSObject.class.getName());
	}

	private void setClassAnnotation(ClassHolder classHolder, Class annotationClass) {
		AnnotationContainer annotations = classHolder.getAnnotations();
		AnnotationHolder annotation = new AnnotationHolder(annotationClass.getName());
		annotations.add(annotation);
	}

}
