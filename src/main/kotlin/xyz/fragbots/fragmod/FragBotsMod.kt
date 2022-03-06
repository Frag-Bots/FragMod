package xyz.fragbots.fragmod

import xyz.fragbots.fragmod.commands.ConfigCommand
import xyz.fragbots.fragmod.core.Config
import xyz.fragbots.fragmod.events.packet.PacketListener
import net.minecraft.client.Minecraft
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent

@Mod(
    modid = FragMod.MOD_ID,
    name = FragMod.MOD_NAME,
    version = FragMod.VERSION
)
class FragMod {

    companion object {
        const val MOD_ID = "fragmod"
        const val MOD_NAME = "FragBotsMod"
        const val VERSION = "1.0"
        const val configLocation = "./config/fragmod.toml" //sec

        val mc: Minecraft = Minecraft.getMinecraft()
        var config: Config? = null
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        config = Config()
        config?.preload()

        ConfigCommand().register()

        MinecraftForge.EVENT_BUS.register(PacketListener())
    }
}
