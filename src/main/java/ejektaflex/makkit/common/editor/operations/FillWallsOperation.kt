package ejektaflex.makkit.common.editor.operations

import ejektaflex.makkit.common.editor.EditAction
import ejektaflex.makkit.common.ext.wallBlocks
import net.minecraft.world.World

class FillWallsOperation() : WorldOperation() {
    override fun getType() = Companion.Type.WALLS

    override fun calculate(action: EditAction, world: World) {
        for (pos in action.box.wallBlocks()) {
            action.edit(pos, action.palette.random())
        }
    }
}