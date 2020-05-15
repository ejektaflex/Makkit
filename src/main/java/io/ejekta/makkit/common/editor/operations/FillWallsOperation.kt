package io.ejekta.makkit.common.editor.operations

import io.ejekta.makkit.common.editor.data.EditAction
import io.ejekta.makkit.common.ext.wallBlocks
import net.minecraft.world.BlockView

class FillWallsOperation() : WorldOperation() {
    override fun getType() = Companion.Type.WALLS

    override fun calculate(action: EditAction, view: BlockView) {
        for (pos in action.selectionBox.wallBlocks()) {
            action.edit(pos, action.palette.random())
        }
    }
}