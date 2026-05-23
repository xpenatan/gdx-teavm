package com.github.xpenatan.gdx.teavm.backends.shared.config.plugins.optimizations;

import com.github.xpenatan.gdx.teavm.backends.shared.config.plugins.optimizations.substitutions.GdxTeaVMArrayFastPaths;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.teavm.interop.Address;
import org.teavm.model.BasicBlock;
import org.teavm.model.ClassHolder;
import org.teavm.model.ClassHolderTransformer;
import org.teavm.model.ClassHolderTransformerContext;
import org.teavm.model.FieldReference;
import org.teavm.model.Instruction;
import org.teavm.model.MethodHolder;
import org.teavm.model.MethodReference;
import org.teavm.model.Program;
import org.teavm.model.ValueType;
import org.teavm.model.Variable;
import org.teavm.model.instructions.ArrayElementType;
import org.teavm.model.instructions.BinaryInstruction;
import org.teavm.model.instructions.BinaryOperation;
import org.teavm.model.instructions.BranchingInstruction;
import org.teavm.model.instructions.GetElementInstruction;
import org.teavm.model.instructions.GetFieldInstruction;
import org.teavm.model.instructions.CloneArrayInstruction;
import org.teavm.model.instructions.ConstructArrayInstruction;
import org.teavm.model.instructions.ConstructInstruction;
import org.teavm.model.instructions.ConstructMultiArrayInstruction;
import org.teavm.model.instructions.ExitInstruction;
import org.teavm.model.instructions.InitClassInstruction;
import org.teavm.model.instructions.IntegerConstantInstruction;
import org.teavm.model.instructions.InvocationType;
import org.teavm.model.instructions.InvokeInstruction;
import org.teavm.model.instructions.JumpInstruction;
import org.teavm.model.instructions.MonitorEnterInstruction;
import org.teavm.model.instructions.MonitorExitInstruction;
import org.teavm.model.instructions.NumericOperandType;
import org.teavm.model.instructions.PutElementInstruction;
import org.teavm.model.instructions.PutFieldInstruction;
import org.teavm.model.instructions.RaiseInstruction;
import org.teavm.model.instructions.SwitchInstruction;
import org.teavm.model.instructions.UnwrapArrayInstruction;
import org.teavm.model.util.UsageExtractor;

public class SmartArrayFastPathTransformer implements ClassHolderTransformer {

    private static final String PROP_PREFIX = "gdx.teavm.fastpath.";
    private static final String RULE_FIXED_PRIMITIVE_STORE_GROUP = "fixed-primitive-store-group";
    private static final String RULE_FIXED_PRIMITIVE_ACCESS = "fixed-primitive-access";
    private static final String HELPER_CLASS = GdxTeaVMArrayFastPaths.class.getName();
    private static final String ADDRESS_CLASS = Address.class.getName();
    private static final ValueType ADDRESS_TYPE = ValueType.object(ADDRESS_CLASS);
    private static final MethodReference ADDRESS_ADD_INT = new MethodReference(ADDRESS_CLASS, "add",
            ValueType.INTEGER, ADDRESS_TYPE);

    private final Config config = Config.fromProperties();
    private final Reporter reporter = new Reporter(config);

    @Override
    public void transformClass(ClassHolder cls, ClassHolderTransformerContext context) {
        if (config.mode == Mode.OFF || !config.acceptsClass(cls.getName())) {
            return;
        }
        for (MethodHolder method : new ArrayList<>(cls.getMethods())) {
            if (!method.hasProgram()) {
                continue;
            }
            transformMethod(cls, method);
        }
    }

    private void transformMethod(ClassHolder cls, MethodHolder method) {
        Program program = method.getProgram();
        if (program == null) {
            return;
        }

        MethodReference methodRef = new MethodReference(cls.getName(), method.getDescriptor());
        Map<Variable, Integer> constants = collectIntegerConstants(program);
        ArrayAccesses arrayAccesses = collectArrayAccesses(program);
        Map<Variable, IndexExpression> indexExpressions = collectIndexExpressions(program, constants);
        Set<Instruction> groupedInstructions = handleGroupedFixedStores(methodRef, program, indexExpressions,
                arrayAccesses);
        List<Candidate> candidates = new ArrayList<>();

        for (BasicBlock block : program.getBasicBlocks()) {
            if (block == null) {
                continue;
            }
            for (Instruction instruction = block.getFirstInstruction(); instruction != null;) {
                Instruction next = instruction.getNext();
                if (groupedInstructions.contains(instruction)) {
                    instruction = next;
                    continue;
                }

                Candidate candidate = detectFixedPrimitiveAccess(methodRef, instruction, constants);
                if (candidate != null) {
                    candidates.add(candidate);
                    handleCandidate(candidate);
                }
                instruction = next;
            }
        }

        if ((!groupedInstructions.isEmpty() || !candidates.isEmpty()) && config.dumpIr) {
            dumpMethod(methodRef, program);
        }
    }

