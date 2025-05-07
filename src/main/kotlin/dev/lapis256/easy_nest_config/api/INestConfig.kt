package dev.lapis256.easy_nest_config.api

import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.fml.config.ModConfig


interface INestConfig {
    val type: ModConfig.Type
    val name: String
    val builder: ForgeConfigSpec.Builder
    val spec: ForgeConfigSpec
}
