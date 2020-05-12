package ejektaflex.makkit.common.network.pakkits.server

import com.google.gson.GsonBuilder
import ejektaflex.makkit.common.network.pakkit.ServerBoundPakkit
import ejektaflex.makkit.common.network.pakkit.ServerSidePakkitHandler
import ejektaflex.makkit.common.editor.*
import ejektaflex.makkit.common.editor.operations.FillBlocksOperation
import ejektaflex.makkit.common.editor.operations.WorldOperation
import ejektaflex.makkit.common.ext.readEnum
import ejektaflex.makkit.common.ext.readIntBox
import ejektaflex.makkit.common.ext.writeEnum
import ejektaflex.makkit.common.ext.writeIntBox
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.network.PacketContext
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction

class EditWorldPacket(
        var box: Box = Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0),
        var side: Direction = Direction.NORTH,
        var op: WorldOperation = FillBlocksOperation(),
        var palette: List<ItemStack> = listOf()
) : ServerBoundPakkit {

    constructor(buffer: PacketByteBuf) : this() {
        read(buffer)
    }

    override fun getId() = ID

    override fun write(): PacketByteBuf {
        return PacketByteBuf(Unpooled.buffer()).apply {
            writeIntBox(box)
            writeEnum(side)
            writeEnum(op.getType())
            writeString(gson.toJson(op, op.getType().clazz.java))
            writeInt(palette.size)
            for (item in palette) {
                writeItemStack(item)
            }
        }
    }

    override fun read(buf: PacketByteBuf) {
        box = buf.readIntBox()
        side = buf.readEnum()
        val opType = buf.readEnum<WorldOperation.Companion.Type>()
        op = gson.fromJson(buf.readString(), opType.clazz.java)
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
        val ID = Identifier("makkit", "edit_intent")

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