# gdx-teavm benchmark tests

This module contains backend-agnostic benchmark cases plus launchers for stock libGDX LWJGL3, GraalVM native image,
and TeaVM GLFW native C.

Run the standard SpriteBatch comparison:

```bash
./gradlew :benchmark:compareSpriteBatch
```

Run the broader overhead matrix:

```bash
./gradlew :benchmark:benchmarkMatrix
```

The matrix writes a Markdown report to:

```text
benchmark/build/benchmark-results/matrix/results.md
```

Configure runs with Gradle properties:

```bash
./gradlew :benchmark:compareSpriteBatch -PbenchSprites=8191 -PbenchWarmup=3 -PbenchSeconds=15
```

Useful properties:

- `benchTest`: `spritebatch_default`, `spritebatch_fast`, `spritebatch_direct_getters`,
  `spritebatch_direct_array_state`, `spritebatch_simple_direct`, `spritebatch_precomputed_arraycopy`,
  or `spritebatch_begin_end`
- `benchSprites`: sprite count, default `8191`
- `benchWarmup`: warmup seconds, default `3`
- `benchSeconds`: measured seconds, default `15`
- `benchWidth`: window width, default `640`
- `benchHeight`: window height, default `480`
- `benchRotate`: rotate sprites in SpriteBatch draw benchmarks, default `true`
- `benchScale`: scale sprites in SpriteBatch draw benchmarks, default `true`
- `benchClear`: clear the frame in draw benchmarks, default `true`
- `benchGlfwConsole`: stream TeaVM GLFW native output to the Gradle console, default `true`
- `benchGlfwContinueOnTimeout`: kill a timed-out TeaVM GLFW benchmark and continue the Gradle task, default `true`

Run isolated backends:

```bash
./gradlew :benchmark:lwjgl3:benchmark
./gradlew :benchmark:graalvm:benchmarkJvm
./gradlew :benchmark:graalvm:benchmarkRelease
./gradlew :benchmark:glfw:benchmarkRelease
```

Each run prints `BENCH_RESULT` and can append TSV rows to a report file when `--resultFile=...` is passed by the aggregate tasks.

The GraalVM backend uses the optimized `release` native-image binary for aggregate comparisons. It requires a local
GraalVM/native-image setup, matching the existing `examples:basic:platforms:desktop:graalvm` module.
