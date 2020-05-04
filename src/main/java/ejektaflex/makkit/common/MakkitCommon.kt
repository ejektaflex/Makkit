package ejektaflex.makkit.common

import ejektaflex.makkit.common.network.pakkits.EditIntentPacket
import net.fabricmc.api.ModInitializer

class MakkitCommon : ModInitializer {
    override fun onInitialize() {
        EditIntentPacket.registerC2S()
        println("Common init")
    }
}