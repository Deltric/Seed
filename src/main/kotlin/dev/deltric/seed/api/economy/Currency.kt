package dev.deltric.seed.api.economy

import net.kyori.adventure.text.Component
import net.minecraft.util.Identifier

data class Currency(
    val id: Identifier,
    val displayName: Component,
    val prefix: Component?,
)