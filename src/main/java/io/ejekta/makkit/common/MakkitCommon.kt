package io.ejekta.makkit.common

import io.ejekta.kambrik.Kambrik
import io.ejekta.kambrik.message.KambrikMsg
import io.ejekta.makkit.client.event.Events
import io.ejekta.makkit.common.editor.operations.WorldOperation
import io.ejekta.makkit.common.network.pakkits.client.FocusRegionPacket
import io.ejekta.makkit.common.network.pakkits.client.ShadowBoxShowPacket
import io.ejekta.makkit.common.network.pakkits.server.EditWorldPacket
import io.ejekta.makkit.common.network.pakkits.server.ShadowBoxUpdatePacket
import kotlinx.serialization.KSerializer
import kotlinx.serialization.modules.SerializersModule
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier

class MakkitCommon : ModInitializer {


    companion object {
        const val ID = "makkit"

        fun onServerPlayerDisconnect(e: Events.ServerDisconnectEvent) {
            for (player in e.player.world.players) {
                ShadowBoxShowPacket(uid = e.player.uuidAsString, disconnect = true).sendToClient(player as ServerPlayerEntity)
            }
        }
    }

    fun <M : KambrikMsg> KSerializer<M>.toSimplePacketCodecTwo(): PacketCodec<RegistryByteBuf, M> {
        val json = Kambrik.Serial.networkingFormat()
        return PacketCodec.of(
            { value, buf -> buf.writeString(json.encodeToString(this, value)) },
            { json.decodeFromString(this, it.readString()) }
        )
    }

    override fun onInitialize() {

//        Kambrik.Message.addSerializerModule(
//            SerializersModule {
//                //contextual(WorldOperation::class, WorldOperation.serializer())
//            }
//        )

//        PayloadTypeRegistry.playS2C().register(ShadowBoxShowPacket.ID, ShadowBoxShowPacket.serializer().toSimplePacketCodecTwo())
//        ClientPlayNetworking.registerGlobalReceiver(ShadowBoxShowPacket.ID) { payload, context ->
//            println("Woo!")
//            (payload as KambrikMsg).onClientReceived()
//        }

        Kambrik.Message.registerClientMessage(
            FocusRegionPacket.serializer(),
            FocusRegionPacket.ID
        )

        Kambrik.Message.registerClientMessage(
            ShadowBoxShowPacket.serializer(),
            ShadowBoxShowPacket.ID
        )

        // Serverbound packets
        Kambrik.Message.registerServerMessage(EditWorldPacket.serializer(), EditWorldPacket.ID)
        //Kambrik.Message.registerServerMessage(EditHistoryPacket.serializer(), EditHistoryPacket::class, Identifier(ID, "edit_history"))
        Kambrik.Message.registerServerMessage(ShadowBoxUpdatePacket.serializer(), ShadowBoxUpdatePacket.ID)
        //Kambrik.Message.registerServerMessage(ClipboardIntentPacket.serializer(), ClipboardIntentPacket::class, Identifier(ID, "clipboard_intent"))

        Events.ServerDisconnectEvent.Dispatcher.register(::onServerPlayerDisconnect)

        println("Common init")
    }


}