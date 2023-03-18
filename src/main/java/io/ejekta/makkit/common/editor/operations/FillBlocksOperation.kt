package io.ejekta.makkit.common.editor.operations

import io.ejekta.makkit.common.editor.data.EditAction
import io.ejekta.makkit.common.ext.getBlockArray
import net.minecraft.text.Text
import net.minecraft.world.BlockView

class FillBlocksOperation : WorldOperation() {
    override fun getType() = Companion.Type.SET

    override fun calculate(action: EditAction, view: BlockView) {
        if (action.palette.isEmpty()) {
            action.player.sendMessage(Text.literal("You must have a valid block or item in your palette!"), true)
            return
        }

        for (pos in action.box.getBlockArray()) {
            action.edit(pos, action.palette.getRandom())
        }
    }
}