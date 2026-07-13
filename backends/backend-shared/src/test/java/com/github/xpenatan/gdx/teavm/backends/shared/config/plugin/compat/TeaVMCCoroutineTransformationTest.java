package com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.compat;

import static com.google.common.truth.Truth.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import org.teavm.model.BasicBlock;
import org.teavm.model.ClassHolder;
import org.teavm.model.Instruction;
import org.teavm.model.MethodHolder;
import org.teavm.model.MethodReference;
import org.teavm.model.MutableClassHolderSource;
import org.teavm.model.Program;
import org.teavm.model.ValueType;
import org.teavm.model.instructions.CastInstruction;
import org.teavm.model.instructions.ExitInstruction;
import org.teavm.model.instructions.InvocationType;
import org.teavm.model.instructions.InvokeInstruction;
import org.teavm.model.instructions.JumpInstruction;

public class TeaVMCCoroutineTransformationTest {

    @Test
    public void detectsAsyncMethodDeclaredByInterface() {
        MutableClassHolderSource classSource = new MutableClassHolderSource();
        MethodReference interfaceMethod = new MethodReference("test.AsyncApi", "load", ValueType.VOID);

        ClassHolder asyncInterface = new ClassHolder(interfaceMethod.getClassName());
        asyncInterface.addMethod(new MethodHolder(interfaceMethod.getDescriptor()));
        classSource.putClassHolder(asyncInterface);

        ClassHolder implementation = new ClassHolder("test.AsyncImplementation");
        implementation.getInterfaces().add(interfaceMethod.getClassName());
        implementation.addMethod(new MethodHolder(interfaceMethod.getDescriptor()));
        classSource.putClassHolder(implementation);

        TeaVMCCoroutineTransformation transformation = new TeaVMCCoroutineTransformation(
                classSource, Set.of(interfaceMethod), true);

        assertThat(transformation.isAsyncSplit(new MethodReference(
                implementation.getName(), interfaceMethod.getDescriptor()))).isTrue();
    }

    @Test
    public void restoresTypedArrayThroughWeakCast() {
        MethodReference asyncMethod = new MethodReference("test.AsyncApi", "load", ValueType.VOID);
        MethodReference testMethod = new MethodReference("test.Test", "run",
                ValueType.arrayOf(ValueType.INTEGER), ValueType.arrayOf(ValueType.INTEGER));

        Program program = new Program();
        program.createVariable();
        program.createVariable();
        BasicBlock entryBlock = program.createBasicBlock();
        BasicBlock bodyBlock = program.createBasicBlock();

        JumpInstruction jump = new JumpInstruction();
        jump.setTarget(bodyBlock);
        entryBlock.add(jump);

        InvokeInstruction invoke = new InvokeInstruction();
        invoke.setType(InvocationType.SPECIAL);
        invoke.setMethod(asyncMethod);
        bodyBlock.add(invoke);

        ExitInstruction exit = new ExitInstruction();
        exit.setValueToReturn(program.variableAt(1));
        bodyBlock.add(exit);

        TeaVMCCoroutineTransformation transformation = new TeaVMCCoroutineTransformation(
                new MutableClassHolderSource(), Set.of(asyncMethod), true);
        transformation.apply(program, testMethod);

        List<CastInstruction> casts = new ArrayList<>();
        for(BasicBlock transformedBlock : program.getBasicBlocks()) {
            for(Instruction instruction : transformedBlock) {
                if(instruction instanceof CastInstruction) {
                    casts.add((CastInstruction)instruction);
                }
            }
        }

        assertThat(casts).hasSize(1);
        assertThat(casts.get(0).getTargetType()).isEqualTo(ValueType.arrayOf(ValueType.INTEGER));
        assertThat(casts.get(0).isWeak()).isTrue();
    }

    @Test
    public void compatibilityIsLimitedToTeaVM0150() {
        assertThat(TeaVMCCompatibilityTarget.isRequired("0.15.0")).isTrue();
        assertThat(TeaVMCCompatibilityTarget.isRequired("0.15.1")).isFalse();
        assertThat(TeaVMCCompatibilityTarget.isRequired("0.16.0")).isFalse();
    }

    @Test
    public void detectsResolvedTeaVMVersion() {
        String version = TeaVMCCompatibilityTarget.detectTeaVMVersion();

        assertThat(version).isNotNull();
        assertThat(TeaVMCCompatibilityTarget.isRequired()).isEqualTo(
                TeaVMCCompatibilityTarget.AFFECTED_TEAVM_VERSION.equals(version));
    }
}
