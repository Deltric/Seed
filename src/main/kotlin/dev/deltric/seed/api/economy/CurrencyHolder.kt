package dev.deltric.seed.api.economy

import net.minecraft.util.Identifier

/**
 * Interface for a holder of currencies
 *
 * @author Deltric
 * @since 4/23/2022
 */
interface CurrencyHolder {
    /**
     * Gets the map (wallet) which contains their currencies
     */
    fun getWalletMap(): Map<Identifier, Int>

    /**
     * Deposits an amount of currency
     *
     * @param currency - The currency that's being deposited
     * @param amount - The amount of currency being deposited
     * @return the resulting transaction
     */
    fun deposit(currency: Currency, amount: Int): Transaction

    /**
     * Withdraws an amount of currency
     *
     * @param currency - The currency that's being withdrawn
     * @param amount - The amount of currency being withdrawn
     * @return the resulting transaction
     */
    fun withdraw(currency: Currency, amount: Int): Transaction

    /**
     * Sets the balance of a currency
     *
     * @param currency - The currency that's being set
     * @param amount - The amount of currency to set to
     * @return the resulting transaction
     */
    fun set(currency: Currency, amount: Int): Transaction

    /**
     * Gets the balance of a currency
     *
     * @param currency - The currency to get the balance of
     */
    fun getBalance(currency: Currency): Int
}