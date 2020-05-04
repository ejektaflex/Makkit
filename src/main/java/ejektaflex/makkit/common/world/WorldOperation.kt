package ejektaflex.makkit.common.world

import ejektaflex.makkit.common.ext.getBlockArray

enum class WorldOperation(val execute: (it: EditAction) -> Unit) {

    FILL({  fillBlocks(it)  });

    private companion object {
        fun fillBlocks(action: EditAction) {
            for (pos in action.box.getBlockArray()) {
                action.edit(pos, action.palette.random())
            }
        }
    }
}