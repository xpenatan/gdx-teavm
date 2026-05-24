package com.github.xpenatan.gdx.teavm.backends.shared.config.plugins.optimizations;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.github.xpenatan.gdx.teavm.backends.shared.config.plugins.optimizations.substitutions.GdxTeaVMSpriteBatchSubstitution;
import com.github.xpenatan.gdx.teavm.backends.shared.config.plugins.optimizations.substitutions.GdxTeaVMSpriteSubstitution;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.teavm.model.BasicBlock;
import org.teavm.model.ClassHolder;
import org.teavm.model.ClassHolderTransformer;
import org.teavm.model.ClassHolderTransformerContext;
import org.teavm.model.ClassHierarchy;
import org.teavm.model.ClassReader;
import org.teavm.model.ElementModifier;
import org.teavm.model.FieldReference;
import org.teavm.model.Incoming;
import org.teavm.model.MethodDescriptor;
import org.teavm.model.MethodHolder;
import org.teavm.model.MethodReader;
import org.teavm.model.MethodReference;
import org.teavm.model.Phi;
import org.teavm.model.Program;
import org.teavm.model.ReferenceCache;
import org.teavm.model.ValueType;
import org.teavm.model.Variable;
import org.teavm.model.instructions.ArrayLengthInstruction;
import org.teavm.model.instructions.BinaryBranchingInstruction;
import org.teavm.model.instructions.BinaryInstruction;
import org.teavm.model.instructions.AssignInstruction;
import org.teavm.model.instructions.ArrayElementType;
import org.teavm.model.instructions.CastInstruction;
import org.teavm.model.instructions.ConstructArrayInstruction;
import org.teavm.model.instructions.ConstructInstruction;
import org.teavm.model.instructions.EmptyInstruction;
import org.teavm.model.instructions.ExitInstruction;
import org.teavm.model.instructions.FloatConstantInstruction;
import org.teavm.model.instructions.GetElementInstruction;
import org.teavm.model.instructions.GetFieldInstruction;
import org.teavm.model.Instruction;
import org.teavm.model.instructions.IntegerConstantInstruction;
import org.teavm.model.instructions.InvocationType;
import org.teavm.model.instructions.InvokeInstruction;
import org.teavm.model.instructions.JumpInstruction;
import org.teavm.model.instructions.NullCheckInstruction;
import org.teavm.model.instructions.BranchingInstruction;
import org.teavm.model.instructions.BinaryOperation;
import org.teavm.model.instructions.NumericOperandType;
import org.teavm.model.instructions.PutFieldInstruction;
import org.teavm.model.instructions.UnwrapArrayInstruction;
import org.teavm.model.util.ModelUtils;
import org.teavm.parsing.ClassRefsRenamer;

public class SpriteBatchDrawTransformer implements ClassHolderTransformer {

    private static final String SPRITE_BATCH_CLASS = SpriteBatch.class.getName();
    private static final String SPRITE_BATCH_SUBSTITUTION_CLASS = GdxTeaVMSpriteBatchSubstitution.class.getName();
    private static final String SPRITE_CLASS = Sprite.class.getName();
    private static final String SPRITE_SUBSTITUTION_CLASS = GdxTeaVMSpriteSubstitution.class.getName();
    private static final FieldReference SPRITE_ROTATION_FIELD = new FieldReference(SPRITE_CLASS, "rotation");
    private static final FieldReference SPRITE_SCALE_X_FIELD = new FieldReference(SPRITE_CLASS, "scaleX");
    private static final FieldReference SPRITE_SCALE_Y_FIELD = new FieldReference(SPRITE_CLASS, "scaleY");
    private static final FieldReference SPRITE_DIRTY_FIELD = new FieldReference(SPRITE_CLASS, "dirty");
    private static final MethodDescriptor DRAW_TEXTURE_ARRAY = new MethodReference(
            SpriteBatch.class, "draw", Texture.class, float[].class, int.class, int.class, void.class)
            .getDescriptor();
    private static final MethodDescriptor DRAW_TEXTURE_RECT = new MethodReference(
            SpriteBatch.class, "draw", Texture.class, float.class, float.class, float.class, float.class, void.class)
            .getDescriptor();
    private static final MethodDescriptor DRAW_TEXTURE_TRANSFORM = new MethodReference(
            SpriteBatch.class, "draw", Texture.class, float.class, float.class, float.class, float.class,
            float.class, float.class, float.class, float.class, float.class, int.class, int.class, int.class,
            int.class, boolean.class, boolean.class, void.class)
            .getDescriptor();
    private static final MethodDescriptor DRAW_SPRITE = new MethodReference(
            GdxTeaVMSpriteBatchSubstitution.class, "drawSprite", GdxTeaVMSpriteSubstitution.class, void.class)
            .getDescriptor();
    private static final MethodReference DRAW_SPRITE_TARGET = new MethodReference(
            SpriteBatch.class.getName(), "drawSprite", ValueType.object(Sprite.class.getName()), ValueType.VOID);
    private static final MethodDescriptor DRAW_SPRITE_NATIVE = new MethodReference(
            GdxTeaVMSpriteBatchSubstitution.class, "drawSpriteNative", GdxTeaVMSpriteBatchSubstitution.class,
            GdxTeaVMSpriteSubstitution.class, void.class)
            .getDescriptor();
    private static final MethodDescriptor DRAW_SPRITE_ARRAY_NATIVE = new MethodReference(
            GdxTeaVMSpriteBatchSubstitution.class, "drawSpriteArrayNative", GdxTeaVMSpriteBatchSubstitution.class,
            GdxTeaVMSpriteSubstitution[].class, int.class, float.class, float.class, void.class)
            .getDescriptor();
    private static final MethodReference DRAW_SPRITE_ARRAY_NATIVE_TARGET = new MethodReference(
            SPRITE_BATCH_CLASS, "drawSpriteArrayNative", ValueType.object(SPRITE_BATCH_CLASS),
            ValueType.arrayOf(ValueType.object(SPRITE_CLASS)), ValueType.INTEGER, ValueType.FLOAT, ValueType.FLOAT,
            ValueType.VOID);
    private static final MethodDescriptor DRAW_TEXTURE_TRANSFORM_NATIVE = new MethodReference(
            GdxTeaVMSpriteBatchSubstitution.class, "drawTextureTransformNative",
            GdxTeaVMSpriteBatchSubstitution.class, Texture.class, float.class, float.class, float.class, float.class,
            float.class, float.class, float.class, float.class, float.class, int.class, int.class, int.class,
            int.class, boolean.class, boolean.class, void.class)
            .getDescriptor();
    private static final MethodDescriptor DRAW_TEXTURE_RECT_NATIVE = new MethodReference(
            GdxTeaVMSpriteBatchSubstitution.class, "drawTextureRectNative", GdxTeaVMSpriteBatchSubstitution.class,
            Texture.class, float.class, float.class, float.class, float.class, void.class)
            .getDescriptor();
    private static final MethodDescriptor GET_VERTICES = new MethodReference(
            Sprite.class, "getVertices", float[].class)
            .getDescriptor();
    private static final MethodDescriptor UPDATE_VERTICES_NATIVE = new MethodReference(
            GdxTeaVMSpriteSubstitution.class, "updateVerticesNative", GdxTeaVMSpriteSubstitution.class, void.class)
            .getDescriptor();
    private static final MethodDescriptor DRAW_BATCH = new MethodReference(
            Sprite.class, "draw", Batch.class, void.class)
            .getDescriptor();
    private static final MethodDescriptor SPRITE_ROTATE = new MethodReference(
            Sprite.class, "rotate", float.class, void.class)
            .getDescriptor();
    private static final MethodDescriptor SPRITE_SET_SCALE = new MethodReference(
            Sprite.class, "setScale", float.class, void.class)
            .getDescriptor();
    private final ReferenceCache referenceCache = new ReferenceCache();

