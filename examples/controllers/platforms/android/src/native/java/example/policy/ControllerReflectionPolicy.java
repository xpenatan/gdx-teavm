package example.policy;

import org.teavm.extension.Autoregistered;
import org.teavm.extension.spi.reflection.SimpleReflectionPolicy;

@Autoregistered
public class ControllerReflectionPolicy extends SimpleReflectionPolicy {
    @Override
    protected void setup() {
        for(String name : new String[] {"com.badlogic.gdx.controllers.IosControllerManager",
                "com.badlogic.gdx.controllers.android.AndroidControllers"}) {
            selectClass(name).foundByName()
                    .reflectableMethods(method -> method.isConstructor() && withParameterCount(0).test(method));
        }
    }
}
