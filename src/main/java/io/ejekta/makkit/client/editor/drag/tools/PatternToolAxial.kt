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

internal class PatternToolAxial(
        region: EditRegion,
        binding: KeyStateHandler
) : SingleAxisDragTool(region, binding) {

    private var beforeBox: Box? = null

    override fun onStartDragging(start: BoxTraceResult) {
        super.onStartDragging(start)
    }

    override fun getPreviewBox(offset: Vec3d, box: Box): Box {
        return box.stretch(
                offset.axisMask(dragStart.dir)
        )
    }

    // Set selection to original size, not the preview size
    override fun getSelectionBox(offset: Vec3d, oldSelection: Box, preview: Box): Box {
        return oldSelection
    }

    override fun onStopDragging(stop: BoxTraceResult) {
        val chosen = updateState(updateSelection = false)

        println("Chosen is ${chosen?.getSize()}, region is ${region.selection.getSize()}")
        if (chosen != null) {
            println("Doing operation repeat")
            region.doOperation(RepeatOperation(region.selection, chosen), chosen, region.selection)
        }
    }

    override fun onDrawPreview(offset: Vec3d) {
        super.onDrawPreview(offset)
        preview.drawSizeOnFace(dragStart.dir)

        // If any of the size dimensions are 0, this will crash
        if (region.selection.getSize().hasZeroAxis()) {
            return
        }

        val origBox = region.selection

        val pos = origBox.getStart()
        val size = origBox.getSize()

        // Should be the number of times to tile in a given direction
        val tileVector = Vec3d(
                preview.size.x / size.x,
                preview.size.y / size.y,
                preview.size.z / size.z
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
