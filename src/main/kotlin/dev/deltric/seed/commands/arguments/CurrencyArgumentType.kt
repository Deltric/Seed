package dev.deltric.seed.commands.arguments

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import dev.deltric.seed.api.economy.Currency
import dev.deltric.seed.api.economy.CurrencyRegistry
import net.minecraft.command.CommandSource
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralText
import net.minecraft.util.Identifier
import java.util.concurrent.CompletableFuture

class CurrencyArgumentType: ArgumentType<Currency> {
    companion object {
        val INVALID_CURRENCY_EXCEPTION = DynamicCommandExceptionType { id: Any ->
            LiteralText("A currency does not exist with the id '$id'") }

        fun currency(): CurrencyArgumentType {
            return CurrencyArgumentType()
        }

        fun getCurrency(ctx: CommandContext<ServerCommandSource>, name: String): Currency {
            return ctx.getArgument(name, Currency::class.java)
        }
    }

    override fun parse(reader: StringReader): Currency {
        val identifier = Identifier.fromCommandInput(reader)
        return CurrencyRegistry.getCurrency(identifier)
            ?: throw INVALID_CURRENCY_EXCEPTION.create(identifier)
    }

    override fun <S : Any> listSuggestions(context: CommandContext<S>?, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        return CommandSource.suggestMatching(CurrencyRegistry.getIds(), builder)
    }
}