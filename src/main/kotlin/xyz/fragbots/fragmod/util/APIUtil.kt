package xyz.fragbots.fragmod.util

import com.google.gson.JsonParser
import net.minecraftforge.fml.common.Loader
import xyz.fragbots.fragmod.FragBots
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.zip.GZIPInputStream

object APIUtil {
    data class User(val verified: Boolean?, val whitelisted: Boolean?, val active: Boolean?, val exclusive: Boolean?)
    data class Bots(val verified1: String?, val verified2: String?, val whitelisted: String?, val active: String?, val exclusive: String?)

    var user: User? = null
    var bots: Bots? = null
    val timer = Timer()

    fun fetchBots() {

        try {
            val sessionId = if (Loader.isModLoaded("pizzaclient")) "" else FragBots.mc.session.token
            val serverId = hash(FragBots.mc.session.playerID)
            val username = FragBots.mc.session.username
            FragBots.mc.sessionService.joinServer(FragBots.mc.session.profile, sessionId, serverId)
            val url = URL("https://api.fragbots.xyz/v2/mod?serverId=$serverId&username=$username")
            val conn = url.openConnection() as HttpURLConnection
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
            val json = JsonParser().parse(content).asJsonObject
            val user = json.getAsJsonObject("user")
            val bots = json.getAsJsonObject("bots")
            this.user = User(
                verified = user.get("verified").asBoolean,
                whitelisted = user.get("whitelisted").asBoolean,
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

    init {
        Thread {
            fetchBots()
        }.start()

        timer.scheduleAtFixedRate(object : java.util.TimerTask() {
            override fun run() {
                fetchBots()
            }
        }, 0, 60000)
    }
}