    @Override
    public void transformClass(ClassHolder cls, ClassHolderTransformerContext context) {
        if (SPRITE_BATCH_CLASS.equals(cls.getName())) {
            replaceMethod(cls, context, SPRITE_BATCH_SUBSTITUTION_CLASS, SPRITE_BATCH_CLASS, DRAW_TEXTURE_ARRAY);
            replaceMethod(cls, context, SPRITE_BATCH_SUBSTITUTION_CLASS, SPRITE_BATCH_CLASS, DRAW_TEXTURE_RECT);
            replaceMethod(cls, context, SPRITE_BATCH_SUBSTITUTION_CLASS, SPRITE_BATCH_CLASS, DRAW_TEXTURE_TRANSFORM);
            replaceMethod(cls, context, SPRITE_BATCH_SUBSTITUTION_CLASS, SPRITE_BATCH_CLASS, DRAW_SPRITE);
            replaceMethod(cls, context, SPRITE_BATCH_SUBSTITUTION_CLASS, SPRITE_BATCH_CLASS, DRAW_SPRITE_NATIVE);
            replaceMethod(cls, context, SPRITE_BATCH_SUBSTITUTION_CLASS, SPRITE_BATCH_CLASS,
                    DRAW_SPRITE_ARRAY_NATIVE);
            replaceMethod(cls, context, SPRITE_BATCH_SUBSTITUTION_CLASS, SPRITE_BATCH_CLASS,
                    DRAW_TEXTURE_TRANSFORM_NATIVE);
            replaceMethod(cls, context, SPRITE_BATCH_SUBSTITUTION_CLASS, SPRITE_BATCH_CLASS,
                    DRAW_TEXTURE_RECT_NATIVE);
        }
        else if (SPRITE_CLASS.equals(cls.getName())) {
            replaceMethod(cls, context, SPRITE_SUBSTITUTION_CLASS, SPRITE_CLASS, GET_VERTICES);
            replaceMethod(cls, context, SPRITE_SUBSTITUTION_CLASS, SPRITE_CLASS, UPDATE_VERTICES_NATIVE);
            replaceMethod(cls, context, SPRITE_SUBSTITUTION_CLASS, SPRITE_CLASS, DRAW_BATCH);
        }
        rewriteSpriteArrayLoops(cls, context);
        rewriteSpriteDrawCallSites(cls, context);
    }

    private void replaceMethod(ClassHolder cls, ClassHolderTransformerContext context, String substitutionClass,
            String targetClass, MethodDescriptor descriptor) {
        ClassReader substitution = context.getHierarchy().getClassSource().get(substitutionClass);
        if (substitution == null) {
            throw new IllegalStateException("Missing TeaVM substitution class: " + substitutionClass);
        }

        MethodReader substitutionMethod = substitution.getMethod(descriptor);
        if (substitutionMethod == null) {
            throw new IllegalStateException("Missing substitution method: " + descriptor);
        }

        MethodHolder originalMethod = cls.getMethod(descriptor);
        if (originalMethod != null) {
            cls.removeMethod(originalMethod);
        }

        MethodHolder methodCopy = ModelUtils.copyMethod(substitutionMethod);
        ClassRefsRenamer renamer = new ClassRefsRenamer(referenceCache,
                name -> renameSubstitutionClass(name, substitutionClass, targetClass));
        cls.addMethod(renamer.rename(methodCopy));
    }

    private String renameSubstitutionClass(String name, String substitutionClass, String targetClass) {
        if (SPRITE_BATCH_SUBSTITUTION_CLASS.equals(name)) {
            return SPRITE_BATCH_CLASS;
        }
        if (SPRITE_SUBSTITUTION_CLASS.equals(name)) {
            return SPRITE_CLASS;
        }
        return substitutionClass.equals(name) ? targetClass : name;
    }

    private void rewriteSpriteArrayLoops(ClassHolder cls, ClassHolderTransformerContext context) {
        for (MethodHolder method : new java.util.ArrayList<>(cls.getMethods())) {
            if (!method.hasProgram()) {
                continue;
            }
            SpriteArrayLoopCandidate candidate = detectSpriteArrayLoop(cls, method, context.getHierarchy());
            if (candidate != null) {
                rewriteSpriteArrayLoop(method, candidate);
            }
        }
    }

