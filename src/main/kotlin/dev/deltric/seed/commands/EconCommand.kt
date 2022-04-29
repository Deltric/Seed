package dev.deltric.seed.commands

import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import com.mojang.brigadier.builder.RequiredArgumentBuilder.argument
import com.mojang.brigadier.context.CommandContext
import dev.deltric.seed.Seed
import dev.deltric.seed.api.economy.Currency
import dev.deltric.seed.api.economy.CurrencyHolder
import dev.deltric.seed.api.economy.TransactionType
import dev.deltric.seed.commands.arguments.CurrencyArgumentType
import dev.deltric.seed.commands.arguments.TransactionArgumentType
import dev.deltric.seed.util.PermissionNodes
import me.lucko.fabric.api.permissions.v0.Permissions
import net.kyori.adventure.text.Component
import net.minecraft.command.EntitySelector
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.ServerCommandSource

object EconCommand {

    val STRUCTURE: LiteralArgumentBuilder<ServerCommandSource> = literal<ServerCommandSource>("econ")
        .requires { Permissions.check(it, PermissionNodes.ECON_COMMAND) || it.hasPermissionLevel(4) }
        .then(argument<ServerCommandSource, EntitySelector>("players", EntityArgumentType.players())
            .then(argument<ServerCommandSource, TransactionType>("type", TransactionArgumentType.transactionType())
                .then(argument<ServerCommandSource, Currency>("currency", CurrencyArgumentType.currency())
                    .then(argument<ServerCommandSource, Int>("amount", IntegerArgumentType.integer(1))
                        .executes(EconCommand::onExecute)))))

    private fun onExecute(ctx: CommandContext<ServerCommandSource>): Int {
        val players = EntityArgumentType.getPlayers(ctx, "players")
        val transactionType = TransactionArgumentType.getTransactionType(ctx, "type")
        val currency = CurrencyArgumentType.getCurrency(ctx, "currency")
        val amount = IntegerArgumentType.getInteger(ctx, "amount")

        for(player in players) {
            val currencyHolder = player as CurrencyHolder
            val transaction = when(transactionType) {
                TransactionType.WITHDRAW -> currencyHolder.withdraw(currency, amount)
                TransactionType.DEPOSIT -> currencyHolder.deposit(currency, amount)
                TransactionType.SET -> currencyHolder.set(currency, amount)
            }

            val audience = Seed.adventure.player(player.uuid)
            audience.sendMessage(Component.text("${transaction.result} with new balance of ${transaction.finalBalance}"))
        }
        return 1
    }

}