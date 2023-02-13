package com.github.xpenatan.gdx.backends.teavm.plugins;

import org.teavm.dependency.AbstractDependencyListener;
import org.teavm.dependency.DependencyAgent;
import org.teavm.dependency.DependencyNode;
import org.teavm.dependency.MethodDependency;

public class JavaObjectExporterDependency extends AbstractDependencyListener {
    DependencyNode node;

    @Override
    public void started(DependencyAgent agent) {
        node = agent.createNode();
    }

    @Override
    public void methodReached(DependencyAgent agent, MethodDependency method) {
        String ownerName = method.getMethod().getOwnerName();
        if(ownerName.equals("java.util.concurrent.impl.JsPromise")) {
            switch(method.getMethod().getName()) {
                case "asJsObject":
                    DependencyNode variable = method.getVariable(1);
                    variable.connect(node);
                    break;
                case "asJavaObject":
                    DependencyNode result = method.getResult();
                    node.connect(result);
                    break;
            }
        }
    }
}
