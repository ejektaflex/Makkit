package ejektaflex.makkit.common.network.pakkit

import ejektaflex.makkit.common.network.pakkit.Pakkit
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry

interface ClientPakkit : Pakkit {
    fun sendToServer() {
        //println("Sending packet with ID: ${getId()}")
        //ClientSidePacketRegistry.INSTANCE.sendToServer(getId(), write())
    }
}