package io.ejekta.makkit.common.editor.operations

import io.ejekta.makkit.common.editor.data.CopyData
import io.ejekta.makkit.common.editor.data.CopyHelper
import io.ejekta.makkit.common.editor.data.EditAction
import io.ejekta.makkit.common.ext.rotateClockwise
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3i
import net.minecraft.world.BlockView

class PasteOperation(val copy: CopyData) : WorldOperation() {

    override fun getType() = Companion.Type.PASTE

    override fun calculate(action: EditAction, view: BlockView) {

        val dirs = listOf(
                Direction.WEST,
                Direction.NORTH,
                Direction.EAST,
                Direction.SOUTH
        )

        println("Copy dir: ${copy.dir}, action dir: ${action.direction}")

        for (entry in copy.data) {

            val rotPos = BlockPos(entry.key.rotateClockwise(dirs.indexOf(action.direction)))

            val start = CopyHelper.getLocalAxisStartPos(action.box, action.direction)

            action.edit(start.add(rotPos), entry.value)
        }

    }
}