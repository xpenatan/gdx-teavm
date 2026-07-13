package com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.compat;

import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.DelegatingTeaVMTarget;
import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.TeaVMTargetInstaller;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.teavm.model.ListableClassReaderSource;
import org.teavm.model.MethodReader;
import org.teavm.model.MethodReference;
import org.teavm.model.Program;
import org.teavm.model.lowlevel.Characteristics;
import org.teavm.model.lowlevel.ClassInitializerEliminator;
import org.teavm.model.lowlevel.ShadowStackTransformer;
import org.teavm.model.lowlevel.WriteBarrierInsertion;
import org.teavm.model.util.AsyncMethodFinder;
import org.teavm.vm.TeaVMTarget;
import org.teavm.vm.TeaVMTargetController;
import org.teavm.vm.spi.TeaVMHost;

/** Backports corrected C coroutine lowering for TeaVM 0.15.0 while retaining the remaining CTarget passes. */
public final class TeaVMCCompatibilityTarget extends DelegatingTeaVMTarget {
    static final String AFFECTED_TEAVM_VERSION = "0.15.0";
    private static final Pattern TEAVM_CORE_JAR = Pattern.compile("teavm-core-([^/\\\\]+)\\.jar$");

    private TeaVMTargetController controller;
    private ClassInitializerEliminator classInitializerEliminator;
    private ShadowStackTransformer shadowStackTransformer;
    private WriteBarrierInsertion writeBarrierInsertion;
    private Set<MethodReference> asyncMethods = Set.of();
    private boolean hasThreads;

    public TeaVMCCompatibilityTarget(TeaVMTarget delegate) {
        super(delegate);
    }

    public static void install(TeaVMHost host) {
        if(!isRequired()) {
            return;
        }
        TeaVMTargetInstaller.install(host, TeaVMCCompatibilityTarget.class, TeaVMCCompatibilityTarget::new);
    }

    static boolean isRequired() {
        return isRequired(detectTeaVMVersion());
    }

    static boolean isRequired(String version) {
        // Source/composite builds do not expose artifact metadata. gdx-teavm currently pins 0.15.0,
        // so keep the backport enabled when the version cannot be discovered.
        return version == null || AFFECTED_TEAVM_VERSION.equals(version);
    }

    static String detectTeaVMVersion() {
        Package teaVMPackage = TeaVMTarget.class.getPackage();
        if(teaVMPackage != null && teaVMPackage.getImplementationVersion() != null) {
            return teaVMPackage.getImplementationVersion();
        }
        if(TeaVMTarget.class.getProtectionDomain() == null
                || TeaVMTarget.class.getProtectionDomain().getCodeSource() == null) {
            return null;
        }
        String location = TeaVMTarget.class.getProtectionDomain().getCodeSource().getLocation().toExternalForm();
        Matcher matcher = TEAVM_CORE_JAR.matcher(location);
        return matcher.find() ? matcher.group(1) : null;
    }

    @Override
    public void setController(TeaVMTargetController controller) {
        super.setController(controller);
        this.controller = controller;
        Characteristics characteristics = new Characteristics(controller.getUnprocessedClassSource());
        classInitializerEliminator = new ClassInitializerEliminator(controller.getUnprocessedClassSource());
        shadowStackTransformer = new ShadowStackTransformer(characteristics);
        writeBarrierInsertion = new WriteBarrierInsertion(characteristics);
    }

    @Override
    public void analyzeBeforeOptimizations(ListableClassReaderSource classSource) {
        super.analyzeBeforeOptimizations(classSource);
        AsyncMethodFinder asyncFinder = new AsyncMethodFinder(controller.getDependencyInfo().getCallGraph(),
                controller.getDependencyInfo());
        asyncFinder.find(classSource);
        asyncMethods = new HashSet<>(asyncFinder.getAsyncMethods());
        asyncMethods.addAll(asyncFinder.getAsyncFamilyMethods());
        hasThreads = asyncFinder.hasAsyncMethods();
    }

    @Override
    public void afterOptimizations(Program program, MethodReader method) {
        classInitializerEliminator.apply(program);
        new TeaVMCCoroutineTransformation(controller.getUnprocessedClassSource(), asyncMethods, hasThreads)
                .apply(program, method.getReference());
        shadowStackTransformer.apply(program, method);
        writeBarrierInsertion.apply(program);
    }
}
