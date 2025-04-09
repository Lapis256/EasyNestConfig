package io.github.lapis256.easy_nest_config.api

import net.neoforged.fml.config.ModConfig
import net.neoforged.neoforge.common.ModConfigSpec


@Suppress("Unused")
abstract class AbstractNestConfig(override val type: ModConfig.Type, override val name: String) : INestConfig {
    override val builder: ModConfigSpec.Builder = ModConfigSpec.Builder()
    override val spec by lazy { builder.build() }
}
