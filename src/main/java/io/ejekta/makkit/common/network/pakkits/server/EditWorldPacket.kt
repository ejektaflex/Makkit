package io.ejekta.makkit.common.network.pakkits.server

import com.google.gson.GsonBuilder
import io.ejekta.makkit.common.MakkitCommon
import io.ejekta.makkit.common.editor.NetworkHandler
import io.ejekta.makkit.common.editor.operations.FillBlocksOperation
import io.ejekta.makkit.common.editor.operations.WorldOperation
import io.ejekta.makkit.common.ext.readEnum
import io.ejekta.makkit.common.ext.readIntBox
import io.ejekta.makkit.common.ext.writeEnum
import io.ejekta.makkit.common.ext.writeIntBox
import io.ejekta.makkit.common.network.pakkit.ServerBoundPakkit
import io.ejekta.makkit.common.network.pakkit.ServerSidePakkitHandler
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction

class EditWorldPacket(
        // The box that we want to edit
        var box: Box = Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0),
        // The box that we should highlight when moving backwards in history
        var undoBox: Box = Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0),
        // The side of the edit box that we are selecting
        var side: Direction = Direction.NORTH,
        // Which operation we are calling on the selection
        var op: WorldOperation = FillBlocksOperation(),
        // Whether items in a palette should be weighted based on their stack sizes
        var weightedPalette: Boolean = false,
        // Whether rotatable blocks should be randomly rotated
        var randomRotate: Boolean = false,
        // Which items we are using for the operation
        var palette: List<ItemStack> = listOf()
) : ServerBoundPakkit {

    constructor(buffer: PacketByteBuf) : this() {
        read(buffer)
    }

    override fun getId() = ID

    override fun write(): PacketByteBuf {
        return PacketByteBuf(Unpooled.buffer()).apply {
            writeIntBox(box)
            writeIntBox(undoBox)
            writeEnum(side)
            writeEnum(op.getType())
            writeString(gson.toJson(op, op.getType().clazz.java))
            writeBoolean(weightedPalette)
            writeBoolean(randomRotate)
            writeInt(palette.size)
            for (item in palette) {
                writeItemStack(item)
            }
        }
    }

    override fun read(buf: PacketByteBuf) {
        box = buf.readIntBox()
        undoBox = buf.readIntBox()
        side = buf.readEnum()
        val opType = buf.readEnum<WorldOperation.Companion.Type>()
        op = gson.fromJson(buf.readString(), opType.clazz.java)
        weightedPalette = buf.readBoolean()
        randomRotate = buf.readBoolean()
        palette = mutableListOf<ItemStack>().apply {
            val paletteSize = buf.readInt()
            for (i in 0 until paletteSize) {
                add(
                        buf.readItemStack()
                )
            }
        }
    }

    companion object : ServerSidePakkitHandler {
        val ID = Identifier(MakkitCommon.ID, "edit_intent")

        val gson = GsonBuilder().setPrettyPrinting().create()

        override fun getId() = ID

        override fun run(context: PacketContext, buffer: PacketByteBuf) {
            val intent = EditWorldPacket(buffer)
            context.taskQueue.execute {
                NetworkHandler.handleEdit(context.player as ServerPlayerEntity, intent)
            }
        }

    }

}