    private Set<Instruction> handleGroupedFixedStores(MethodReference methodRef, Program program,
            Map<Variable, IndexExpression> indexExpressions, ArrayAccesses arrayAccesses) {
        Set<Instruction> groupedInstructions = new HashSet<>();
        if (!config.rules.contains(RULE_FIXED_PRIMITIVE_STORE_GROUP)) {
            return groupedInstructions;
        }

        for (BasicBlock block : program.getBasicBlocks()) {
            if (block == null) {
                continue;
            }
            StoreGroup group = null;
            for (Instruction instruction = block.getFirstInstruction(); instruction != null;) {
                Instruction next = instruction.getNext();
                if (isGroupBoundary(instruction)) {
                    applyStoreGroup(group, groupedInstructions);
                    group = null;
                    instruction = next;
                    continue;
                }

                StoreCandidate candidate = detectFixedPrimitiveStore(methodRef, instruction, indexExpressions,
                        arrayAccesses);
                if (candidate == null) {
                    instruction = next;
                    continue;
                }

                if (group != null && !group.accepts(candidate)) {
                    applyStoreGroup(group, groupedInstructions);
                    group = null;
                }
                if (group == null) {
                    group = new StoreGroup(candidate);
                }
                else {
                    group.add(candidate);
                }
                instruction = next;
            }
            applyStoreGroup(group, groupedInstructions);
        }
        return groupedInstructions;
    }

    private boolean isGroupBoundary(Instruction instruction) {
        return instruction instanceof InvokeInstruction
                || instruction instanceof PutFieldInstruction
                || instruction instanceof ConstructInstruction
                || instruction instanceof ConstructArrayInstruction
                || instruction instanceof ConstructMultiArrayInstruction
                || instruction instanceof CloneArrayInstruction
                || instruction instanceof InitClassInstruction
                || instruction instanceof MonitorEnterInstruction
                || instruction instanceof MonitorExitInstruction
                || instruction instanceof RaiseInstruction
                || instruction instanceof BranchingInstruction
                || instruction instanceof SwitchInstruction
                || instruction instanceof JumpInstruction
                || instruction instanceof ExitInstruction;
    }

    private StoreCandidate detectFixedPrimitiveStore(MethodReference methodRef, Instruction instruction,
            Map<Variable, IndexExpression> indexExpressions, ArrayAccesses arrayAccesses) {
        if (!(instruction instanceof PutElementInstruction)) {
            return null;
        }

        PutElementInstruction put = (PutElementInstruction)instruction;
        FastPrimitive primitive = FastPrimitive.from(put.getType());
        if (primitive == null) {
            return null;
        }

        IndexExpression index = expressionOf(put.getIndex(), indexExpressions);
        if (index == null || index.offset < 0) {
            return null;
        }

        ArrayAccess arrayAccess = arrayAccesses.resolve(put.getArray());
        if (arrayAccess == null) {
            return null;
        }

        return new StoreCandidate(methodRef, put, primitive, index, arrayAccess);
    }

    private void applyStoreGroup(StoreGroup group, Set<Instruction> groupedInstructions) {
        if (group == null || group.size() < config.groupMinStores) {
            return;
        }

        groupedInstructions.addAll(group.instructions());
        if (config.mode == Mode.REPORT) {
            reporter.report(group, Decision.CANDIDATE);
            return;
        }

        if (!config.allowUncheckedFixedAccess) {
            reporter.report(group.withReason("unchecked grouped fixed primitive store rewrite disabled"),
                    Decision.SKIPPED);
            return;
        }

        rewriteStoreGroup(group);
        reporter.report(group, Decision.APPLIED);
    }

    private void rewriteStoreGroup(StoreGroup group) {
        Program program = group.first().put.getProgram();
        Variable baseAddress = program.createVariable();
        InvokeInstruction ofData = new InvokeInstruction();
        ofData.setType(InvocationType.SPECIAL);
        ofData.setMethod(group.primitive.ofDataMethod);
        ofData.setArguments(group.arrayAccess.array);
        ofData.setReceiver(baseAddress);
        ofData.setLocation(group.first().put.getLocation());
        group.first().put.insertPrevious(ofData);

        Variable byteBaseOffset = null;
        if (group.indexBase != null) {
            Variable elementSize = program.createVariable();
            IntegerConstantInstruction elementSizeConstant = new IntegerConstantInstruction();
            elementSizeConstant.setConstant(group.primitive.size);
            elementSizeConstant.setReceiver(elementSize);
            elementSizeConstant.setLocation(group.first().put.getLocation());

            byteBaseOffset = program.createVariable();
            BinaryInstruction multiply = new BinaryInstruction(BinaryOperation.MULTIPLY, NumericOperandType.INT);
            multiply.setFirstOperand(group.indexBase);
            multiply.setSecondOperand(elementSize);
            multiply.setReceiver(byteBaseOffset);
            multiply.setLocation(group.first().put.getLocation());

            ofData.insertNext(elementSizeConstant);
            elementSizeConstant.insertNext(multiply);
        }

        for (StoreCandidate candidate : group.stores) {
            Variable offset = byteOffsetVariable(program, group, candidate, byteBaseOffset);

            Variable elementAddress = program.createVariable();
            InvokeInstruction add = new InvokeInstruction();
            add.setType(InvocationType.VIRTUAL);
            add.setMethod(ADDRESS_ADD_INT);
            add.setInstance(baseAddress);
            add.setArguments(offset);
            add.setReceiver(elementAddress);
            add.setLocation(candidate.put.getLocation());

            InvokeInstruction put = new InvokeInstruction();
            put.setType(InvocationType.VIRTUAL);
            put.setMethod(candidate.primitive.addressPutMethod);
            put.setInstance(elementAddress);
            put.setArguments(candidate.put.getValue());
            put.setLocation(candidate.put.getLocation());

            candidate.put.insertPrevious(add);
            add.insertNext(put);
            candidate.put.delete();
            if (candidate.arrayAccess.deleteUnwrap) {
                candidate.arrayAccess.unwrapInstruction.delete();
            }
        }
    }

