package io.ejekta.makkit.common.editor.operations

import io.ejekta.makkit.common.editor.data.EditAction
import io.ejekta.makkit.common.ext.wallBlocks
import net.minecraft.text.LiteralText
import net.minecraft.world.BlockView

class FillWallsOperation : WorldOperation() {
    override fun getType() = Companion.Type.WALLS

    override fun calculate(action: EditAction, view: BlockView) {
        if (action.palette.isEmpty()) {
            action.player.sendMessage(LiteralText("You must have a valid block or item in your palette!"), true)
            return
        }

        for (pos in action.box.wallBlocks()) {
            action.edit(pos, action.palette.getRandom())
        }
    }
}