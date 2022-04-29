package dev.deltric.seed.api.economy

import net.minecraft.util.Identifier

/**
 * Centralized registry for all currency types
 *
 * @author Deltric
 * @since 4/27/2022
 */
object CurrencyRegistry {
    private val currencyMap = mutableMapOf<Identifier, Currency>()

    /**
     * Gets a currency from an identifier
     */
    fun getCurrency(id: Identifier): Currency? {
        return currencyMap[id]
    }

    /**
     * Registers a new form of currency
     * @param currency - The currency to register
     */
    fun register(currency: Currency) {
        currencyMap[currency.id] = currency
    }

    /**
     * Gets a list of all the currency ids
     */
    fun getIds(): List<String> {
        return currencyMap.keys.map(Identifier::toString)
    }
}