    private Variable byteOffsetVariable(Program program, StoreGroup group, StoreCandidate candidate,
            Variable byteBaseOffset) {
        int byteOffset = candidate.index.offset * candidate.primitive.size;
        if (group.indexBase == null) {
            Variable offset = program.createVariable();
            IntegerConstantInstruction offsetConstant = new IntegerConstantInstruction();
            offsetConstant.setConstant(byteOffset);
            offsetConstant.setReceiver(offset);
            offsetConstant.setLocation(candidate.put.getLocation());
            candidate.put.insertPrevious(offsetConstant);
            return offset;
        }

        if (byteOffset == 0) {
            return byteBaseOffset;
        }

        Variable offsetConstantVariable = program.createVariable();
        IntegerConstantInstruction offsetConstant = new IntegerConstantInstruction();
        offsetConstant.setConstant(byteOffset);
        offsetConstant.setReceiver(offsetConstantVariable);
        offsetConstant.setLocation(candidate.put.getLocation());

        Variable offset = program.createVariable();
        BinaryInstruction add = new BinaryInstruction(BinaryOperation.ADD, NumericOperandType.INT);
        add.setFirstOperand(byteBaseOffset);
        add.setSecondOperand(offsetConstantVariable);
        add.setReceiver(offset);
        add.setLocation(candidate.put.getLocation());

        candidate.put.insertPrevious(offsetConstant);
        offsetConstant.insertNext(add);
        return offset;
    }

    private Candidate detectFixedPrimitiveAccess(MethodReference methodRef, Instruction instruction,
            Map<Variable, Integer> constants) {
        if (!config.rules.contains(RULE_FIXED_PRIMITIVE_ACCESS)) {
            return null;
        }

        if (instruction instanceof PutElementInstruction) {
            PutElementInstruction put = (PutElementInstruction)instruction;
            FastPrimitive primitive = FastPrimitive.from(put.getType());
            if (primitive == null) {
                return Candidate.skipped(methodRef, instruction, RULE_FIXED_PRIMITIVE_ACCESS,
                        "unsupported array element type: " + put.getType());
            }
            Integer index = constants.get(put.getIndex());
            if (index == null) {
                return Candidate.skipped(methodRef, instruction, RULE_FIXED_PRIMITIVE_ACCESS,
                        "index is not an integer constant");
            }
            return Candidate.fixedPut(methodRef, put, primitive, index);
        }

        if (instruction instanceof GetElementInstruction) {
            GetElementInstruction get = (GetElementInstruction)instruction;
            FastPrimitive primitive = FastPrimitive.from(get.getType());
            if (primitive == null) {
                return Candidate.skipped(methodRef, instruction, RULE_FIXED_PRIMITIVE_ACCESS,
                        "unsupported array element type: " + get.getType());
            }
            Integer index = constants.get(get.getIndex());
            if (index == null) {
                return Candidate.skipped(methodRef, instruction, RULE_FIXED_PRIMITIVE_ACCESS,
                        "index is not an integer constant");
            }
            return Candidate.fixedGet(methodRef, get, primitive, index);
        }

        return null;
    }

    private void handleCandidate(Candidate candidate) {
        if (!candidate.canRewrite()) {
            if (config.logSkipped) {
                reporter.report(candidate, Decision.SKIPPED);
            }
            return;
        }

        if (config.mode == Mode.REPORT) {
            reporter.report(candidate, Decision.CANDIDATE);
            return;
        }

        if (!config.allowUncheckedFixedAccess) {
            reporter.report(candidate.withReason("unchecked fixed primitive access rewrite disabled"),
                    Decision.SKIPPED);
            return;
        }

        rewriteFixedPrimitiveAccess(candidate);
        reporter.report(candidate, Decision.APPLIED);
    }

    private void rewriteFixedPrimitiveAccess(Candidate candidate) {
        InvokeInstruction invoke = new InvokeInstruction();
        invoke.setType(InvocationType.SPECIAL);
        invoke.setMethod(candidate.helperMethod());
        invoke.setLocation(candidate.instruction.getLocation());

        if (candidate.put != null) {
            invoke.setArguments(candidate.put.getArray(), candidate.put.getIndex(), candidate.put.getValue());
            candidate.put.replace(invoke);
        }
        else {
            invoke.setArguments(candidate.get.getArray(), candidate.get.getIndex());
            invoke.setReceiver(candidate.get.getReceiver());
            candidate.get.replace(invoke);
        }
    }

