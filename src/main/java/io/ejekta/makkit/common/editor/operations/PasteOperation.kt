package io.ejekta.makkit.common.editor.operations

import io.ejekta.makkit.common.editor.data.CopyData
import io.ejekta.makkit.common.editor.data.EditAction
import io.ejekta.makkit.common.ext.rotateClockwise
import io.ejekta.makkit.common.ext.startBlock
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView

class PasteOperation(val copy: CopyData, otherAxis: Boolean) : WorldOperation() {

    override fun getType() = Companion.Type.PASTE

    override fun calculate(action: EditAction, view: BlockView) {

//        println("${action.direction}, ${copy.dir}")
//
//        var currFace = action.direction
//
//        var timesToRotate = 0
//        while (currFace != copy.dir) {
//            currFace = currFace.rotateYClockwise()
//            timesToRotate++
//        }
//
//        val copyMap = copy.data.map {
//            println("I: ${it.key}, ${it.key.rotateClockwise(timesToRotate)}")
//            BlockPos(it.key.rotateClockwise(timesToRotate)) to it.value
//        }.toMap()


        for (entry in copy.data) {

//            val newPos = action.box.startBlock().add(entry.key)

            action.edit(entry.key, entry.value)
        }

    }
}