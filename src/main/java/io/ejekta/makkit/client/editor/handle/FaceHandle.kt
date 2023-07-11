package io.ejekta.makkit.client.editor.handle

import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.common.ext.getFacePlane
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction

class FaceHandle(region: EditRegion, private val faceDir: Direction) : Handle(region, setOf(faceDir)) {
    override val handleBox: Box
        get() = region.selection.getFacePlane(faceDir)

}