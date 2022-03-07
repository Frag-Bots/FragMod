package xyz.fragbots.fragmod.commands

import com.google.gson.JsonParser
import gg.essential.api.commands.Command
import gg.essential.api.commands.DefaultHandler
import gg.essential.api.commands.SubCommand
import gg.essential.universal.wrappers.message.UTextComponent
import net.minecraft.client.settings.KeyBinding
import net.minecraft.event.ClickEvent
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.Loader
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.input.Keyboard
import xyz.fragbots.fragmod.FragBots
import xyz.fragbots.fragmod.util.hash
import java.net.HttpURLConnection
import java.net.URL
import java.util.Timer

class FragRunCommand : Command("fragrun") {

    data class User(val verified: Boolean?, val whitelisted: Boolean?, val active: Boolean?, val exclusive: Boolean?)
    data class Bots(val verified1: String?, val verified2: String?, val whitelisted: String?, val active: String?, val exclusive: String?)

    val fragrunBind = KeyBinding("Frag Run", Keyboard.KEY_NONE, "Frag Bots")

    var user: User? = null
    var bots: Bots? = null
    val timer = Timer()

    @DefaultHandler
    fun handle() {
        when (FragBots.config.defaultBot) {
            0 -> {
                val bot = listOf("verified1", "verified2").random()
                partyBot(bot)
            }
            1 -> {
                partyBot("whitelisted")
            }
            2 -> {
                partyBot("exclusive")
            }
            3 -> {
                partyBot("active")
            }
            else -> {
                partyBot("custom")
            }
        }
    }

    @SubCommand(value = "verified", aliases = ["v"])
    fun verified() {
        partyBot("verified1")
    }

    @SubCommand(value = "whitelisted", aliases = ["whitelist", "wl"])
    fun whitelisted() {
        partyBot("whitelisted")
    }

    @SubCommand(value = "active", aliases = ["ac"])
    fun active() {
        partyBot("active")
    }

    @SubCommand(value = "exclusive", aliases = ["ex"])
    fun exclusive() {
        partyBot("exclusive")
    }

    fun partyBot(bot: String) {
        when (bot) {
            "verified1", "verified2" -> {
                when (user!!.verified) {
                    false -> {
                        FragBots.chat(UTextComponent("&cYou must be verified to use this bot. Join our &r").appendSibling(UTextComponent("&d&ndiscord&r").setClick(ClickEvent.Action.OPEN_URL, "https://discord.gg/fragbots")).appendText("&c to verify!"))
                        return
                    }
                    true -> {
                        if (bot == "verified1") {
                            FragBots.command("/party ${bots!!.verified1}")
                            return
                        } else {
                            FragBots.command("/party ${bots!!.verified2}")
                            return
                        }
                    }
                    else -> {
                        FragBots.notify("There was an error with our API. Please try again later.")
                        return
                    }
                }
            }
            "whitelisted" -> {
                when (user!!.whitelisted) {
                    false -> {
                        FragBots.chat(UTextComponent("&cYou must be whitelisted to use this bot. Join our &r").appendSibling(UTextComponent("&d&ndiscord&r").setClick(ClickEvent.Action.OPEN_URL, "https://discord.gg/fragbots")).appendText("&c and make a ticket to get whitelisted!"))
                        return
                    }
                    true -> {
                        FragBots.command("/party ${bots!!.whitelisted}")
                        return
                    }
                    else -> {
                        FragBots.notify("There was an error with our API. Please try again later.")
                        return
                    }
                }
            }
            "active" -> {
                when (user!!.active) {
                    false -> {
                        FragBots.chat(UTextComponent("&cYou must be active in our &r").appendSibling(UTextComponent("&d&ndiscord&r").setClick(ClickEvent.Action.OPEN_URL, "https://discord.gg/fragbots")).appendText("&c server to use this bot!"))
                        return
                    }
                    true -> {
                        FragBots.command("/party ${bots!!.active}")
                        return
                    }
                    else -> {
                        FragBots.notify("There was an error with our API. Please try again later.")
                        return
                    }
                }
            }
            "exclusive" -> {
                when (user!!.exclusive) {
                    false -> {
                        FragBots.chat(UTextComponent("&cYou must be exclusive to use this bot. Join our &r").appendSibling(UTextComponent("&d&ndiscord&r").setClick(ClickEvent.Action.OPEN_URL, "https://discord.gg/fragbots")).appendText("&c and make a ticket to get exclusive!"))
                        return
                    }
                    true -> {
                        FragBots.command("/party ${bots!!.exclusive}")
                        return
                    }
                    else -> {
                        FragBots.notify("There was an error with our API. Please try again later.")
                        return
                    }
                }
            }
            "custom" -> {
                val username = FragBots.config.customIGN
                if (username == "") {
                    FragBots.chat(UTextComponent("&cYou must set a custom bot in the ").appendSibling(UTextComponent("&b&nconfig").setClick(ClickEvent.Action.RUN_COMMAND, "/fragbots")).appendText("&c to use this bot!"))
                } else {
                    FragBots.command("/party $username")
                }
            }
        }
    }

    fun fetchBots() {
        try {
            val sessionId = if (Loader.isModLoaded("pizzaclient")) "" else FragBots.mc.session.sessionID
            val serverId = hash(FragBots.mc.thePlayer.uniqueID.toString())
            val username = FragBots.mc.thePlayer.name
            FragBots.mc.sessionService.joinServer(FragBots.mc.thePlayer.gameProfile, sessionId, serverId)
            val url = URL("https://api.fragbots.xyz/v2/mod?serverId=$serverId&username=$username")
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
            val json = JsonParser().parse(content).asJsonObject
            val user = json.getAsJsonObject("user")
            val bots = json.getAsJsonObject("bots")
            this.user = User(
                    verified = user.get("verified").asBoolean,
                    whitelisted = user.get("whiteListed").asBoolean,
                    active = user.get("active").asBoolean,
                    exclusive = user.get("exclusive").asBoolean
            )
            this.bots = Bots(
                    verified1 = bots.get("verified1").asString,
                    verified2 = bots.get("verified2").asString,
                    whitelisted = bots.get("whitelisted").asString,
                    active = bots.get("active").asString,
                    exclusive = bots.get("exclusive").asString
            )
        } catch (e: Exception) {
            e.printStackTrace()
            if (this.bots == null) {
                this.bots = Bots(null, null, null, null, null)
            }
            if (this.user == null) {
                this.user = User(null, null, null, null)
            }
        }
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (FragBots.mc.theWorld == null || event.phase == TickEvent.Phase.END) return
        if (fragrunBind.isPressed) {
            handle()
        }
    }

    init {
        Thread {
            fetchBots()
        }.start()

        timer.scheduleAtFixedRate(object : java.util.TimerTask() {
            override fun run() {
                fetchBots()
            }
        }, 0, 60000)

        ClientRegistry.registerKeyBinding(fragrunBind)
        FR().register()
    }
}

class FR : Command("fr") {
    val parent = FragRunCommand()
    @DefaultHandler
    fun handle() {
        parent.handle()
    }

    @SubCommand(value = "verified", aliases = ["v"])
    fun verified() {
        parent.verified()
    }

    @SubCommand(value = "whitelisted", aliases = ["whitelist", "wl"])
    fun whitelisted() {
        parent.whitelisted()
    }

    @SubCommand(value = "active", aliases = ["ac"])
    fun active() {
        parent.active()
    }

    @SubCommand(value = "exclusive", aliases = ["ex"])
    fun exclusive() {
        parent.exclusive()
    }
}