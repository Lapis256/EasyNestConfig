package dev.lapis256.easy_nest_config.api

import net.minecraftforge.common.ForgeConfigSpec
import kotlin.reflect.KClass


interface IApplyHandler {
    fun apply(clazz: KClass<*>, builder: ForgeConfigSpec.Builder)
}
