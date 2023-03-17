package io.ejekta.makkit.client.compat

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.common.MakkitCommon

class MakkitModMenu : ModMenuApi {

    override fun getModConfigScreenFactory(): ConfigScreenFactory<*> {
        return ConfigScreenFactory {
            MakkitClient.config.buildScreen()
        }
    }

}