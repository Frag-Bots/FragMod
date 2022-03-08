package xyz.fragbots.fragmod

import com.google.gson.JsonParser
import gg.essential.api.EssentialAPI
import gg.essential.universal.wrappers.message.UTextComponent
import xyz.fragbots.fragmod.commands.ConfigCommand
import xyz.fragbots.fragmod.core.Config
import xyz.fragbots.fragmod.events.packet.PacketListener
import net.minecraft.client.Minecraft
import net.minecraft.client.settings.KeyBinding
import net.minecraft.network.login.server.S02PacketLoginSuccess
import net.minecraft.network.play.server.S01PacketJoinGame
import net.minecraft.util.IChatComponent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.apache.http.conn.ssl.SSLContexts
import org.lwjgl.input.Keyboard
import xyz.fragbots.fragmod.commands.FR
import xyz.fragbots.fragmod.commands.FragRunCommand
import xyz.fragbots.fragmod.events.packet.PacketEvent
import xyz.fragbots.fragmod.util.ApiUtils
import xyz.fragbots.fragmod.util.isValidCert
import java.awt.Desktop
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.GZIPInputStream
import javax.net.ssl.HttpsURLConnection

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
        const val VERSION = "1.1"
        const val configLocation = "./config/fragbots.toml"

        val mc: Minecraft = Minecraft.getMinecraft()
        var config: Config = Config()
        val fragrunBind = KeyBinding("Frag Run", Keyboard.KEY_NONE, "Frag Bots")
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
        FragRunCommand().register()
        FR().register()

        ClientRegistry.registerKeyBinding(fragrunBind)
        MinecraftForge.EVENT_BUS.register(this)
        MinecraftForge.EVENT_BUS.register(PacketListener())
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
        ApiUtils
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (FragBots.mc.theWorld == null || event.phase == TickEvent.Phase.END) return
        if (fragrunBind.isPressed) {
            FragRunCommand().handle()
        }
    }

    @SubscribeEvent
    fun onPacket(event: PacketEvent) {
        when (event.getPacket()) {
            is S01PacketJoinGame -> {
                if (!checkedUpdate) {
                    checkedUpdate = true;
                    Thread {
                        try {
                            val version = VERSION
                            val url = URL("https://api.fragbots.xyz/v2/mod/version")
                            val conn = url.openConnection() as HttpsURLConnection
                            // Thanks lily💕#0999 for this code
                            val socketFactory = SSLContexts.custom().useProtocol("TLS").loadTrustMaterial(null, ::isValidCert).build().socketFactory
                            conn.sslSocketFactory = socketFactory
                            conn.requestMethod = "GET"
                            conn.doOutput = true
                            conn.setRequestProperty("Accept-Encoding", "gzip")
                            conn.setRequestProperty("User-Agent", "FragBots-Mod/${FragBots.VERSION}")
                            val status = conn.responseCode
                            var content = "";
                            var stream = if (status < 299) conn.inputStream else conn.errorStream
                            stream = if (conn.contentEncoding == "gzip") {
                                GZIPInputStream(stream)
                            } else {
                                stream
                            }
                            val reader = BufferedReader(InputStreamReader(stream))

                            while (true) {
                                content += reader.readLine() ?: break
                            }

                            conn.disconnect()
                            if (status == 200) {
                                val json = JsonParser().parse(content).asJsonObject
                                val latest = json.get("version").asString
                                println("Latest version: $latest")
                                println("Current version: $version")
                                if (latest.toFloat() > version.toFloat()) {
                                    notify("A new version of Frag Bots is available! Click here to download it.", "https://github.com/Frag-Bots/FragMod/releases", 15)
                                }
                            } else {
                                notify("Failed to check for updates!")
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            notify("Failed to check for updates!")
                        }
                    }.start()
                }
            }
        }
    }


}
