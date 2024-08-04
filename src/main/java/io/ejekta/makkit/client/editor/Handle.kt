package io.ejekta.makkit.client.editor

import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.common.ext.drawAxisSizes
import io.ejekta.makkit.common.ext.drawFace
import io.ejekta.makkit.common.ext.getFacePlane
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction

class Handle(val region: EditRegion, val faceDir: Direction) {
    val handleBox: Box
        get() = region.selection.getFacePlane(faceDir)

    val renderBox: Box
        get() = region.selectionRenderer.renderBox

    fun renderHover() {
        renderBox.drawFace(faceDir, MakkitClient.selectionFaceColor.toAlpha(.3f))
        renderBox.drawAxisSizes()
    }
}