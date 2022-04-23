package dev.deltric.seed

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.kyori.adventure.platform.fabric.FabricServerAudiences
import net.minecraft.server.MinecraftServer

class Seed : ModInitializer {
    companion object  {
        lateinit var adventure: FabricServerAudiences
    }

    override fun onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(ServerLifecycleEvents.ServerStarting { server: MinecraftServer ->
            adventure = FabricServerAudiences.of(server)
        })
    }
}