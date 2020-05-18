package io.ejekta.makkit.common.network.pakkits.server

import io.ejekta.makkit.common.MakkitCommon
import io.ejekta.makkit.common.editor.NetworkHandler
import io.ejekta.makkit.common.enums.ClipboardMode
import io.ejekta.makkit.common.ext.readEnum
import io.ejekta.makkit.common.ext.readIntBox
import io.ejekta.makkit.common.ext.writeEnum
import io.ejekta.makkit.common.ext.writeIntBox
import io.ejekta.makkit.common.network.pakkit.ServerBoundPakkit
import io.ejekta.makkit.common.network.pakkit.ServerSidePakkitHandler
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction

data class ClipboardIntentPacket(
        var mode: ClipboardMode = ClipboardMode.CUT,
        var face: Direction = Direction.UP,
        var box: Box = Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)
) : ServerBoundPakkit {

    constructor(buffer: PacketByteBuf) : this() {
        read(buffer)
    }

    override fun getId() = ID

    override fun read(buf: PacketByteBuf) {
        mode = buf.readEnum()
        face = buf.readEnum()
        box = buf.readIntBox()
    }

    override fun write(): PacketByteBuf {
        return PacketByteBuf(Unpooled.buffer()).apply {
            writeEnum(mode)
            writeEnum(face)
            writeIntBox(box)
        }
    }

    companion object : ServerSidePakkitHandler {

        val ID = Identifier(MakkitCommon.ID, "clipboard_intent")

        override fun getId() = ID

        override fun run(context: PacketContext, buffer: PacketByteBuf) {
            val pakkit = ClipboardIntentPacket(buffer)
            context.taskQueue.execute {
                NetworkHandler.handleCopyPaste(context.player as ServerPlayerEntity, pakkit)
            }
        }

    }


}