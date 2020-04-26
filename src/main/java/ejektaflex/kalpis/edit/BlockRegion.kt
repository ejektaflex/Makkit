package ejektaflex.kalpis.edit

import ejektaflex.kalpis.ext.BoxTraceResult
import ejektaflex.kalpis.ext.plus
import ejektaflex.kalpis.render.RenderBox
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d

class BlockRegion {
    var pos: BlockPos = BlockPos(0, 0, 0)
        set(value) {
            field = value
            updateAreaPositions()
        }

    var size: BlockPos = BlockPos(1, 1, 1)
        set(value) {
            field = value
            updateAreaPositions()
        }

    val end: BlockPos
        get() = pos + size

    fun fitTo(renderBox: RenderBox) {
        pos = BlockPos(renderBox.pos)
        size = BlockPos(renderBox.pos)
    }

    private var areaPositions: List<BlockPos> = calcAreaPositions()

    private fun updateAreaPositions() {
        areaPositions = calcAreaPositions()
    }

    fun closestBlock(pos: Vec3d): BlockPos? {
        return areaPositions.minBy { it.getSquaredDistance(pos, true) }
    }

    fun closestOutsidePos(result: BoxTraceResult): BlockPos {
        val dirVec = result.dir.unitVector
        val dirD = Vec3d(dirVec.x.toDouble(), dirVec.y.toDouble(), dirVec.z.toDouble())
        return BlockPos(result.hit + dirD.multiply(0.5))
    }

    private fun calcAreaPositions(): List<BlockPos> {
        val buff = mutableListOf<BlockPos>()
        val endPos = end
        for (dx in pos.x until endPos.x) {
            for (dy in pos.y until endPos.y) {
                for (dz in pos.z until endPos.z) {
                    buff.add(BlockPos(dx, dy, dz))
                }
            }
        }
        return buff
    }
}