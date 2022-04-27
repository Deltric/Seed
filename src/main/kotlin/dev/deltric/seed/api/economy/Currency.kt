package dev.deltric.seed.api.economy

import net.kyori.adventure.text.Component
import net.minecraft.util.Identifier

/**
 * Data object representing a currency
 *
 * @author Deltric
 * @since 4/23/2022
 */
data class Currency(
    val id: Identifier,
    val displayName: Component,
    val prefix: Component?,
)