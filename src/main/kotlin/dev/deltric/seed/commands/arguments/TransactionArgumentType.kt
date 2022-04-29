package dev.deltric.seed.commands.arguments

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import dev.deltric.seed.api.economy.TransactionType
import net.minecraft.command.CommandSource
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralText
import java.util.concurrent.CompletableFuture

class TransactionArgumentType: ArgumentType<TransactionType> {
    companion object {
        val INVALID_TRANSACTION_TYPE_EXCEPTION = DynamicCommandExceptionType { id: Any ->
            LiteralText("A transaction type does not exist with the name '$id'") }

        fun transactionType(): TransactionArgumentType {
            return TransactionArgumentType()
        }

        fun getTransactionType(ctx: CommandContext<ServerCommandSource>, name: String): TransactionType {
            return ctx.getArgument(name, TransactionType::class.java)
        }
    }

    override fun parse(reader: StringReader): TransactionType {
        val id = reader.readString()
        return TransactionType.values().find { transactionType -> transactionType.name.equals(id, true) }
            ?: throw INVALID_TRANSACTION_TYPE_EXCEPTION.create(id)
    }

    override fun <S : Any> listSuggestions(context: CommandContext<S>?, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        return CommandSource.suggestMatching(TransactionType.values().map(TransactionType::toString), builder)
    }
}