package ejektaflex.makkit.common.network.pakkit

import ejektaflex.makkit.common.network.pakkit.PakkitHandler
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry

interface ClientPakkitHandler : PakkitHandler {
    fun registerC2S() {
        //ClientSidePacketRegistry.INSTANCE.register(getId(), ::run)
    }
}