package dev.lapis256.easy_nest_config.api

import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.fml.config.ModConfig


@Suppress("Unused")
abstract class AbstractNestConfig(override val type: ModConfig.Type, override val name: String) : INestConfig {
    override val builder: ForgeConfigSpec.Builder = ForgeConfigSpec.Builder()
    override val spec: ForgeConfigSpec by lazy { builder.build() }
}
