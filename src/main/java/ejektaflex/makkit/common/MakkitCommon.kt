package ejektaflex.makkit.common

import ejektaflex.makkit.client.network.EditIntentPacket
import ejektaflex.makkit.common.network.EditIntentPacketHandler
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry

class MakkitCommon : ModInitializer {
    override fun onInitialize() {
        //EditIntentPacketHandler.registerC2S()
        println("Common init")

    }
}