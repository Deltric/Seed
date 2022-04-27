package dev.deltric.seed.api.economy

/**
 * Enum of resulting transaction states
 *
 * @author Deltric
 * @since 4/23/2022
 */
enum class TransactionResult {
    SUCCESS,
    INSUFFICIENT_BALANCE,
    BALANCE_OVERFLOW,
    NO_MODIFICATION,
    CALLBACK_CANCELED
}