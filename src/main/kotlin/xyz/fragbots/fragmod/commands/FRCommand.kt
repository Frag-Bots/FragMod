package xyz.fragbots.fragmod.commands

import gg.essential.api.commands.Command
import gg.essential.api.commands.DefaultHandler
import gg.essential.api.commands.SubCommand

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

    @SubCommand(value = "config", aliases = ["cfg"])
    fun config() {
        parent.config()
    }

    @SubCommand(value = "debug")
    fun debug() {
        parent.debug()
    }
}