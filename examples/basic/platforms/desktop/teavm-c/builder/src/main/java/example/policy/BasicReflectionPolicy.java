package example.policy;

import org.teavm.extension.spi.reflection.SimpleReflectionPolicy;
import com.github.xpenatan.gdx.teavm.backends.shared.config.reflection.GdxReflectionHelper;

/** Reflection required by the basic sample's Json, Skin, VisUI and pooling demonstrations. */

public class BasicReflectionPolicy extends SimpleReflectionPolicy {
    @Override
    protected void setup() {
        GdxReflectionHelper.applyDefaults(this::selectClasses);
        selectClasses(type -> type.name().startsWith("com.kotcrab.vis.ui.")
                && (type.name().endsWith("Style") || type.name().endsWith(".Sizes")))
                .foundByName().reflectableFields(field -> true)
                .reflectableMethods(method -> method.isConstructor() && withParameterCount(0).test(method));
        selectClasses(type -> type.name().startsWith(
                "com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.JsonTest$"))
                .foundByName().reflectableFields(field -> true)
                .reflectableMethods(method -> method.isConstructor() && withParameterCount(0).test(method));
        selectClass("com.badlogic.gdx.math.Vector2").foundByName().reflectableMembers(member -> true);
        for(String name : new String[] {"java.util.HashMap", "java.util.LinkedHashMap", "java.util.ArrayList"}) {
            selectClass(name).foundByName()
                    .reflectableMethods(method -> method.isConstructor() && withParameterCount(0).test(method));
        }
    }
}
