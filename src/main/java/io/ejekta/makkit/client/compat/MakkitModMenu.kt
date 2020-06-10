package io.ejekta.makkit.client.compat

import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.common.MakkitCommon
import io.github.prospector.modmenu.api.ConfigScreenFactory
import io.github.prospector.modmenu.api.ModMenuApi

class MakkitModMenu : ModMenuApi {

    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return ConfigScreenFactory {
            MakkitClient.config.buildScreen()
        }
    }

}