    private Map<Variable, Integer> collectIntegerConstants(Program program) {
        Map<Variable, Integer> constants = new HashMap<>();
        Set<Variable> invalid = new HashSet<>();
        for (BasicBlock block : program.getBasicBlocks()) {
            if (block == null) {
                continue;
            }
            for (Instruction instruction : block) {
                if (instruction instanceof IntegerConstantInstruction) {
                    IntegerConstantInstruction constant = (IntegerConstantInstruction)instruction;
                    Variable receiver = constant.getReceiver();
                    if (constants.containsKey(receiver)) {
                        invalid.add(receiver);
                    }
                    constants.put(receiver, constant.getConstant());
                }
            }
        }
        for (Variable variable : invalid) {
            constants.remove(variable);
        }
        return constants;
    }

    private Map<Variable, IndexExpression> collectIndexExpressions(Program program, Map<Variable, Integer> constants) {
        Map<Variable, IndexExpression> expressions = new HashMap<>();
        for (Map.Entry<Variable, Integer> entry : constants.entrySet()) {
            expressions.put(entry.getKey(), IndexExpression.constant(entry.getValue()));
        }

        for (BasicBlock block : program.getBasicBlocks()) {
            if (block == null) {
                continue;
            }
            for (Instruction instruction : block) {
                if (!(instruction instanceof BinaryInstruction)) {
                    continue;
                }
                BinaryInstruction binary = (BinaryInstruction)instruction;
                if (binary.getOperandType() != NumericOperandType.INT || binary.getReceiver() == null) {
                    continue;
                }
                IndexExpression first = expressionOf(binary.getFirstOperand(), expressions);
                IndexExpression second = expressionOf(binary.getSecondOperand(), expressions);
                IndexExpression result = combineIndexExpression(binary.getOperation(), first, second);
                if (result != null) {
                    expressions.put(binary.getReceiver(), result);
                }
            }
        }
        return expressions;
    }

    private IndexExpression expressionOf(Variable variable, Map<Variable, IndexExpression> expressions) {
        IndexExpression expression = expressions.get(variable);
        return expression != null ? expression : IndexExpression.variable(variable);
    }

    private IndexExpression combineIndexExpression(BinaryOperation operation, IndexExpression first,
            IndexExpression second) {
        if (operation == BinaryOperation.ADD) {
            return first.add(second);
        }
        if (operation == BinaryOperation.SUBTRACT && second.base == null) {
            return first.add(IndexExpression.constant(-second.offset));
        }
        return null;
    }

    private ArrayAccesses collectArrayAccesses(Program program) {
        ArrayAccesses accesses = new ArrayAccesses();
        UsageExtractor usageExtractor = new UsageExtractor();
        for (BasicBlock block : program.getBasicBlocks()) {
            if (block == null) {
                continue;
            }
            for (Instruction instruction : block) {
                if (instruction instanceof GetFieldInstruction) {
                    GetFieldInstruction getField = (GetFieldInstruction)instruction;
                    if (getField.getReceiver() != null) {
                        accesses.fieldSources.put(getField.getReceiver(),
                                new FieldArraySource(getField.getField(), getField.getInstance()));
                    }
                }
                else if (instruction instanceof UnwrapArrayInstruction) {
                    UnwrapArrayInstruction unwrap = (UnwrapArrayInstruction)instruction;
                    if (unwrap.getReceiver() != null) {
                        accesses.unwrappedArrays.put(unwrap.getReceiver(), unwrap.getArray());
                        accesses.unwrapInstructions.put(unwrap.getReceiver(), unwrap);
                    }
                }

                instruction.acceptVisitor(usageExtractor);
                for (Variable usedVariable : usageExtractor.getUsedVariables()) {
                    accesses.usageCounts.put(usedVariable, accesses.usageCounts.getOrDefault(usedVariable, 0) + 1);
                }
            }
        }
        return accesses;
    }

    private void dumpMethod(MethodReference methodRef, Program program) {
        String fileName = methodRef.toString().replace('\\', '_').replace('/', '_')
                .replace(':', '_').replace('<', '_').replace('>', '_')
                .replace('(', '_').replace(')', '_');
        Path path = Paths.get(config.dumpDir, fileName + ".txt");
        StringBuilder builder = new StringBuilder();
        builder.append(methodRef).append('\n');
        for (BasicBlock block : program.getBasicBlocks()) {
            if (block == null) {
                continue;
            }
            builder.append("block ").append(block.getIndex()).append('\n');
            for (Instruction instruction : block) {
                builder.append("  ").append(describeInstruction(instruction)).append('\n');
            }
        }
        write(path, builder.toString(), false);
    }

