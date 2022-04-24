package dev.deltric.seed.api.economy

/**
 * Enum of resulting transaction states
 */
enum class TransactionResult {
    SUCCESS,
    INSUFFICIENT_BALANCE,
    BALANCE_OVERFLOW,
    NO_MODIFICATION,
    CALLBACK_CANCELED
}