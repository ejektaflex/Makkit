package io.ejekta.makkit.common.network.pakkits.server

import com.google.gson.GsonBuilder
import io.ejekta.makkit.common.MakkitCommon
import io.ejekta.makkit.common.editor.NetworkHandler
import io.ejekta.makkit.common.editor.data.EditWorldOptions
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
import kotlin.reflect.KClass

class EditWorldPacket(
        // The box that we want to edit
        var box: Box = Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0),
        // The box that we should highlight when moving backwards in history
        var undoBox: Box = Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0),
        // The side of the edit box that we are selecting
        var side: Direction = Direction.NORTH,
        // Which operation we are calling on the selection
        var worldOpCode: WorldOperation = FillBlocksOperation(),
        // Packet options
        var options: EditWorldOptions = EditWorldOptions(),
        // Which items we are using for the operation
        var palette: List<ItemStack> = listOf()
) : ServerBoundPakkit {

    constructor(buffer: PacketByteBuf) : this() {
        read(buffer)
    }

    override fun getId() = ID

    fun <T : Any> PacketByteBuf.writeObject(obj: T, clazz: KClass<out T> = obj::class) {
        writeString(gson.toJson(obj, clazz.java))
    }

    inline fun <reified T : Any> PacketByteBuf.readObject(clazz: KClass<out T> = T::class): T {
        return gson.fromJson(readString(), clazz.java)
    }

    override fun write(): PacketByteBuf {
        return PacketByteBuf(Unpooled.buffer()).apply {
            writeIntBox(box)
            writeIntBox(undoBox)
            writeEnum(side)
            writeEnum(worldOpCode.getType())
            writeObject(worldOpCode, worldOpCode.getType().clazz)
            writeObject(options)
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
        worldOpCode = buf.readObject(opType.clazz)

        options = buf.readObject()

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