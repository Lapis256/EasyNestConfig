package dev.lapis256.easy_nest_config.impl

import dev.lapis256.easy_nest_config.api.Comment
import dev.lapis256.easy_nest_config.api.IApplyHandler
import dev.lapis256.easy_nest_config.api.Translation
import net.neoforged.neoforge.common.ModConfigSpec
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation


object NestConfigHandler : IApplyHandler {
    override fun apply(clazz: KClass<*>, builder: ModConfigSpec.Builder) {
        clazz.findAnnotation<Comment>()?.let { builder.comment(it.value, *it.additional) }
    }
}

object TranslationHandler : IApplyHandler {
    override fun apply(clazz: KClass<*>, builder: ModConfigSpec.Builder) {
        clazz.findAnnotation<Translation>()?.let { builder.translation(it.value) }
    }
}
