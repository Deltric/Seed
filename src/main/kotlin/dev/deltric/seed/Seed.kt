package dev.deltric.seed

import com.mojang.brigadier.CommandDispatcher
import dev.deltric.seed.api.economy.Currency
import dev.deltric.seed.api.economy.CurrencyHolder
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.kyori.adventure.platform.fabric.FabricServerAudiences
import net.kyori.adventure.text.Component
import net.minecraft.entity.damage.DamageSource
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

class Seed : ModInitializer {
    companion object  {
        lateinit var adventure: FabricServerAudiences
    }

    override fun onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(ServerLifecycleEvents.ServerStarting { server: MinecraftServer ->
            adventure = FabricServerAudiences.of(server)
        })

        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource>, dedicated: Boolean ->
            if (!dedicated) return@CommandRegistrationCallback
        })

        // TODO: Remove after testing
        val gold = Currency(Identifier("seed:gold"), Component.text("Gold"), Component.text('G'))
        AttackBlockCallback.EVENT.register(AttackBlockCallback { player: PlayerEntity, world: World, hand: Hand, pos: BlockPos, direction: Direction ->
            if (!player.isSpectator) {
                if(player is CurrencyHolder) {
                    val audience = adventure.player(player.uuid)
                    player.deposit(gold, 500)
                    audience.sendMessage(Component.text(player.getBalance(gold)))
                }
            }
            ActionResult.PASS
        })
    }
}