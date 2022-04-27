package dev.deltric.seed

import com.google.gson.GsonBuilder
import com.mojang.brigadier.CommandDispatcher
import dev.deltric.seed.api.economy.CurrencyRegistry
import dev.deltric.seed.util.adapters.ComponentAdapter
import dev.deltric.seed.util.adapters.IdentifierAdapter
import dev.deltric.seed.util.manifests.CurrencyManifest
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import net.kyori.adventure.platform.fabric.FabricServerAudiences
import net.kyori.adventure.text.Component
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.util.Identifier
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class Seed : ModInitializer {
    companion object  {
        lateinit var adventure: FabricServerAudiences
    }

    override fun onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(ServerLifecycleEvents.ServerStarting { server: MinecraftServer ->
            adventure = FabricServerAudiences.of(server)
            val seedDirectory = FabricLoader.getInstance().configDir.resolve("seed").toFile()
            if(!seedDirectory.exists()) {
                seedDirectory.mkdir()
            }
            this.loadCurrencies(seedDirectory);
        })

        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource>, dedicated: Boolean ->
            if (!dedicated) return@CommandRegistrationCallback
        })
    }

    private fun loadCurrencies(seedDirectory: File) {
        val gson = GsonBuilder()
            .registerTypeAdapter(Component::class.java, ComponentAdapter)
            .registerTypeAdapter(Identifier::class.java, IdentifierAdapter)
            .setPrettyPrinting()
            .create()
        val currencyFile = File(seedDirectory, "currencies.json")

        if(!currencyFile.exists()) {
            // Create empty currencies manifest if it doesn't exist
            val fileWriter = FileWriter(currencyFile)
            gson.toJson(CurrencyManifest(listOf()), fileWriter)
            fileWriter.flush()
            fileWriter.close()
        } else {
            // Otherwise, load manifest and register currencies
            val fileReader = FileReader(currencyFile)
            val manifest = gson.fromJson(fileReader, CurrencyManifest::class.java)
            manifest.currencies.forEach(CurrencyRegistry::register)
        }
    }
}