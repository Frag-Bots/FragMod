package xyz.fragbots.fragmod.commands

import xyz.fragbots.fragmod.FragBots
import gg.essential.api.EssentialAPI
import gg.essential.api.commands.Command
import gg.essential.api.commands.DefaultHandler

class ConfigCommand : Command("fragbots") {

    @DefaultHandler
    fun handle() {
        EssentialAPI.getGuiUtil().openScreen(FragBots.config.gui())
    }
}
