package ejektaflex.makkit.common.network.pakkit

import ejektaflex.makkit.common.network.pakkit.PakkitHandler
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry

interface ServerSidePakkitHandler : PakkitHandler {
    fun registerC2S() {
        ServerSidePacketRegistry.INSTANCE.register(getId(), ::run)
    }
}