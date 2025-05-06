package dev.lapis256.easy_nest_config.api

import net.neoforged.neoforge.common.ModConfigSpec
import kotlin.reflect.KClass


interface IApplyHandler {
    fun apply(clazz: KClass<*>, builder: ModConfigSpec.Builder)
}