    private String describeInstruction(Instruction instruction) {
        if (instruction instanceof PutElementInstruction) {
            PutElementInstruction put = (PutElementInstruction)instruction;
            return "put-element " + put.getType() + " array=v" + put.getArray().getIndex()
                    + " index=v" + put.getIndex().getIndex() + " value=v" + put.getValue().getIndex();
        }
        if (instruction instanceof GetElementInstruction) {
            GetElementInstruction get = (GetElementInstruction)instruction;
            return "get-element " + get.getType() + " array=v" + get.getArray().getIndex()
                    + " index=v" + get.getIndex().getIndex() + " -> v" + get.getReceiver().getIndex();
        }
        if (instruction instanceof InvokeInstruction) {
            InvokeInstruction invoke = (InvokeInstruction)instruction;
            return "invoke " + invoke.getMethod();
        }
        if (instruction instanceof GetFieldInstruction) {
            GetFieldInstruction getField = (GetFieldInstruction)instruction;
            return "get-field " + getField.getField() + " -> v" + getField.getReceiver().getIndex();
        }
        if (instruction instanceof UnwrapArrayInstruction) {
            UnwrapArrayInstruction unwrap = (UnwrapArrayInstruction)instruction;
            return "unwrap-array " + unwrap.getElementType() + " array=v" + unwrap.getArray().getIndex()
                    + " -> v" + unwrap.getReceiver().getIndex();
        }
        if (instruction instanceof BinaryInstruction) {
            BinaryInstruction binary = (BinaryInstruction)instruction;
            return "binary " + binary.getOperation() + " -> v" + binary.getReceiver().getIndex();
        }
        if (instruction instanceof IntegerConstantInstruction) {
            IntegerConstantInstruction constant = (IntegerConstantInstruction)instruction;
            return "const " + constant.getConstant() + " -> v" + constant.getReceiver().getIndex();
        }
        return instruction.getClass().getSimpleName();
    }

    private void write(Path path, String content, boolean append) {
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (append) {
                Files.write(path, content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND);
            }
            else {
                Files.write(path, content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
            }
        }
        catch (IOException e) {
            throw new IllegalStateException("Could not write TeaVM fastpath debug output: " + path, e);
        }
    }

    private enum Mode {
        OFF,
        REPORT,
        REWRITE
    }

    private enum Decision {
        CANDIDATE,
        APPLIED,
        SKIPPED
    }

    private static final class Config {
        final Mode mode;
        final List<String> includes;
        final List<String> excludes;
        final Set<String> rules;
        final boolean logSkipped;
        final boolean logToConsole;
        final boolean dumpIr;
        final String dumpDir;
        final String reportFile;
        final boolean allowUncheckedFixedAccess;
        final int groupMinStores;

        private Config(Mode mode, List<String> includes, List<String> excludes, Set<String> rules,
                boolean logSkipped, boolean logToConsole, boolean dumpIr, String dumpDir, String reportFile,
                boolean allowUncheckedFixedAccess, int groupMinStores) {
            this.mode = mode;
            this.includes = includes;
            this.excludes = excludes;
            this.rules = rules;
            this.logSkipped = logSkipped;
            this.logToConsole = logToConsole;
            this.dumpIr = dumpIr;
            this.dumpDir = dumpDir;
            this.reportFile = reportFile;
            this.allowUncheckedFixedAccess = allowUncheckedFixedAccess;
            this.groupMinStores = groupMinStores;
        }

        static Config fromProperties() {
            Mode mode = parseMode(read("mode", "rewrite"));
            return new Config(
                    mode,
                    parseList(read("include",
                            "com.badlogic.gdx.graphics.g2d.Sprite,"
                                    + "com.badlogic.gdx.graphics.g2d.SpriteBatch")),
                    parseList(read("exclude", "")),
                    new HashSet<>(parseList(read("rules", RULE_FIXED_PRIMITIVE_STORE_GROUP))),
                    Boolean.parseBoolean(read("logSkipped", "false")),
                    Boolean.parseBoolean(read("logToConsole", mode == Mode.REPORT ? "true" : "false")),
                    Boolean.parseBoolean(read("dumpIr", "false")),
                    read("dumpDir", "build/teavm-fastpath-ir"),
                    read("reportFile", ""),
                    Boolean.parseBoolean(read("allowUncheckedFixedAccess", "true")),
                    Integer.parseInt(read("groupMinStores", "4")));
        }

        boolean acceptsClass(String className) {
            boolean included = includes.isEmpty();
            for (String include : includes) {
                if (matches(include, className)) {
                    included = true;
                    break;
                }
            }
            if (!included) {
                return false;
            }
            for (String exclude : excludes) {
                if (matches(exclude, className)) {
                    return false;
                }
            }
            return true;
        }

        private static Mode parseMode(String value) {
            try {
                return Mode.valueOf(value.trim().toUpperCase(Locale.ROOT));
            }
            catch (IllegalArgumentException e) {
                return Mode.OFF;
            }
        }

        private static List<String> parseList(String value) {
            List<String> result = new ArrayList<>();
            if (value == null || value.trim().isEmpty()) {
                return result;
            }
            for (String item : value.split(",")) {
                String trimmed = item.trim();
                if (!trimmed.isEmpty()) {
                    result.add(trimmed);
                }
            }
            return result;
        }

        private static boolean matches(String pattern, String value) {
            if (pattern.equals(value)) {
                return true;
            }
            if (pattern.endsWith(".*")) {
                return value.startsWith(pattern.substring(0, pattern.length() - 1));
            }
            int star = pattern.indexOf('*');
            if (star < 0) {
                return false;
            }
            String prefix = pattern.substring(0, star);
            String suffix = pattern.substring(star + 1);
            return value.startsWith(prefix) && value.endsWith(suffix);
        }

        private static String read(String name, String defaultValue) {
            String property = System.getProperty(PROP_PREFIX + name);
            if (property != null) {
                return property;
            }
            String envName = "GDX_TEAVM_FASTPATH_" + name.replaceAll("([a-z])([A-Z])", "$1_$2")
                    .replace('.', '_').toUpperCase(Locale.ROOT);
            String env = System.getenv(envName);
            return env != null ? env : defaultValue;
        }
    }

