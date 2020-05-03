package ejektaflex.kalpis.common.world

import ejektaflex.kalpis.common.ext.getBlockArray

enum class WorldOperation(val execute: (it: EditAction) -> Unit) {

    FILL({  fillBlocks(it)  });

    private companion object {
        fun fillBlocks(action: EditAction) {
            println("Beepboop")
            for (pos in action.box.getBlockArray()) {
                action.player.world.setBlockState(pos, action.palette.random())
            }
        }
    }
}