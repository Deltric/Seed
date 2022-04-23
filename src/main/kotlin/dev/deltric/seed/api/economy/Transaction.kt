package dev.deltric.seed.api.economy

import java.util.UUID

/**
 * Data object that represents a currency transaction
 *
 * @author Deltric
 * @since 4/23/2022
 */
data class Transaction(
    val uuid: UUID,
    val currency: Currency,
    val type: TransactionType,
    val result: TransactionResult,
    val originalBalance: Int,
    val finalBalance: Int = originalBalance,
    val amount: Int = 0,
)