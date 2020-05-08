package ejektaflex.makkit.client.compat

import ejektaflex.makkit.client.MakkitClient
import ejektaflex.makkit.client.config.MakkitConfig
import ejektaflex.makkit.common.MakkitCommon
import io.github.prospector.modmenu.api.ConfigScreenFactory
import io.github.prospector.modmenu.api.ModMenuApi

class MakkitModMenu : ModMenuApi {

    override fun getModId(): String {
        return MakkitCommon.ID
    }

    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return ConfigScreenFactory {
            MakkitClient.config.buildScreen()
        }
    }

}