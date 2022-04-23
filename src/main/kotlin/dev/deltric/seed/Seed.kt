package dev.deltric.seed

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.kyori.adventure.platform.fabric.FabricServerAudiences
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.ServerCommandSource


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
    }
}