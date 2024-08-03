package io.ejekta.makkit.client.editor.handle

import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.client.data.BoxTraceResult
import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.common.ext.drawAxisSizes
import io.ejekta.makkit.common.ext.drawFace
import io.ejekta.makkit.common.ext.getFacePlane
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction

class FaceHandle(region: EditRegion, private val faceDir: Direction) : Handle(region, setOf(faceDir)) {
    override val handleBox: Box
        get() = region.selection.getFacePlane(faceDir)

    override val renderBox: Box
        get() = region.selectionRenderer.renderBox

    override fun renderHover() {
        renderBox.drawFace(faceDir, MakkitClient.selectionFaceColor.toAlpha(.3f))
        renderBox.drawAxisSizes()
    }

}