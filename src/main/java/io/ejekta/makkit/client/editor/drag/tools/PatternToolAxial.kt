package io.ejekta.makkit.client.editor.drag.tools

import io.ejekta.makkit.client.data.BoxTraceResult
import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.drag.SingleAxisDragTool
import io.ejekta.makkit.client.editor.input.KeyStateHandler
import io.ejekta.makkit.client.render.RenderBox
import io.ejekta.makkit.common.ext.*
import io.ejekta.makkit.common.editor.operations.RepeatOperation
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import kotlin.math.max

internal class PatternToolAxial(
        region: EditRegion,
        binding: KeyStateHandler
) : SingleAxisDragTool(region, binding) {

    private var beforeBox: Box? = null

    override fun onStartDragging(start: BoxTraceResult) {
        super.onStartDragging(start)

        beforeBox = region.area.box
    }

    override fun calcSelectionBox(offset: Vec3d, box: Box): Box {
        return box.stretch(
                offset.axisMask(dragStart.dir)
        )
    }

    override fun onStopDragging(stop: BoxTraceResult) {
        super.onStopDragging(stop)
        beforeBox?.let {
            region.doOperation(RepeatOperation(it))
        }
    }

    override fun onDrawPreview(offset: Vec3d) {
        super.onDrawPreview(offset)
        preview.drawSizeOnFace(dragStart.dir)

        // If any of the size dimensions are 0, this will crash
        if (region.area.box.getSize().hasZeroAxis()) {
            return
        }

        val origBox = region.area.box

        val pos = origBox.getStart()
        val size = origBox.getSize()

        // Should be the number of times to tile in a given direction
        val tileVector = Vec3d(
                preview.size.x / max(size.x, 1.0),
                preview.size.y / max(size.y, 1.0),
                preview.size.z / max(size.z, 1.0)
        ).roundToVec3i()

        for (x in 0 until tileVector.x) {
            for (y in 0 until tileVector.y) {
                for (z in 0 until tileVector.z) {

                    val step = Vec3d(x.toDouble(), y.toDouble(), z.toDouble())

                    RenderBox().apply {
                        box = Box(
                                pos.add(size.multiply(step).dirMask(dragStart.dir)),
                                pos.add(size.multiply(step).dirMask(dragStart.dir)).add(size)
                        )
                    }.draw()

                }
            }
        }

    }

}
