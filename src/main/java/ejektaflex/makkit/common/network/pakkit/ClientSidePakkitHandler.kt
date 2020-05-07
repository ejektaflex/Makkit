package ejektaflex.makkit.common.network.pakkit

import ejektaflex.makkit.common.network.pakkit.PakkitHandler
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry

interface ClientSidePakkitHandler : PakkitHandler {
    fun registerS2C() {
        ClientSidePacketRegistry.INSTANCE.register(getId(), ::run)
    }
}