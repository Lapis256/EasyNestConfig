package dev.lapis256.easy_nest_config.impl

import dev.lapis256.easy_nest_config.EasyNestConfig
import dev.lapis256.easy_nest_config.api.IApplyHandler
import dev.lapis256.easy_nest_config.api.INestConfig
import dev.lapis256.easy_nest_config.api.NestConfig
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.fml.ModContainer
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.config.ModConfig
import kotlin.reflect.KClass
import kotlin.reflect.full.hasAnnotation


@Suppress("Unused")
class ConfigHelper(private val context: ModLoadingContext, private val folderName: String?) {
    private val _configs = mutableMapOf<ForgeConfigSpec, INestConfig>()

    @Suppress("MemberVisibilityCanBePrivate")
    val configs: Map<ForgeConfigSpec, INestConfig>
        get() = _configs.toMap()

    @Suppress("MemberVisibilityCanBePrivate")
    fun registerConfig(config: INestConfig) {
        processNestedClasses(config.builder, config::class.nestedClasses)

        context.registerConfig(config.type, config.spec, "${folderName?.plus("/") ?: ""}${config.name}.toml")

        _configs[config.spec] = config
    }


    @Suppress("MemberVisibilityCanBePrivate")
    fun <C : INestConfig> registerConfig(config: C, modConfigCreator: (ModConfig.Type, ForgeConfigSpec, ModContainer, String, C) -> ModConfig) {
        processNestedClasses(config.builder, config::class.nestedClasses)

        val modConfig = modConfigCreator(config.type, config.spec, context.container, "${folderName?.plus("/") ?: ""}${config.name}.toml", config)
        context.container.addConfig(modConfig)

        _configs[config.spec] = config
    }


    private val applyHandlers = mutableListOf(NestConfigHandler, TranslationHandler)

    @Suppress("MemberVisibilityCanBePrivate")
    fun registerApplyHandler(handler: IApplyHandler) {
        applyHandlers.add(handler)
    }

    private fun apply(clazz: KClass<*>, builder: ForgeConfigSpec.Builder) {
        applyHandlers.forEach { handler -> handler.apply(clazz, builder) }
    }

    private fun processNestedClasses(builder: ForgeConfigSpec.Builder, nested: Collection<KClass<*>>) {
        nested.forEach { clazz -> processNested(builder, clazz) }
    }

    private fun processNested(builder: ForgeConfigSpec.Builder, clazz: KClass<*>) {
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