    private SpriteArrayLoopCandidate detectSpriteArrayLoop(ClassHolder cls, MethodHolder method,
            ClassHierarchy hierarchy) {
        if (method.hasModifier(ElementModifier.STATIC) || !ValueType.VOID.equals(method.getResultType())) {
            return null;
        }

        Program program = method.getProgram();
        if (program == null) {
            return null;
        }

        ValueType[] valueTypes = inferDeclaredTypes(cls, method, hierarchy);
        SourceInfo sources = collectSources(method);
        List<InvokeInstruction> draws = new java.util.ArrayList<>();
        List<InvokeInstruction> rotates = new java.util.ArrayList<>();
        List<InvokeInstruction> scales = new java.util.ArrayList<>();
        Map<Instruction, Integer> positions = new IdentityHashMap<>();
        boolean hasLoopControl = false;
        int position = 0;

        for (BasicBlock block : program.getBasicBlocks()) {
            if (block == null) {
                continue;
            }
            for (Instruction instruction : block) {
                positions.put(instruction, position++);
                if (instruction instanceof BranchingInstruction
                        || instruction instanceof BinaryBranchingInstruction
                        || instruction instanceof JumpInstruction) {
                    hasLoopControl = true;
                }
                if (instruction instanceof InvokeInstruction) {
                    InvokeInstruction invoke = (InvokeInstruction)instruction;
                    if (isSpriteInvocation(invoke, DRAW_BATCH) || isSpriteBatchDrawSpriteInvocation(invoke)) {
                        draws.add(invoke);
                    }
                    else if (isSpriteInvocation(invoke, SPRITE_ROTATE)) {
                        rotates.add(invoke);
                    }
                    else if (isSpriteInvocation(invoke, SPRITE_SET_SCALE)) {
                        scales.add(invoke);
                    }
                    else {
                        return null;
                    }
                }
                else if (!isAllowedSpriteArrayLoopInstruction(instruction)) {
                    return null;
                }
            }
        }

        if (!hasLoopControl || draws.size() != 1 || rotates.size() > 1 || scales.size() > 1) {
            return null;
        }

        InvokeInstruction draw = draws.get(0);
        DrawVariables drawVariables = drawVariables(draw);
        if (drawVariables == null) {
            return null;
        }

        Variable sprite = drawVariables.sprite;
        GetElementInstruction spriteElement = findSpriteElement(program, sources, sprite, valueTypes, hierarchy);
        if (spriteElement == null) {
            return null;
        }
        if (!allPutFieldsBelongToSpriteLoop(sources, sprite, draw, positions)) {
            return null;
        }

        ValueProvider spriteArray = providerOf(sources, method, spriteElement.getArray());
        if (!isSpriteArrayProvider(spriteArray, hierarchy)) {
            return null;
        }

        ValueProvider batch = providerOf(sources, method, drawVariables.batch);
        if (!isSpriteBatchProvider(batch, hierarchy)) {
            return null;
        }

        ValueProvider rotationDelta = new FloatConstantProvider(0.0f);
        if (!rotates.isEmpty()) {
            InvokeInstruction rotate = rotates.get(0);
            if (rotate.getInstance() == null || rotate.getArguments().size() != 1
                    || !sameVariableSource(sources, rotate.getInstance(), sprite)) {
                return null;
            }
            rotationDelta = providerOf(sources, method, rotate.getArguments().get(0));
            if (!isFloatProvider(rotationDelta)) {
                return null;
            }
        }
        else {
            ValueProvider inlinedRotationDelta = findInlinedRotateProvider(sources, method, sprite, draw, positions);
            if (inlinedRotationDelta != null) {
                rotationDelta = inlinedRotationDelta;
            }
        }

        ValueProvider scale = new FloatConstantProvider(1.0f);
        if (!scales.isEmpty()) {
            InvokeInstruction setScale = scales.get(0);
            if (setScale.getInstance() == null || setScale.getArguments().size() != 1
                    || !sameVariableSource(sources, setScale.getInstance(), sprite)) {
                return null;
            }
            scale = providerOf(sources, method, setScale.getArguments().get(0));
            if (!isFloatProvider(scale)) {
                return null;
            }
        }
        else {
            ValueProvider inlinedScale = findInlinedScaleProvider(sources, method, sprite, draw, positions);
            if (inlinedScale != null) {
                scale = inlinedScale;
            }
        }

        ValueProvider count = findLoopCountProvider(sources, method, spriteArray);
        if (!isIntProvider(count)) {
            return null;
        }

        return new SpriteArrayLoopCandidate(spriteArray, batch, count, rotationDelta, scale);
    }

    private boolean isAllowedSpriteArrayLoopInstruction(Instruction instruction) {
        return instruction instanceof AssignInstruction
                || instruction instanceof CastInstruction
                || instruction instanceof NullCheckInstruction
                || instruction instanceof EmptyInstruction
                || instruction instanceof GetFieldInstruction
                || instruction instanceof GetElementInstruction
                || instruction instanceof UnwrapArrayInstruction
                || instruction instanceof ArrayLengthInstruction
                || instruction instanceof IntegerConstantInstruction
                || instruction instanceof FloatConstantInstruction
                || instruction instanceof BinaryInstruction
                || instruction instanceof BranchingInstruction
                || instruction instanceof BinaryBranchingInstruction
                || instruction instanceof JumpInstruction
                || instruction instanceof ExitInstruction
                || instruction instanceof PutFieldInstruction;
    }

    private boolean isSpriteInvocation(InvokeInstruction invoke, MethodDescriptor descriptor) {
        MethodReference method = invoke.getMethod();
        return SPRITE_CLASS.equals(method.getClassName()) && descriptor.equals(method.getDescriptor());
    }

