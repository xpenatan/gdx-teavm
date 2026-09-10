package com.github.xpenatan.gdx.teavm.backends.shared.config.plugin;

import java.util.Collection;
import org.teavm.extension.introspect.IntrospectClass;
import org.teavm.extension.introspect.IntrospectMember;
import org.teavm.extension.spi.reflection.SimpleReflectionPolicy;

/** Exposes the registered classes through TeaVM's compiler extension API. */
public class TeaReflectionPolicy extends SimpleReflectionPolicy {

    @Override
    protected void setup() {
        // Backend plugins can register classes after policy initialization; consult the live registry.
        // Preserve full access, including non-public members and parameterized constructors.
        selectClasses(cls -> TeaReflectionSupplier.containsReflection(cls.name()))
                .foundByName()
                .reflectableMembers(member -> true);
    }

    @Override
    public Collection<IntrospectMember> classAccessibleMembers(IntrospectClass<?> cls) {
        Collection<IntrospectMember> members = super.classAccessibleMembers(cls);
        if(TeaReflectionSupplier.printDebugLogs) {
            System.out.println("classAccessibleMembers: " + cls.name() + " = " + members);
        }
        return members;
    }

    @Override
    public boolean isClassFoundByName(IntrospectClass<?> cls) {
        boolean found = super.isClassFoundByName(cls);
        if(TeaReflectionSupplier.printDebugLogs) {
            System.out.println("isClassFoundByName: " + cls.name() + " = " + found);
        }
        return found;
    }
}
