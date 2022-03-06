package xyz.fragbots.fragmod.commands

import xyz.fragbots.fragmod.FragMod
import gg.essential.api.EssentialAPI
import gg.essential.api.commands.Command
import gg.essential.api.commands.DefaultHandler

class ConfigCommand : Command("examplemod") {

    @DefaultHandler
    fun handle() {
        EssentialAPI.getGuiUtil().openScreen(FragMod.config?.gui())
    }
}