    private boolean isSpriteBatchDrawSpriteInvocation(InvokeInstruction invoke) {
        MethodReference method = invoke.getMethod();
        return SPRITE_BATCH_CLASS.equals(method.getClassName())
                && "drawSprite".equals(method.getName())
                && method.parameterCount() == 1
                && ValueType.VOID.equals(method.getReturnType())
                && ValueType.object(SPRITE_CLASS).equals(method.parameterType(0));
    }

    private DrawVariables drawVariables(InvokeInstruction draw) {
        if (isSpriteInvocation(draw, DRAW_BATCH)) {
            if (draw.getInstance() == null || draw.getArguments().size() != 1) {
                return null;
            }
            return new DrawVariables(draw.getInstance(), draw.getArguments().get(0));
        }
        if (isSpriteBatchDrawSpriteInvocation(draw)) {
            if (draw.getInstance() == null || draw.getArguments().size() != 1) {
                return null;
            }
            return new DrawVariables(draw.getArguments().get(0), draw.getInstance());
        }
        return null;
    }

    private boolean allPutFieldsBelongToSpriteLoop(SourceInfo sources, Variable sprite, InvokeInstruction draw,
            Map<Instruction, Integer> positions) {
        Integer drawPosition = positions.get(draw);
        for (PutFieldInstruction putField : sources.putFields) {
            if (positions.get(putField) > drawPosition || !sameVariableSource(sources, putField.getInstance(), sprite)
                    || !isAllowedInlinedSpriteField(putField.getField())) {
                return false;
            }
        }
        return true;
    }

    private boolean isAllowedInlinedSpriteField(FieldReference field) {
        return SPRITE_ROTATION_FIELD.equals(field)
                || SPRITE_SCALE_X_FIELD.equals(field)
                || SPRITE_SCALE_Y_FIELD.equals(field)
                || SPRITE_DIRTY_FIELD.equals(field);
    }

    private ValueProvider findInlinedRotateProvider(SourceInfo sources, MethodHolder method, Variable sprite,
            InvokeInstruction draw, Map<Instruction, Integer> positions) {
        ValueProvider result = null;
        Integer drawPosition = positions.get(draw);
        for (PutFieldInstruction putField : sources.putFields) {
            if (!SPRITE_ROTATION_FIELD.equals(putField.getField())
                    || positions.get(putField) > drawPosition
                    || !sameVariableSource(sources, putField.getInstance(), sprite)) {
                continue;
            }

            BinaryInstruction add = sources.binaries.get(putField.getValue());
            if (add == null || add.getOperation() != BinaryOperation.ADD
                    || add.getOperandType() != NumericOperandType.FLOAT) {
                return null;
            }

            ValueProvider delta = null;
            if (isSpriteFieldRead(sources, add.getFirstOperand(), sprite, SPRITE_ROTATION_FIELD)) {
                delta = providerOf(sources, method, add.getSecondOperand());
            }
            else if (isSpriteFieldRead(sources, add.getSecondOperand(), sprite, SPRITE_ROTATION_FIELD)) {
                delta = providerOf(sources, method, add.getFirstOperand());
            }
            if (!isFloatProvider(delta)) {
                return null;
            }
            if (result != null && !result.equals(delta)) {
                return null;
            }
            result = delta;
        }
        return result;
    }

    private ValueProvider findInlinedScaleProvider(SourceInfo sources, MethodHolder method, Variable sprite,
            InvokeInstruction draw, Map<Instruction, Integer> positions) {
        ValueProvider scaleX = null;
        ValueProvider scaleY = null;
        Integer drawPosition = positions.get(draw);
        for (PutFieldInstruction putField : sources.putFields) {
            if (positions.get(putField) > drawPosition
                    || !sameVariableSource(sources, putField.getInstance(), sprite)) {
                continue;
            }
            if (SPRITE_SCALE_X_FIELD.equals(putField.getField())) {
                scaleX = providerOf(sources, method, putField.getValue());
            }
            else if (SPRITE_SCALE_Y_FIELD.equals(putField.getField())) {
                scaleY = providerOf(sources, method, putField.getValue());
            }
        }
        if (scaleX == null && scaleY == null) {
            return null;
        }
        if (!isFloatProvider(scaleX) || !scaleX.equals(scaleY)) {
            return null;
        }
        return scaleX;
    }

    private boolean isSpriteFieldRead(SourceInfo sources, Variable variable, Variable sprite, FieldReference field) {
        FieldRead read = fieldReadOf(sources, variable, new HashSet<>());
        return read != null && field.equals(read.field) && sameVariableSource(sources, read.instance, sprite);
    }

    private FieldRead fieldReadOf(SourceInfo sources, Variable variable, Set<Integer> visiting) {
        if (variable == null || !visiting.add(variable.getIndex())) {
            return null;
        }
        FieldRead read = sources.fieldReads.get(variable);
        if (read != null) {
            return read;
        }
        Variable alias = sources.aliases.get(variable);
        if (alias != null) {
            return fieldReadOf(sources, alias, visiting);
        }
        List<Variable> incomings = sources.phis.get(variable);
        if (incomings != null && !incomings.isEmpty()) {
            FieldRead result = null;
            for (Variable incoming : incomings) {
                FieldRead incomingRead = fieldReadOf(sources, incoming, new HashSet<>(visiting));
                if (incomingRead == null) {
                    return null;
                }
                if (result == null) {
                    result = incomingRead;
                }
                else if (!result.equals(incomingRead)) {
                    return null;
                }
            }
            return result;
        }
        return null;
    }

    private GetElementInstruction findSpriteElement(Program program, SourceInfo sources, Variable sprite,
            ValueType[] valueTypes, ClassHierarchy hierarchy) {
        for (BasicBlock block : program.getBasicBlocks()) {
            if (block == null) {
                continue;
            }
            for (Instruction instruction : block) {
                if (!(instruction instanceof GetElementInstruction)) {
                    continue;
                }
                GetElementInstruction getElement = (GetElementInstruction)instruction;
                if (getElement.getType() != ArrayElementType.OBJECT
                        || !sameVariableSource(sources, getElement.getReceiver(), sprite)) {
                    continue;
                }
                ValueType arrayType = typeOf(valueTypes, getElement.getArray());
                if (isSpriteArrayType(arrayType, hierarchy)) {
                    return getElement;
                }
            }
        }
        return null;
    }

