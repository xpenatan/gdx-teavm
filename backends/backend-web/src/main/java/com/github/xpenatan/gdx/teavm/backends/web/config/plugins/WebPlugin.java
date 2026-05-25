package com.github.xpenatan.gdx.teavm.backends.web.config.plugins;

import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.GdxTeaVMPluginConfig;
import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.TeaReflectionSupplier;
import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.TeaVMPluginClasspath;
import java.net.URL;
import java.util.ArrayList;
import org.teavm.backend.javascript.TeaVMJavaScriptHost;
import org.teavm.backend.wasm.TeaVMWasmGCHost;
import org.teavm.dependency.AbstractDependencyListener;
import org.teavm.dependency.DependencyAgent;
import org.teavm.jso.impl.JSOPlugin;
import org.teavm.model.AccessLevel;
import org.teavm.model.ClassReader;
import org.teavm.model.ElementModifier;
import org.teavm.model.MethodDescriptor;
import org.teavm.model.MethodReader;
import org.teavm.model.ValueType;
import org.teavm.vm.spi.Before;
import org.teavm.vm.spi.TeaVMHost;
import org.teavm.vm.spi.TeaVMPlugin;

/**
 * @author xpenatan
 */
@Before(JSOPlugin.class)
public class WebPlugin implements TeaVMPlugin {

    @Override
    public void install(TeaVMHost host) {
        TeaVMJavaScriptHost javaScriptHost = host.getExtension(TeaVMJavaScriptHost.class);
        TeaVMWasmGCHost wasmGCHost = host.getExtension(TeaVMWasmGCHost.class);
        if(javaScriptHost == null && wasmGCHost == null) {
            return;
        }

        host.add(new WebClassTransformer());
        host.add(new JavaObjectExporterDependency());

        GdxTeaVMPluginConfig config = GdxTeaVMPluginConfig.from(host.getProperties());
        ArrayList<URL> classPathURLs = TeaVMPluginClasspath.getURLs(host.getClassLoader(), config.classpath);
        TeaReflectionSupplier.printDebugLogs = config.reflectionDebug;
        if(config.reflectionEnabled) {
            TeaReflectionSupplier.addReflectionClass(config.reflectionClasses);
            if(config.reflectionDefaults) {
                TeaReflectionSupplier.addDefaultReflectionClasses(classPathURLs);
            }
            TeaReflectionSupplier.installReflectionDependencySupport(host);
            if(config.reflectionDebug) {
                TeaReflectionSupplier.printReflectionClasses();
            }
        }
        if(javaScriptHost != null) {
            installJavaScriptAbstractConstructorFix(host, javaScriptHost);
        }

        if(config.webappEnabled) {
            if(javaScriptHost != null) {
                GdxWebTargetWrapper.install(host, GdxWebRendererListener.TargetType.JAVASCRIPT, config, classPathURLs);
            }
            else if(wasmGCHost != null) {
                GdxWebTargetWrapper.install(host, GdxWebRendererListener.TargetType.WASM_GC, config, classPathURLs);
            }
        }
    }

    private static void installJavaScriptAbstractConstructorFix(TeaVMHost host, TeaVMJavaScriptHost javaScriptHost) {
        host.add(new AbstractDefaultConstructorDependencyListener());
        javaScriptHost.addForcedFunctionMethods((context, methodRef) ->
                isAbstractPublicNoArgConstructor(context.getClassSource().get(methodRef.getClassName()),
                        methodRef.getDescriptor()));
    }

    private static boolean isAbstractPublicNoArgConstructor(ClassReader cls, MethodDescriptor descriptor) {
        if(cls == null
                || !cls.hasModifier(ElementModifier.ABSTRACT)
                || !descriptor.getName().equals("<init>")
                || descriptor.parameterCount() != 0) {
            return false;
        }
        MethodReader method = cls.getMethod(descriptor);
        return method != null && method.getProgram() != null && method.getLevel() == AccessLevel.PUBLIC;
    }

    private static class AbstractDefaultConstructorDependencyListener extends AbstractDependencyListener {
        @Override
        public void started(DependencyAgent agent) {
            for(String className : agent.getReachableClasses()) {
                preserveAbstractDefaultConstructor(agent, className);
            }
        }

        @Override
        public void classReached(DependencyAgent agent, String className) {
            preserveAbstractDefaultConstructor(agent, className);
        }

        private void preserveAbstractDefaultConstructor(DependencyAgent agent, String className) {
            ClassReader cls = agent.getClassSource().get(className);
            if(cls == null || !cls.hasModifier(ElementModifier.ABSTRACT)) {
                return;
            }
            MethodReader constructor = cls.getMethod(new MethodDescriptor("<init>", void.class));
            if(constructor != null && constructor.getProgram() != null && constructor.getLevel() == AccessLevel.PUBLIC) {
                var methodDependency = agent.linkMethod(constructor.getReference());
                methodDependency.use();
                methodDependency.getVariable(0).propagate(agent.getType(ValueType.object(className)));
            }
        }
    }
}
