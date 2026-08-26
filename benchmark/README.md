# Desktop test benchmark

This module runs an existing libGDX test object as the same desktop workload on two targets:

- TeaVM C with the GLFW backend;
- Java with the LWJGL3 backend.

The test class remains ordinary example code. It does not implement a benchmark interface and does not
contain benchmark timing, result writing, or alternate rendering paths.

## Architecture

`BenchmarkApplication` accepts an `ApplicationListener` object in its constructor. It delegates `create`,
`resize`, `render`, `pause`, `resume`, and `dispose` directly to that object while measuring complete calls to
`render`.

Each platform launcher creates the desired test normally and passes it to the benchmark wrapper:

```java
new BenchmarkApplication("backend-name", benchmarkConfig, new TestClass())
```

There is no test-name registry, string resolver, alias list, reflection, or benchmark-specific requirement on
the test class. To use another test, create that test object in the corresponding launchers. Separate launcher
classes and Gradle tasks can be added when multiple test benchmarks need to coexist.

The LWJGL3 task uses `examples/basic/assets` as its working directory. TeaVM C packages the same complete
asset directory, allowing test objects to use their normal runtime assets.

## Running the comparison

Run the configured test on both backends and generate a Markdown report:

```bash
./gradlew :benchmark:compare
```

The report is written to:

```text
benchmark/build/benchmark-results/results.md
```

The platform-project tasks remain available for debugging the individual launch paths:

```bash
./gradlew :benchmark:glfw:benchmarkRelease
./gradlew :benchmark:lwjgl3:benchmark
```

## Measurement configuration

The benchmark controls only the measurement environment. Workload behavior remains owned by the test
object:

- `benchWarmup`: warmup seconds, default `3`
- `benchSeconds`: measured seconds, default `15`
- `benchWidth`: window width, default `640`
- `benchHeight`: window height, default `480`
- `benchGlfwConsole`: stream TeaVM GLFW benchmark output to the Gradle console, default `true`
- `benchGlfwContinueOnTimeout`: stop a timed-out native run and continue, default `true`

For a short verification run:

```bash
./gradlew :benchmark:compare -PbenchWarmup=1 -PbenchSeconds=3
```

Each successful run prints `BENCH_START`, per-second `BENCH_SECOND` samples, and one `BENCH_RESULT`. The test
name stored in the result is derived from the supplied object's class rather than a separately maintained
name table.

The TeaVM side uses normal generated Java/libGDX code and its default `SIMPLE` optimization level. The
generated C is built as a native CMake `Release` executable.
