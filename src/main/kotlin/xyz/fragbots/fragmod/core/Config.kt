package xyz.fragbots.fragmod.core

import xyz.fragbots.fragmod.FragBots
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import xyz.fragbots.fragmod.util.APIUtil

import java.io.File

class Config : Vigilant(File(FragBots.configLocation)) {

    @Property(
        name = "Default Bot",
        description = "The bot that will be partied when typing /fragrun",
        type = PropertyType.SELECTOR,
        category = "Frag Bots",
        options = ["Verified", "Whitelisted", "Exclusive", "Active", "Custom IGN"]
    )
    var defaultBot = 0

    @Property(
        name = "Custom IGN",
        description = "The IGN of the bot that will be partied when typing /fragrun\n&8  - Note: You must select the Custom IGN option above for this to work.",
        type = PropertyType.TEXT,
        category = "Frag Bots"
    )
    var customIGN = "";

    @Property(
        name = "Refresh Bot List",
        description = "Fetches the bot list from the API.",
        type = PropertyType.BUTTON,
        category = "Frag Bots",
        placeholder = "Refresh"
    )
    fun onClick() {
        Thread {
            APIUtil.fetchBots()
            FragBots.notify("Bot list refreshed.")
        }.start()
    }


    init {
        initialize()
    }
}
