package dev.lapis256.easy_nest_config.impl

import dev.lapis256.easy_nest_config.EasyNestConfig
import dev.lapis256.easy_nest_config.api.IApplyHandler
import dev.lapis256.easy_nest_config.api.INestConfig
import dev.lapis256.easy_nest_config.api.NestConfig
import net.neoforged.fml.ModContainer
import net.neoforged.neoforge.common.ModConfigSpec
import kotlin.reflect.KClass
import kotlin.reflect.full.hasAnnotation


@Suppress("Unused")
class ConfigHelper(private val modContainer: ModContainer, private val folderName: String?) {
    private val _configs = mutableMapOf<ModConfigSpec, INestConfig>()

    @Suppress("MemberVisibilityCanBePrivate")
    val configs: Map<ModConfigSpec, INestConfig>
        get() = _configs.toMap()

    @Suppress("MemberVisibilityCanBePrivate")
    fun registerConfig(config: INestConfig) {
        processNestedClasses(config.builder, config::class.nestedClasses)

        modContainer.registerConfig(config.type, config.spec, "${folderName?.plus("/") ?: ""}${config.name}.toml")
        _configs[config.spec] = config
    }


    private val applyHandlers = mutableListOf(NestConfigHandler, TranslationHandler)

    @Suppress("MemberVisibilityCanBePrivate")
    fun registerApplyHandler(handler: IApplyHandler) {
        applyHandlers.add(handler)
    }

    private fun apply(clazz: KClass<*>, builder: ModConfigSpec.Builder) {
        applyHandlers.forEach { handler -> handler.apply(clazz, builder) }
    }

    private fun processNestedClasses(builder: ModConfigSpec.Builder, nested: Collection<KClass<*>>) {
        nested.forEach { clazz -> processNested(builder, clazz) }
    }

    private fun processNested(builder: ModConfigSpec.Builder, clazz: KClass<*>) {
        val name = clazz.simpleName ?: error("Unnamed class found: $clazz")

        if (!clazz.hasAnnotation<NestConfig>()) {
            return EasyNestConfig.LOGGER.debug("${clazz.simpleName} has no @NestConfig annotation, skipping")
        }

        apply(clazz, builder)
        builder.push(name)

        clazz.objectInstance ?: error("No object instance for $clazz")
        processNestedClasses(builder, clazz.nestedClasses)

        builder.pop()
    }
}
