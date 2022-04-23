package dev.deltric.seed.api.callbacks

import dev.deltric.seed.api.economy.Transaction
import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.util.ActionResult

fun interface TransactionCallback {
    operator fun invoke(transaction: Transaction): ActionResult

    companion object {
        val EVENT: Event<TransactionCallback> = EventFactory.createArrayBacked(TransactionCallback::class.java)
        { listeners -> TransactionCallback { transaction: Transaction ->
                for (listener in listeners) {
                    val result = listener(transaction)
                    if (result !== ActionResult.PASS) {
                        return@TransactionCallback result
                    }
                }
                return@TransactionCallback ActionResult.PASS
            }
        }
    }
}