    private static final class Reporter {
        private final Config config;

        Reporter(Config config) {
            this.config = config;
            if (!config.reportFile.isEmpty()) {
                try {
                    Path path = Paths.get(config.reportFile);
                    Path parent = path.getParent();
                    if (parent != null) {
                        Files.createDirectories(parent);
                    }
                    Files.write(path, new byte[0], StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING);
                }
                catch (IOException e) {
                    throw new IllegalStateException("Could not reset TeaVM fastpath report", e);
                }
            }
        }

        void report(Candidate candidate, Decision decision) {
            StringBuilder builder = new StringBuilder();
            builder.append("[TeaVM fastpath] ").append(decision).append('\n');
            builder.append("  method: ").append(candidate.method).append('\n');
            builder.append("  rule: ").append(candidate.rule).append('\n');
            if (candidate.canRewrite()) {
                builder.append("  access: ").append(candidate.put != null ? "put" : "get").append(' ')
                        .append(candidate.primitive.type).append("[").append(candidate.index).append("]").append('\n');
            }
            if (candidate.instruction.getLocation() != null) {
                builder.append("  location: ").append(candidate.instruction.getLocation()).append('\n');
            }
            if (candidate.reason != null && !candidate.reason.isEmpty()) {
                builder.append("  reason: ").append(candidate.reason).append('\n');
            }
            builder.append('\n');
            write(builder.toString());
        }

        void report(StoreGroup group, Decision decision) {
            StringBuilder builder = new StringBuilder();
            builder.append("[TeaVM fastpath] ").append(decision).append('\n');
            builder.append("  method: ").append(group.method).append('\n');
            builder.append("  rule: ").append(RULE_FIXED_PRIMITIVE_STORE_GROUP).append('\n');
            builder.append("  access: put ").append(group.primitive.type).append(" x")
                    .append(group.size()).append('\n');
            builder.append("  source: ").append(group.arrayAccess.source).append('\n');
            builder.append("  indexes: ").append(group.indexSummary()).append('\n');
            if (group.first().put.getLocation() != null) {
                builder.append("  location: ").append(group.first().put.getLocation()).append('\n');
            }
            if (group.reason != null && !group.reason.isEmpty()) {
                builder.append("  reason: ").append(group.reason).append('\n');
            }
            builder.append('\n');
            write(builder.toString());
        }

        private void write(String text) {
            if (config.logToConsole) {
                System.out.print(text);
            }
            if (!config.reportFile.isEmpty()) {
                try {
                    Path path = Paths.get(config.reportFile);
                    Path parent = path.getParent();
                    if (parent != null) {
                        Files.createDirectories(parent);
                    }
                    Files.write(path, text.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE,
                            StandardOpenOption.APPEND);
                }
                catch (IOException e) {
                    throw new IllegalStateException("Could not write TeaVM fastpath report", e);
                }
            }
        }
    }

    private static final class StoreGroup {
        final MethodReference method;
        final FastPrimitive primitive;
        final ArrayAccess arrayAccess;
        final Variable indexBase;
        final List<StoreCandidate> stores = new ArrayList<>();
        final String reason;

        StoreGroup(StoreCandidate first) {
            this(first.method, first.primitive, first.arrayAccess, first.index.base,
                    first.index.base == null
                            ? "constant-index primitive stores to same array"
                            : "relative-index primitive stores to same array");
            add(first);
        }

        private StoreGroup(MethodReference method, FastPrimitive primitive, ArrayAccess arrayAccess,
                Variable indexBase, String reason) {
            this.method = method;
            this.primitive = primitive;
            this.arrayAccess = arrayAccess;
            this.indexBase = indexBase;
            this.reason = reason;
        }

        boolean accepts(StoreCandidate candidate) {
            return primitive == candidate.primitive
                    && arrayAccess.source.equals(candidate.arrayAccess.source)
                    && sameVariable(indexBase, candidate.index.base);
        }

        void add(StoreCandidate candidate) {
            stores.add(candidate);
        }

        StoreCandidate first() {
            return stores.get(0);
        }

        int size() {
            return stores.size();
        }

        Collection<Instruction> instructions() {
            List<Instruction> result = new ArrayList<>();
            for (StoreCandidate store : stores) {
                result.add(store.put);
            }
            return result;
        }

        StoreGroup withReason(String reason) {
            StoreGroup result = new StoreGroup(method, primitive, arrayAccess, indexBase, reason);
            result.stores.addAll(stores);
            return result;
        }

        String indexSummary() {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < stores.size(); i++) {
                if (i > 0) {
                    builder.append(',');
                }
                builder.append(stores.get(i).index);
            }
            return builder.toString();
        }

