package com.github.xpenatan.gdx.teavm.gradle

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.teavm.gradle.api.OptimizationLevel
import org.teavm.gradle.api.SourceFilePolicy
import javax.inject.Inject

/**
 * Optional conventions inherited by every JavaScript and Wasm target.
 *
 * Target output directories and target file names intentionally remain target-specific so named
 * variants cannot accidentally write to the same location.
 */
open class GdxTeaVMWebDefaults @Inject constructor(objects: ObjectFactory) {
    val mainClass: Property<String> = objects.property(String::class.java)
    val relativePathInOutputDir: Property<String> = objects.property(String::class.java)
    val optimization: Property<OptimizationLevel> = objects.property(OptimizationLevel::class.java)
    val debugInformation: Property<Boolean> = objects.property(Boolean::class.javaObjectType)
    val fastGlobalAnalysis: Property<Boolean> = objects.property(Boolean::class.javaObjectType)
    val outOfProcess: Property<Boolean> = objects.property(Boolean::class.javaObjectType)
    val processMemory: Property<Int> = objects.property(Int::class.javaObjectType)
    val preservedClasses: ListProperty<String> = objects.listProperty(String::class.java)

    val entryPointName: Property<String> = objects.property(String::class.java)
    val mainClassArgs: Property<String> = objects.property(String::class.java)
    val webappEnabled: Property<Boolean> = objects.property(Boolean::class.javaObjectType)
    val generateIndexHtml: Property<Boolean> = objects.property(Boolean::class.javaObjectType)
    val htmlTitle: Property<String> = objects.property(String::class.java)
    val htmlWidth: Property<Int> = objects.property(Int::class.javaObjectType)
    val htmlHeight: Property<Int> = objects.property(Int::class.javaObjectType)
    val copyAssets: Property<Boolean> = objects.property(Boolean::class.javaObjectType)
    val logoPath: Property<String> = objects.property(String::class.java)
    val copyLoadingAsset: Property<Boolean> = objects.property(Boolean::class.javaObjectType)
    val serverPort: Property<Int> = objects.property(Int::class.javaObjectType)

    val obfuscated: Property<Boolean> = objects.property(Boolean::class.javaObjectType)
    val strict: Property<Boolean> = objects.property(Boolean::class.javaObjectType)
    val sourceMap: Property<Boolean> = objects.property(Boolean::class.javaObjectType)
    val sourceFilePolicy: Property<SourceFilePolicy> = objects.property(SourceFilePolicy::class.java)
}

/**
 * Optional conventions inherited by every TeaVM C/native target.
 *
 * Output and release directories intentionally remain target-specific so named variants are
 * isolated by default.
 */
open class GdxTeaVMNativeDefaults @Inject constructor(objects: ObjectFactory) {
    val mainClass: Property<String> = objects.property(String::class.java)
    val relativePathInOutputDir: Property<String> = objects.property(String::class.java)
    val targetFileName: Property<String> = objects.property(String::class.java)
    val optimization: Property<OptimizationLevel> = objects.property(OptimizationLevel::class.java)
    val debugInformation: Property<Boolean> = objects.property(Boolean::class.javaObjectType)
    val fastGlobalAnalysis: Property<Boolean> = objects.property(Boolean::class.javaObjectType)
    val outOfProcess: Property<Boolean> = objects.property(Boolean::class.javaObjectType)
    val processMemory: Property<Int> = objects.property(Int::class.javaObjectType)
    val preservedClasses: ListProperty<String> = objects.listProperty(String::class.java)
    val minHeapSizeMb: Property<Int> = objects.property(Int::class.javaObjectType)
    val maxHeapSizeMb: Property<Int> = objects.property(Int::class.javaObjectType)
    val heapDump: Property<Boolean> = objects.property(Boolean::class.javaObjectType)
    val shortFileNames: Property<Boolean> = objects.property(Boolean::class.javaObjectType)
    val obfuscated: Property<Boolean> = objects.property(Boolean::class.javaObjectType)
}

internal data class GdxTeaVMTargetRegistration<out T>(
    val name: String?,
    val taskNameSegment: String?,
    val target: T
) {
    val isDefault: Boolean
        get() = name == null

    fun taskPrefix(defaultPrefix: String): String {
        return if(taskNameSegment == null) defaultPrefix else "${defaultPrefix}_$taskNameSegment"
    }
}

internal fun normalizeGdxTeaVMTargetName(name: String): String {
    val trimmed = name.trim()
    require(trimmed.isNotEmpty()) { "gdx-teavm target name cannot be blank" }

    val normalized = buildString {
        trimmed.forEachIndexed { index, char ->
            val previous = trimmed.getOrNull(index - 1)
            when {
                char.isLetterOrDigit() -> {
                    if(char.isUpperCase() && previous != null && (previous.isLowerCase() || previous.isDigit())
                        && isNotEmpty() && last() != '_') {
                        append('_')
                    }
                    append(char.lowercaseChar())
                }
                isNotEmpty() && last() != '_' -> append('_')
            }
        }
    }.trim('_')

    require(normalized.isNotEmpty()) {
        "gdx-teavm target name '$name' must contain at least one letter or digit"
    }
    return normalized
}
