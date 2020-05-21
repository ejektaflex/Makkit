package io.ejekta.makkit.common.editor.operations

import io.ejekta.makkit.common.editor.data.EditAction
import io.ejekta.makkit.common.ext.*
import io.ejekta.makkit.common.network.pakkits.client.FocusRegionPacket
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.world.BlockView

class MirrorOperation(val flipCenter: Vec3d) : WorldOperation() {

    override fun getType() = Companion.Type.MIRROR

    override fun calculate(action: EditAction, view: BlockView) {

        println(action.box)

        for (block in action.box.getBlockArray()) {
            println(view.getBlockState(block))
            val vec = block.vec3d()

            val newPosUnconstrained = vec.subtract(vec.flipAround(flipCenter)).add(Vec3d(1.0, 1.0, 1.0))
            val newPos = vec.add(newPosUnconstrained.axisMask(action.direction).multiply(-1.0))

            action.edit(BlockPos(newPos), view.getBlockState(block).flippedOn(action.direction.axis))
        }


    }

}