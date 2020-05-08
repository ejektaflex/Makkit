package ejektaflex.makkit.common.editor.operations

import ejektaflex.makkit.common.editor.EditAction
import ejektaflex.makkit.common.ext.wallBlocks
import net.minecraft.world.BlockView

class FillWallsOperation() : WorldOperation() {
    override fun getType() = Companion.Type.WALLS

    override fun calculate(action: EditAction, view: BlockView) {
        for (pos in action.box.wallBlocks()) {
            action.edit(pos, action.palette.random())
        }
    }
}