package ejektaflex.makkit.common.world

import ejektaflex.makkit.common.ext.getBlockArray
import ejektaflex.makkit.common.ext.wallBlocks

enum class WorldOperation(val execute: (it: EditAction) -> Unit) {

    FILL({  fillBlocks(it)  }),
    WALLS({ fillWalls(it) });

    private companion object {
        fun fillBlocks(action: EditAction) {
            for (pos in action.box.getBlockArray()) {
                action.edit(pos, action.palette.random())
            }
        }

        fun fillWalls(action: EditAction) {
            for (pos in action.box.wallBlocks()) {
                action.edit(pos, action.palette.random())
            }
        }
    }
}