    private ValueProvider findLoopCountProvider(SourceInfo sources, MethodHolder method, ValueProvider spriteArray) {
        for (Map.Entry<Variable, Variable> entry : sources.arrayLengths.entrySet()) {
            ValueProvider array = providerOf(sources, method, entry.getValue());
            if (spriteArray.equals(array)) {
                return providerOf(sources, method, entry.getKey());
            }
        }

        Set<ValueProvider> intFields = new HashSet<>();
        for (FieldValueProvider field : sources.fields.values()) {
            if (ValueType.INTEGER.equals(field.type)) {
                intFields.add(field);
            }
        }
        return intFields.size() == 1 ? intFields.iterator().next() : null;
    }

    private boolean sameVariableSource(SourceInfo sources, Variable first, Variable second) {
        if (first == null || second == null) {
            return first == second;
        }
        if (first.getIndex() == second.getIndex()) {
            return true;
        }
        Variable firstRoot = rootVariable(sources, first, new HashSet<>());
        Variable secondRoot = rootVariable(sources, second, new HashSet<>());
        return firstRoot != null && secondRoot != null && firstRoot.getIndex() == secondRoot.getIndex();
    }

    private Variable rootVariable(SourceInfo sources, Variable variable, Set<Integer> visiting) {
        if (variable == null || !visiting.add(variable.getIndex())) {
            return variable;
        }
        Variable alias = sources.aliases.get(variable);
        if (alias != null) {
            return rootVariable(sources, alias, visiting);
        }
        List<Variable> incomings = sources.phis.get(variable);
        if (incomings != null && !incomings.isEmpty()) {
            Variable root = null;
            for (Variable incoming : incomings) {
                Variable incomingRoot = rootVariable(sources, incoming, visiting);
                if (incomingRoot == null) {
                    return variable;
                }
                if (root == null) {
                    root = incomingRoot;
                }
                else if (root.getIndex() != incomingRoot.getIndex()) {
                    return variable;
                }
            }
            return root;
        }
        return variable;
    }

    private SourceInfo collectSources(MethodHolder method) {
        SourceInfo sources = new SourceInfo();
        Program program = method.getProgram();
        for (BasicBlock block : program.getBasicBlocks()) {
            if (block == null) {
                continue;
            }
            for (Phi phi : block.getPhis()) {
                List<Variable> incomings = new java.util.ArrayList<>();
                for (Incoming incoming : phi.getIncomings()) {
                    incomings.add(incoming.getValue());
                }
                sources.phis.put(phi.getReceiver(), incomings);
            }
            for (Instruction instruction : block) {
                if (instruction instanceof AssignInstruction) {
                    AssignInstruction assign = (AssignInstruction)instruction;
                    sources.aliases.put(assign.getReceiver(), assign.getAssignee());
                }
                else if (instruction instanceof NullCheckInstruction) {
                    NullCheckInstruction nullCheck = (NullCheckInstruction)instruction;
                    sources.aliases.put(nullCheck.getReceiver(), nullCheck.getValue());
                }
                else if (instruction instanceof CastInstruction) {
                    CastInstruction cast = (CastInstruction)instruction;
                    sources.aliases.put(cast.getReceiver(), cast.getValue());
                }
                else if (instruction instanceof GetFieldInstruction) {
                    GetFieldInstruction getField = (GetFieldInstruction)instruction;
                    sources.fieldReads.put(getField.getReceiver(), new FieldRead(getField.getField(),
                            getField.getInstance(), getField.getFieldType()));
                    if (getField.getInstance() != null && getField.getInstance().getIndex() == 0) {
                        sources.fields.put(getField.getReceiver(), new FieldValueProvider(getField.getField(),
                                getField.getFieldType()));
                    }
                }
                else if (instruction instanceof IntegerConstantInstruction) {
                    IntegerConstantInstruction constant = (IntegerConstantInstruction)instruction;
                    sources.constants.put(constant.getReceiver(), new IntConstantProvider(constant.getConstant()));
                }
                else if (instruction instanceof FloatConstantInstruction) {
                    FloatConstantInstruction constant = (FloatConstantInstruction)instruction;
                    sources.constants.put(constant.getReceiver(), new FloatConstantProvider(constant.getConstant()));
                }
                else if (instruction instanceof ArrayLengthInstruction) {
                    ArrayLengthInstruction arrayLength = (ArrayLengthInstruction)instruction;
                    sources.arrayLengths.put(arrayLength.getReceiver(), arrayLength.getArray());
                }
                else if (instruction instanceof UnwrapArrayInstruction) {
                    UnwrapArrayInstruction unwrapArray = (UnwrapArrayInstruction)instruction;
                    sources.aliases.put(unwrapArray.getReceiver(), unwrapArray.getArray());
                }
                else if (instruction instanceof BinaryInstruction) {
                    BinaryInstruction binary = (BinaryInstruction)instruction;
                    if (binary.getReceiver() != null) {
                        sources.binaries.put(binary.getReceiver(), binary);
                    }
                }
                else if (instruction instanceof PutFieldInstruction) {
                    sources.putFields.add((PutFieldInstruction)instruction);
                }
            }
        }
        return sources;
    }

    private ValueProvider providerOf(SourceInfo sources, MethodHolder method, Variable variable) {
        return providerOf(sources, method, variable, new HashSet<>());
    }

