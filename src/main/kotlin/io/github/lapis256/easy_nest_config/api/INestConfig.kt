package io.github.lapis256.easy_nest_config.api

import net.neoforged.fml.config.ModConfig
import net.neoforged.neoforge.common.ModConfigSpec


interface INestConfig {
    val type: ModConfig.Type
    val name: String
    val builder: ModConfigSpec.Builder
    val spec: ModConfigSpec
}
