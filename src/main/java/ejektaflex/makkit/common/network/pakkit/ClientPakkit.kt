package ejektaflex.makkit.common.network.pakkit

import ejektaflex.makkit.common.network.pakkit.Pakkit
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry

interface ClientPakkit : Pakkit {
    fun sendToServer() {
        ClientSidePacketRegistry.INSTANCE.sendToServer(getId(), write())
    }
}