    private ValueProvider providerOf(SourceInfo sources, MethodHolder method, Variable variable, Set<Integer> visiting) {
        if (variable == null || !visiting.add(variable.getIndex())) {
            return null;
        }

        ValueProvider field = sources.fields.get(variable);
        if (field != null) {
            return field;
        }
        ValueProvider constant = sources.constants.get(variable);
        if (constant != null) {
            return constant;
        }
        Variable array = sources.arrayLengths.get(variable);
        if (array != null) {
            ValueProvider arrayProvider = providerOf(sources, method, array, visiting);
            return arrayProvider != null ? new ArrayLengthProvider(arrayProvider) : null;
        }
        Variable alias = sources.aliases.get(variable);
        if (alias != null) {
            return providerOf(sources, method, alias, visiting);
        }
        List<Variable> incomings = sources.phis.get(variable);
        if (incomings != null && !incomings.isEmpty()) {
            ValueProvider provider = null;
            for (Variable incoming : incomings) {
                ValueProvider incomingProvider = providerOf(sources, method, incoming, new HashSet<>(visiting));
                if (incomingProvider == null) {
                    return null;
                }
                if (provider == null) {
                    provider = incomingProvider;
                }
                else if (!provider.equals(incomingProvider)) {
                    return null;
                }
            }
            return provider;
        }

        int parameterStart = method.hasModifier(ElementModifier.STATIC) ? 0 : 1;
        for (int i = 0; i < method.parameterCount(); i++) {
            int variableIndex = parameterStart + i;
            if (variable.getIndex() == variableIndex) {
                return new ParameterProvider(variableIndex, method.parameterType(i));
            }
        }
        return null;
    }

    private boolean isSpriteArrayProvider(ValueProvider provider, ClassHierarchy hierarchy) {
        return provider != null && isSpriteArrayType(provider.type, hierarchy);
    }

    private boolean isSpriteBatchProvider(ValueProvider provider, ClassHierarchy hierarchy) {
        if (provider == null || !(provider.type instanceof ValueType.Object)) {
            return false;
        }
        return hierarchy.isSuperType(SPRITE_BATCH_CLASS, ((ValueType.Object)provider.type).getClassName(), false);
    }

    private boolean isSpriteArrayType(ValueType type, ClassHierarchy hierarchy) {
        if (!(type instanceof ValueType.Array)) {
            return false;
        }
        ValueType itemType = ((ValueType.Array)type).getItemType();
        if (!(itemType instanceof ValueType.Object)) {
            return false;
        }
        return hierarchy.isSuperType(SPRITE_CLASS, ((ValueType.Object)itemType).getClassName(), false);
    }

    private boolean isFloatProvider(ValueProvider provider) {
        return provider != null && ValueType.FLOAT.equals(provider.type);
    }

    private boolean isIntProvider(ValueProvider provider) {
        return provider != null && ValueType.INTEGER.equals(provider.type);
    }

    private void rewriteSpriteArrayLoop(MethodHolder method, SpriteArrayLoopCandidate candidate) {
        Program replacement = new Program();
        int parameterVariables = method.parameterCount() + (method.hasModifier(ElementModifier.STATIC) ? 0 : 1);
        for (int i = 0; i < parameterVariables; i++) {
            replacement.createVariable();
        }

        BasicBlock block = replacement.createBasicBlock();
        Variable self = replacement.variableAt(0);
        Map<ValueProvider, Variable> emitted = new HashMap<>();
        Variable spriteArray = candidate.spriteArray.emit(replacement, block, self, emitted);
        Variable batch = candidate.batch.emit(replacement, block, self, emitted);
        Variable count = candidate.count.emit(replacement, block, self, emitted);
        Variable rotationDelta = candidate.rotationDelta.emit(replacement, block, self, emitted);
        Variable scale = candidate.scale.emit(replacement, block, self, emitted);

        InvokeInstruction invoke = new InvokeInstruction();
        invoke.setType(InvocationType.SPECIAL);
        invoke.setMethod(DRAW_SPRITE_ARRAY_NATIVE_TARGET);
        invoke.setArguments(batch, spriteArray, count, rotationDelta, scale);
        block.add(invoke);

        block.add(new ExitInstruction());
        method.setProgram(replacement);
    }

    private void rewriteSpriteDrawCallSites(ClassHolder cls, ClassHolderTransformerContext context) {
        for (MethodHolder method : new java.util.ArrayList<>(cls.getMethods())) {
            if (!method.hasProgram()) {
                continue;
            }
            rewriteSpriteDrawCallSites(cls, method, context.getHierarchy());
        }
    }

    private void rewriteSpriteDrawCallSites(ClassHolder cls, MethodHolder method, ClassHierarchy hierarchy) {
        Program program = method.getProgram();
        if (program == null) {
            return;
        }

        ValueType[] valueTypes = inferDeclaredTypes(cls, method, hierarchy);
        for (org.teavm.model.BasicBlock block : program.getBasicBlocks()) {
            if (block == null) {
                continue;
            }
            for (Instruction instruction = block.getFirstInstruction(); instruction != null;) {
                Instruction next = instruction.getNext();
                if (instruction instanceof InvokeInstruction) {
                    rewriteSpriteDrawCall((InvokeInstruction)instruction, valueTypes, hierarchy);
                }
                instruction = next;
            }
        }
    }

    private void rewriteSpriteDrawCall(InvokeInstruction invoke, ValueType[] valueTypes, ClassHierarchy hierarchy) {
        MethodReference method = invoke.getMethod();
        if (!SPRITE_CLASS.equals(method.getClassName()) || !DRAW_BATCH.equals(method.getDescriptor())
                || invoke.getArguments().size() != 1 || invoke.getInstance() == null) {
            return;
        }

        Variable batch = invoke.getArguments().get(0);
        if (!isKnownSpriteBatch(batch, valueTypes, hierarchy)) {
            return;
        }

        Variable sprite = invoke.getInstance();
        invoke.setMethod(DRAW_SPRITE_TARGET);
        invoke.setInstance(batch);
        invoke.setArguments(sprite);
        invoke.setType(InvocationType.VIRTUAL);
    }

    private boolean isKnownSpriteBatch(Variable variable, ValueType[] valueTypes, ClassHierarchy hierarchy) {
        if (variable == null || variable.getIndex() < 0 || variable.getIndex() >= valueTypes.length) {
            return false;
        }
        ValueType type = valueTypes[variable.getIndex()];
        if (!(type instanceof ValueType.Object)) {
            return false;
        }
        String className = ((ValueType.Object)type).getClassName();
        return hierarchy.isSuperType(SPRITE_BATCH_CLASS, className, false);
    }

