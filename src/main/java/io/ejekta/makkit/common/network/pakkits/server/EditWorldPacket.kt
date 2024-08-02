package io.ejekta.makkit.common.network.pakkits.server

import io.ejekta.kambrik.message.KambrikMsg
import io.ejekta.makkit.common.editor.NetworkHandler
import io.ejekta.makkit.common.editor.data.EditWorldOptions
import io.ejekta.makkit.common.editor.operations.WorldOperation
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction

@Serializable
data class EditWorldPacket(
        // The box that we want to edit
    var box: @Contextual Box = Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0),
        // The box that we should highlight when moving backwards in history
    var undoBox: @Contextual Box = Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0),
        // The side of the edit box that we are selecting
    var side: Direction = Direction.NORTH,
        // Which operation we are calling on the selection
    var operation: @Contextual WorldOperation,
        // Packet options
    var options: EditWorldOptions = EditWorldOptions(),
        // Which items we are using for the operation
        // TODO maybe we can make this block Identifier list instead for ease
    var palette: List<@Contextual ItemStack> = listOf()
) : KambrikMsg() {

    override fun onServerReceived(ctx: MsgContext) {
        println("Server received editworldpacket, handling..")
        NetworkHandler.handleEdit(ctx.player, this)
    }

    override fun getId() = ID

    companion object {
        val ID = CustomPayload.id<EditWorldPacket>("edit_world")
    }

}