        private static boolean sameVariable(Variable first, Variable second) {
            if (first == null || second == null) {
                return first == second;
            }
            return first.getIndex() == second.getIndex();
        }
    }

    private static final class StoreCandidate {
        final MethodReference method;
        final PutElementInstruction put;
        final FastPrimitive primitive;
        final IndexExpression index;
        final ArrayAccess arrayAccess;

        StoreCandidate(MethodReference method, PutElementInstruction put, FastPrimitive primitive,
                IndexExpression index, ArrayAccess arrayAccess) {
            this.method = method;
            this.put = put;
            this.primitive = primitive;
            this.index = index;
            this.arrayAccess = arrayAccess;
        }
    }

    private static final class IndexExpression {
        final Variable base;
        final int offset;

        private IndexExpression(Variable base, int offset) {
            this.base = base;
            this.offset = offset;
        }

        static IndexExpression constant(int offset) {
            return new IndexExpression(null, offset);
        }

        static IndexExpression variable(Variable base) {
            return new IndexExpression(base, 0);
        }

        IndexExpression add(IndexExpression other) {
            if (base != null && other.base != null) {
                return null;
            }
            return new IndexExpression(base != null ? base : other.base, offset + other.offset);
        }

        @Override
        public String toString() {
            if (base == null) {
                return Integer.toString(offset);
            }
            if (offset == 0) {
                return "v" + base.getIndex();
            }
            return "v" + base.getIndex() + (offset > 0 ? "+" : "") + offset;
        }
    }

    private static final class ArrayAccesses {
        final Map<Variable, Variable> unwrappedArrays = new HashMap<>();
        final Map<Variable, UnwrapArrayInstruction> unwrapInstructions = new HashMap<>();
        final Map<Variable, FieldArraySource> fieldSources = new HashMap<>();
        final Map<Variable, Integer> usageCounts = new HashMap<>();

        ArrayAccess resolve(Variable array) {
            Variable originalArray = unwrappedArrays.get(array);
            UnwrapArrayInstruction unwrap = unwrapInstructions.get(array);
            boolean deleteUnwrap = unwrap != null && usageCounts.getOrDefault(array, 0) == 1;
            if (originalArray == null) {
                originalArray = array;
            }
            FieldArraySource fieldSource = fieldSources.get(originalArray);
            if (fieldSource != null) {
                return new ArrayAccess(originalArray, fieldSource, unwrap, deleteUnwrap);
            }
            return new ArrayAccess(originalArray, new VariableArraySource(originalArray), unwrap, deleteUnwrap);
        }
    }

    private static final class ArrayAccess {
        final Variable array;
        final ArraySource source;
        final UnwrapArrayInstruction unwrapInstruction;
        final boolean deleteUnwrap;

        ArrayAccess(Variable array, ArraySource source, UnwrapArrayInstruction unwrapInstruction,
                boolean deleteUnwrap) {
            this.array = array;
            this.source = source;
            this.unwrapInstruction = unwrapInstruction;
            this.deleteUnwrap = deleteUnwrap;
        }
    }

    private interface ArraySource {
    }

    private static final class FieldArraySource implements ArraySource {
        final FieldReference field;
        final Variable instance;

        FieldArraySource(FieldReference field, Variable instance) {
            this.field = field;
            this.instance = instance;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof FieldArraySource)) {
                return false;
            }
            FieldArraySource other = (FieldArraySource)obj;
            int instanceIndex = instance != null ? instance.getIndex() : -1;
            int otherInstanceIndex = other.instance != null ? other.instance.getIndex() : -1;
            return field.equals(other.field) && instanceIndex == otherInstanceIndex;
        }

        @Override
        public int hashCode() {
            return field.hashCode() * 31 + (instance != null ? instance.getIndex() : -1);
        }

        @Override
        public String toString() {
            return field + " instance=v" + (instance != null ? instance.getIndex() : -1);
        }
    }

    private static final class VariableArraySource implements ArraySource {
        final int variableIndex;

        VariableArraySource(Variable variable) {
            variableIndex = variable.getIndex();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof VariableArraySource && variableIndex == ((VariableArraySource)obj).variableIndex;
        }

        @Override
        public int hashCode() {
            return variableIndex;
        }

        @Override
        public String toString() {
            return "v" + variableIndex;
        }
    }

    private static final class Candidate {
        final MethodReference method;
        final Instruction instruction;
        final String rule;
        final FastPrimitive primitive;
        final int index;
        final PutElementInstruction put;
        final GetElementInstruction get;
        final String reason;

        private Candidate(MethodReference method, Instruction instruction, String rule, FastPrimitive primitive,
                int index, PutElementInstruction put, GetElementInstruction get, String reason) {
            this.method = method;
            this.instruction = instruction;
            this.rule = rule;
            this.primitive = primitive;
            this.index = index;
            this.put = put;
            this.get = get;
            this.reason = reason;
        }

        static Candidate fixedPut(MethodReference method, PutElementInstruction put, FastPrimitive primitive,
                int index) {
            return new Candidate(method, put, RULE_FIXED_PRIMITIVE_ACCESS, primitive, index, put, null,
                    "constant index primitive put");
        }

        static Candidate fixedGet(MethodReference method, GetElementInstruction get, FastPrimitive primitive,
                int index) {
            return new Candidate(method, get, RULE_FIXED_PRIMITIVE_ACCESS, primitive, index, null, get,
                    "constant index primitive get");
        }

        static Candidate skipped(MethodReference method, Instruction instruction, String rule, String reason) {
            return new Candidate(method, instruction, rule, FastPrimitive.UNKNOWN, -1, null, null, reason);
        }

        Candidate withReason(String reason) {
            return new Candidate(method, instruction, rule, primitive, index, put, get, reason);
        }

        boolean canRewrite() {
            return primitive != FastPrimitive.UNKNOWN && index >= 0 && (put != null || get != null);
        }

        MethodReference helperMethod() {
            return put != null ? primitive.putMethod : primitive.getMethod;
        }
    }

    private enum FastPrimitive {
        UNKNOWN(null, null, null, null, null, 0),
        CHAR(ArrayElementType.CHAR,
                new MethodReference(HELPER_CLASS, "getChar", ValueType.arrayOf(ValueType.CHARACTER),
                        ValueType.INTEGER, ValueType.CHARACTER),
                new MethodReference(HELPER_CLASS, "putChar", ValueType.arrayOf(ValueType.CHARACTER),
                        ValueType.INTEGER, ValueType.CHARACTER, ValueType.VOID),
                new MethodReference(ADDRESS_CLASS, "ofData", ValueType.arrayOf(ValueType.CHARACTER), ADDRESS_TYPE),
                new MethodReference(ADDRESS_CLASS, "putChar", ValueType.CHARACTER, ValueType.VOID),
                2),
        SHORT(ArrayElementType.SHORT,
                new MethodReference(HELPER_CLASS, "getShort", ValueType.arrayOf(ValueType.SHORT),
                        ValueType.INTEGER, ValueType.SHORT),
                new MethodReference(HELPER_CLASS, "putShort", ValueType.arrayOf(ValueType.SHORT),
                        ValueType.INTEGER, ValueType.SHORT, ValueType.VOID),
                new MethodReference(ADDRESS_CLASS, "ofData", ValueType.arrayOf(ValueType.SHORT), ADDRESS_TYPE),
                new MethodReference(ADDRESS_CLASS, "putShort", ValueType.SHORT, ValueType.VOID),
                2),
        INT(ArrayElementType.INT,
                new MethodReference(HELPER_CLASS, "getInt", ValueType.arrayOf(ValueType.INTEGER),
                        ValueType.INTEGER, ValueType.INTEGER),
                new MethodReference(HELPER_CLASS, "putInt", ValueType.arrayOf(ValueType.INTEGER),
                        ValueType.INTEGER, ValueType.INTEGER, ValueType.VOID),
                new MethodReference(ADDRESS_CLASS, "ofData", ValueType.arrayOf(ValueType.INTEGER), ADDRESS_TYPE),
                new MethodReference(ADDRESS_CLASS, "putInt", ValueType.INTEGER, ValueType.VOID),
                4),
        LONG(ArrayElementType.LONG,
                new MethodReference(HELPER_CLASS, "getLong", ValueType.arrayOf(ValueType.LONG),
                        ValueType.INTEGER, ValueType.LONG),
                new MethodReference(HELPER_CLASS, "putLong", ValueType.arrayOf(ValueType.LONG),
                        ValueType.INTEGER, ValueType.LONG, ValueType.VOID),
                new MethodReference(ADDRESS_CLASS, "ofData", ValueType.arrayOf(ValueType.LONG), ADDRESS_TYPE),
                new MethodReference(ADDRESS_CLASS, "putLong", ValueType.LONG, ValueType.VOID),
                8),
        FLOAT(ArrayElementType.FLOAT,
                new MethodReference(HELPER_CLASS, "getFloat", ValueType.arrayOf(ValueType.FLOAT),
                        ValueType.INTEGER, ValueType.FLOAT),
                new MethodReference(HELPER_CLASS, "putFloat", ValueType.arrayOf(ValueType.FLOAT),
                        ValueType.INTEGER, ValueType.FLOAT, ValueType.VOID),
                new MethodReference(ADDRESS_CLASS, "ofData", ValueType.arrayOf(ValueType.FLOAT), ADDRESS_TYPE),
                new MethodReference(ADDRESS_CLASS, "putFloat", ValueType.FLOAT, ValueType.VOID),
                4),
        DOUBLE(ArrayElementType.DOUBLE,
                new MethodReference(HELPER_CLASS, "getDouble", ValueType.arrayOf(ValueType.DOUBLE),
                        ValueType.INTEGER, ValueType.DOUBLE),
                new MethodReference(HELPER_CLASS, "putDouble", ValueType.arrayOf(ValueType.DOUBLE),
                        ValueType.INTEGER, ValueType.DOUBLE, ValueType.VOID),
                new MethodReference(ADDRESS_CLASS, "ofData", ValueType.arrayOf(ValueType.DOUBLE), ADDRESS_TYPE),
                new MethodReference(ADDRESS_CLASS, "putDouble", ValueType.DOUBLE, ValueType.VOID),
                8);

        final ArrayElementType type;
        final MethodReference getMethod;
        final MethodReference putMethod;
        final MethodReference ofDataMethod;
        final MethodReference addressPutMethod;
        final int size;

        FastPrimitive(ArrayElementType type, MethodReference getMethod, MethodReference putMethod,
                MethodReference ofDataMethod, MethodReference addressPutMethod, int size) {
            this.type = type;
            this.getMethod = getMethod;
            this.putMethod = putMethod;
            this.ofDataMethod = ofDataMethod;
            this.addressPutMethod = addressPutMethod;
            this.size = size;
        }

        static FastPrimitive from(ArrayElementType type) {
            for (FastPrimitive primitive : values()) {
                if (primitive.type == type) {
                    return primitive;
                }
            }
            return null;
        }
    }
}