    private ValueType[] inferDeclaredTypes(ClassHolder cls, MethodHolder method, ClassHierarchy hierarchy) {
        Program program = method.getProgram();
        ValueType[] types = new ValueType[program.variableCount()];
        if (!method.hasModifier(ElementModifier.STATIC) && program.variableCount() > 0) {
            types[0] = ValueType.object(cls.getName());
        }
        for (int i = 0; i < method.parameterCount(); i++) {
            int variableIndex = i + 1;
            if (variableIndex < types.length) {
                types[variableIndex] = method.parameterType(i);
            }
        }

        boolean changed;
        do {
            changed = false;
            for (org.teavm.model.BasicBlock block : program.getBasicBlocks()) {
                if (block == null) {
                    continue;
                }
                for (Phi phi : block.getPhis()) {
                    ValueType merged = null;
                    for (org.teavm.model.Incoming incoming : phi.getIncomings()) {
                        merged = mergeTypes(merged, typeOf(types, incoming.getValue()), hierarchy);
                    }
                    changed |= updateType(types, phi.getReceiver(), merged, hierarchy);
                }
                for (Instruction instruction : block) {
                    changed |= inferDeclaredType(types, instruction, hierarchy);
                }
            }
        } while (changed);
        return types;
    }

    private boolean inferDeclaredType(ValueType[] types, Instruction instruction, ClassHierarchy hierarchy) {
        if (instruction instanceof AssignInstruction) {
            AssignInstruction assign = (AssignInstruction)instruction;
            return updateType(types, assign.getReceiver(), typeOf(types, assign.getAssignee()), hierarchy);
        }
        if (instruction instanceof NullCheckInstruction) {
            NullCheckInstruction nullCheck = (NullCheckInstruction)instruction;
            return updateType(types, nullCheck.getReceiver(), typeOf(types, nullCheck.getValue()), hierarchy);
        }
        if (instruction instanceof CastInstruction) {
            CastInstruction cast = (CastInstruction)instruction;
            return updateType(types, cast.getReceiver(), cast.getTargetType(), hierarchy);
        }
        if (instruction instanceof ConstructInstruction) {
            ConstructInstruction construct = (ConstructInstruction)instruction;
            return updateType(types, construct.getReceiver(), ValueType.object(construct.getType()), hierarchy);
        }
        if (instruction instanceof ConstructArrayInstruction) {
            ConstructArrayInstruction construct = (ConstructArrayInstruction)instruction;
            return updateType(types, construct.getReceiver(), ValueType.arrayOf(construct.getItemType()), hierarchy);
        }
        if (instruction instanceof GetFieldInstruction) {
            GetFieldInstruction getField = (GetFieldInstruction)instruction;
            return updateType(types, getField.getReceiver(), getField.getFieldType(), hierarchy);
        }
        if (instruction instanceof GetElementInstruction) {
            GetElementInstruction getElement = (GetElementInstruction)instruction;
            ValueType arrayType = typeOf(types, getElement.getArray());
            if (arrayType instanceof ValueType.Array) {
                return updateType(types, getElement.getReceiver(), ((ValueType.Array)arrayType).getItemType(),
                        hierarchy);
            }
        }
        if (instruction instanceof UnwrapArrayInstruction) {
            UnwrapArrayInstruction unwrapArray = (UnwrapArrayInstruction)instruction;
            return updateType(types, unwrapArray.getReceiver(), typeOf(types, unwrapArray.getArray()), hierarchy);
        }
        if (instruction instanceof InvokeInstruction) {
            InvokeInstruction invoke = (InvokeInstruction)instruction;
            return updateType(types, invoke.getReceiver(), invoke.getMethod().getReturnType(), hierarchy);
        }
        return false;
    }

    private ValueType typeOf(ValueType[] types, Variable variable) {
        if (variable == null || variable.getIndex() < 0 || variable.getIndex() >= types.length) {
            return null;
        }
        return types[variable.getIndex()];
    }

    private boolean updateType(ValueType[] types, Variable variable, ValueType type, ClassHierarchy hierarchy) {
        if (variable == null || type == null || variable.getIndex() < 0 || variable.getIndex() >= types.length) {
            return false;
        }
        ValueType merged = mergeTypes(types[variable.getIndex()], type, hierarchy);
        if (merged == null || merged.equals(types[variable.getIndex()])) {
            return false;
        }
        types[variable.getIndex()] = merged;
        return true;
    }

    private ValueType mergeTypes(ValueType current, ValueType incoming, ClassHierarchy hierarchy) {
        if (incoming == null) {
            return current;
        }
        if (current == null || current.equals(incoming)) {
            return incoming;
        }
        if (current instanceof ValueType.Object && incoming instanceof ValueType.Object) {
            String currentClass = ((ValueType.Object)current).getClassName();
            String incomingClass = ((ValueType.Object)incoming).getClassName();
            if (hierarchy.isSuperType(currentClass, incomingClass, false)) {
                return current;
            }
            if (hierarchy.isSuperType(incomingClass, currentClass, false)) {
                return incoming;
            }
            return ValueType.object(Object.class.getName());
        }
        if (current instanceof ValueType.Array && incoming instanceof ValueType.Array) {
            return current.equals(incoming) ? current : ValueType.object(Object.class.getName());
        }
        return current;
    }

    private static final class SpriteArrayLoopCandidate {
        final ValueProvider spriteArray;
        final ValueProvider batch;
        final ValueProvider count;
        final ValueProvider rotationDelta;
        final ValueProvider scale;

        SpriteArrayLoopCandidate(ValueProvider spriteArray, ValueProvider batch, ValueProvider count,
                ValueProvider rotationDelta, ValueProvider scale) {
            this.spriteArray = spriteArray;
            this.batch = batch;
            this.count = count;
            this.rotationDelta = rotationDelta;
            this.scale = scale;
        }
    }

