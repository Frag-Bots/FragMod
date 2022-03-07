package xyz.fragbots.fragmod

import com.google.gson.JsonParser
import gg.essential.api.EssentialAPI
import gg.essential.universal.utils.MCClickEventAction
import gg.essential.universal.wrappers.message.UTextComponent
import xyz.fragbots.fragmod.commands.ConfigCommand
import xyz.fragbots.fragmod.core.Config
import xyz.fragbots.fragmod.events.packet.PacketListener
import net.minecraft.client.Minecraft
import net.minecraft.network.play.server.S01PacketJoinGame
import net.minecraft.util.ChatComponentText
import net.minecraft.util.IChatComponent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import xyz.fragbots.fragmod.events.packet.PacketEvent
import java.awt.Desktop
import java.net.HttpURLConnection
import java.net.URL

@Mod(
    modid = FragBots.MOD_ID,
    name = FragBots.MOD_NAME,
    version = FragBots.VERSION,
    clientSideOnly = true,
    acceptedMinecraftVersions = "[1.8.9]"
)
class FragBots {

    companion object {
        const val MOD_ID = "fragbots"
        const val MOD_NAME = "FragBots"
        const val VERSION = "1.0"
        const val configLocation = "./config/fragbots.toml"

        val mc: Minecraft = Minecraft.getMinecraft()
        var config: Config = Config()

        var checkedUpdate = false

        val PREFIX = "&8[&cFrag Bots&8]&r "

        fun chat(message: String, prefix: Boolean = true) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(UTextComponent(if (prefix) "$PREFIX$message" else message))
        }

        fun chat(message: IChatComponent, prefix: Boolean = true) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(if (prefix) UTextComponent(PREFIX).appendSibling(message) else message)
        }

        fun command(command: String) {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/$command")
        }

        fun notify(message: String, duration: Int = 5) {
            EssentialAPI.getNotifications().push("Frag Bots", message, duration.toFloat())
        }

        fun notify(message: String, url: String , duration: Int = 5) {
            EssentialAPI.getNotifications().push("Frag Bots", message, duration.toFloat()
            ) { Desktop.getDesktop().browse(URL(url).toURI()) }
        }
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        config.preload()
        ConfigCommand().register()

        MinecraftForge.EVENT_BUS.register(PacketListener())
    }

    @SubscribeEvent
    fun onPacket(event: PacketEvent) {
        when (event.getPacket()) {
            is S01PacketJoinGame -> {
                if (checkedUpdate) {
                    Thread {
                        try {
                            val version = VERSION
                            val url = URL("https://api.fragbots.xyz/v2/mod/version")
                            val conn = url.openConnection() as HttpURLConnection
                            conn.requestMethod = "GET"
                            conn.doOutput = true
                            conn.setRequestProperty("Accept-Encoding", "gzip")
                            conn.setRequestProperty("User-Agent", "FragBots-Mod/${FragBots.VERSION}")
                            val status = conn.responseCode
                            var content: String
                            var stream = if (status < 299) conn.inputStream else conn.errorStream
                            stream = if (conn.contentEncoding.contains("gzip")) {
                                val gzip = stream.buffered()
                                gzip.use {
                                    java.util.zip.GZIPInputStream(it)
                                }
                            } else {
                                stream
                            }
                            stream.buffered().use {
                                content = it.reader().readText()
                            }
                            conn.disconnect()
                            if (status == 200) {
                                val json = JsonParser().parse(content).asJsonObject
                                val latest = json.get("version").asString
                                if (latest != version) {
                                    notify("A new version of Frag Bots is available! Click here to download it.", "https://github.com/Frag-Bots/FragMod/releases")
                                }
                            } else {
                                notify("Failed to check for updates!")
                            }
                        } catch (e: Exception) {
                            notify("Failed to check for updates!")
                        }
                    }
                }
            }
        }
    }


}
