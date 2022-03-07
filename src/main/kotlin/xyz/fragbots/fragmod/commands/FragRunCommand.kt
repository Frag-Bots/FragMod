package xyz.fragbots.fragmod.commands

import gg.essential.api.commands.Command
import gg.essential.api.commands.DefaultHandler
import gg.essential.api.commands.SubCommand
import gg.essential.universal.wrappers.message.UTextComponent
import net.minecraft.event.ClickEvent
import net.minecraft.event.HoverEvent
import xyz.fragbots.fragmod.FragBots
import xyz.fragbots.fragmod.util.APIUtil

class FragRunCommand : Command("fragrun") {

    @DefaultHandler
    fun handle() {
        when (FragBots.config.defaultBot) {
            0 -> {
                val bot = listOf("verified1", "verified2").random()
                partyBot(bot)
                return
            }
            1 -> {
                partyBot("whitelisted")
                return
            }
            2 -> {
                partyBot("exclusive")
                return
            }
            3 -> {
                partyBot("active")
                return
            }
            else -> {
                partyBot("custom")
                return
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
    @SubCommand(value = "debug")
    fun debug() {
        FragBots.chat("user: ${APIUtil.user} bots: ${APIUtil.bots}", false)
    }

    fun partyBot(bot: String) {
        when (bot) {
            "verified1", "verified2" -> {
                when (APIUtil.user?.verified) {
                    false -> {
                        FragBots.chat(UTextComponent("&cYou must be verified to use this bot. Join our &r").appendSibling(UTextComponent("&d&ndiscord&r").setClick(ClickEvent.Action.OPEN_URL, "https://discord.gg/fragbots").setHover(HoverEvent.Action.SHOW_TEXT, "Click to join!")).appendSibling(UTextComponent("&c to verify!\"")))
                        return
                    }
                    true -> {
                        if (bot == "verified1") {
                            FragBots.command("party ${APIUtil.bots!!.verified1}")
                            return
                        } else {
                            FragBots.command("party ${APIUtil.bots!!.verified2}")
                            return
                        }
                    }
                    null -> {
                        FragBots.notify("There was an error with our API. Please try again later.")
                        return
                    }
                }
            }
            "whitelisted" -> {
                when (APIUtil.user?.whitelisted) {
                    false -> {
                        FragBots.chat(UTextComponent("&cYou must be whitelisted to use this bot. Join our &r").appendSibling(UTextComponent("&d&ndiscord&r").setClick(ClickEvent.Action.OPEN_URL, "https://discord.gg/fragbots").setHover(HoverEvent.Action.SHOW_TEXT, "Click to join!")).appendSibling(UTextComponent("&c and make a ticket to get whitelisted!")))
                        return
                    }
                    true -> {
                        FragBots.command("party ${APIUtil.bots!!.whitelisted}")
                        return
                    }
                    null -> {
                        FragBots.notify("There was an error with our API. Please try again later.")
                        return
                    }
                }
            }
            "active" -> {
                when (APIUtil.user?.active) {
                    false -> {
                        FragBots.chat(UTextComponent("&cYou must be active in our &r").appendSibling(UTextComponent("&d&ndiscord&r").setClick(ClickEvent.Action.OPEN_URL, "https://discord.gg/fragbots").setHover(HoverEvent.Action.SHOW_TEXT, "Click to join!")).appendSibling(UTextComponent("&c server to use this bot!")))
                        return
                    }
                    true -> {
                        FragBots.command("party ${APIUtil.bots!!.active}")
                        return
                    }
                    null -> {
                        FragBots.notify("There was an error with our API. Please try again later.")
                        return
                    }
                }
            }
            "exclusive" -> {
                when (APIUtil.user?.exclusive) {
                    false -> {
                        FragBots.chat(UTextComponent("&cYou must have exclusive access. Join our &r").appendSibling(UTextComponent("&d&ndiscord&r").setClick(ClickEvent.Action.OPEN_URL, "https://discord.gg/fragbots").setHover(HoverEvent.Action.SHOW_TEXT, "Click to join!")).appendSibling(UTextComponent("&c for ways to get exclusive access!")))
                        return
                    }
                    true -> {
                        FragBots.command("party ${APIUtil.bots!!.exclusive}")
                        return
                    }
                    null -> {
                        FragBots.notify("There was an error with our API. Please try again later.")
                        return
                    }
                }
            }
            "custom" -> {
                val username = FragBots.config.customIGN
                if (username == "") {
                    FragBots.chat(UTextComponent("&cYou must set a custom bot in the ").appendSibling(UTextComponent("&b&nconfig").setClick(ClickEvent.Action.RUN_COMMAND, "/fragbots").setHover(HoverEvent.Action.SHOW_TEXT, "Click to open config!")).appendSibling(UTextComponent("&c to use this bot!")))
                } else {
                    FragBots.command("party $username")
                }
            }
        }
    }
}