    private static final class DrawVariables {
        final Variable sprite;
        final Variable batch;

        DrawVariables(Variable sprite, Variable batch) {
            this.sprite = sprite;
            this.batch = batch;
        }
    }

    private static final class SourceInfo {
        final Map<Variable, Variable> aliases = new HashMap<>();
        final Map<Variable, FieldValueProvider> fields = new HashMap<>();
        final Map<Variable, FieldRead> fieldReads = new HashMap<>();
        final Map<Variable, ValueProvider> constants = new HashMap<>();
        final Map<Variable, Variable> arrayLengths = new HashMap<>();
        final Map<Variable, BinaryInstruction> binaries = new HashMap<>();
        final Map<Variable, List<Variable>> phis = new HashMap<>();
        final List<PutFieldInstruction> putFields = new java.util.ArrayList<>();
    }

    private static final class FieldRead {
        final FieldReference field;
        final Variable instance;
        final ValueType type;

        FieldRead(FieldReference field, Variable instance, ValueType type) {
            this.field = field;
            this.instance = instance;
            this.type = type;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof FieldRead)) {
                return false;
            }
            FieldRead other = (FieldRead)obj;
            int instanceIndex = instance != null ? instance.getIndex() : -1;
            int otherInstanceIndex = other.instance != null ? other.instance.getIndex() : -1;
            return field.equals(other.field) && instanceIndex == otherInstanceIndex && Objects.equals(type, other.type);
        }

        @Override
        public int hashCode() {
            return ((field.hashCode() * 31) + (instance != null ? instance.getIndex() : -1)) * 31
                    + type.hashCode();
        }
    }

    private abstract static class ValueProvider {
        final ValueType type;

        ValueProvider(ValueType type) {
            this.type = type;
        }

        final Variable emit(Program program, BasicBlock block, Variable self, Map<ValueProvider, Variable> emitted) {
            Variable cached = emitted.get(this);
            if (cached != null) {
                return cached;
            }
            Variable result = emitNew(program, block, self, emitted);
            emitted.put(this, result);
            return result;
        }

        abstract Variable emitNew(Program program, BasicBlock block, Variable self,
                Map<ValueProvider, Variable> emitted);
    }

    private static final class FieldValueProvider extends ValueProvider {
        final FieldReference field;

        FieldValueProvider(FieldReference field, ValueType type) {
            super(type);
            this.field = field;
        }

        @Override
        Variable emitNew(Program program, BasicBlock block, Variable self, Map<ValueProvider, Variable> emitted) {
            Variable receiver = program.createVariable();
            GetFieldInstruction getField = new GetFieldInstruction();
            getField.setInstance(self);
            getField.setField(field);
            getField.setFieldType(type);
            getField.setReceiver(receiver);
            block.add(getField);
            return receiver;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof FieldValueProvider)) {
                return false;
            }
            FieldValueProvider other = (FieldValueProvider)obj;
            return field.equals(other.field) && Objects.equals(type, other.type);
        }

        @Override
        public int hashCode() {
            return field.hashCode() * 31 + type.hashCode();
        }
    }

    private static final class ParameterProvider extends ValueProvider {
        final int variableIndex;

        ParameterProvider(int variableIndex, ValueType type) {
            super(type);
            this.variableIndex = variableIndex;
        }

        @Override
        Variable emitNew(Program program, BasicBlock block, Variable self, Map<ValueProvider, Variable> emitted) {
            return program.variableAt(variableIndex);
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ParameterProvider && variableIndex == ((ParameterProvider)obj).variableIndex
                    && Objects.equals(type, ((ParameterProvider)obj).type);
        }

        @Override
        public int hashCode() {
            return variableIndex * 31 + type.hashCode();
        }
    }

    private static final class IntConstantProvider extends ValueProvider {
        final int value;

        IntConstantProvider(int value) {
            super(ValueType.INTEGER);
            this.value = value;
        }

        @Override
        Variable emitNew(Program program, BasicBlock block, Variable self, Map<ValueProvider, Variable> emitted) {
            Variable receiver = program.createVariable();
            IntegerConstantInstruction constant = new IntegerConstantInstruction();
            constant.setConstant(value);
            constant.setReceiver(receiver);
            block.add(constant);
            return receiver;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof IntConstantProvider && value == ((IntConstantProvider)obj).value;
        }

        @Override
        public int hashCode() {
            return value;
        }
    }

    private static final class FloatConstantProvider extends ValueProvider {
        final float value;

        FloatConstantProvider(float value) {
            super(ValueType.FLOAT);
            this.value = value;
        }

        @Override
        Variable emitNew(Program program, BasicBlock block, Variable self, Map<ValueProvider, Variable> emitted) {
            Variable receiver = program.createVariable();
            FloatConstantInstruction constant = new FloatConstantInstruction();
            constant.setConstant(value);
            constant.setReceiver(receiver);
            block.add(constant);
            return receiver;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof FloatConstantProvider
                    && Float.floatToIntBits(value) == Float.floatToIntBits(((FloatConstantProvider)obj).value);
        }

        @Override
        public int hashCode() {
            return Float.floatToIntBits(value);
        }
    }

    private static final class ArrayLengthProvider extends ValueProvider {
        final ValueProvider array;

        ArrayLengthProvider(ValueProvider array) {
            super(ValueType.INTEGER);
            this.array = array;
        }

        @Override
        Variable emitNew(Program program, BasicBlock block, Variable self, Map<ValueProvider, Variable> emitted) {
            Variable arrayVariable = array.emit(program, block, self, emitted);
            Variable receiver = program.createVariable();
            ArrayLengthInstruction arrayLength = new ArrayLengthInstruction();
            arrayLength.setArray(arrayVariable);
            arrayLength.setReceiver(receiver);
            block.add(arrayLength);
            return receiver;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ArrayLengthProvider && array.equals(((ArrayLengthProvider)obj).array);
        }

        @Override
        public int hashCode() {
            return array.hashCode() * 31 + 1;
        }
    }
}
