package io.ejekta.makkit.client.editor.drag.tools

import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.drag.DragTool
import io.ejekta.makkit.client.editor.handle.Handle

enum class MakkitTool(val producer: (handleCtx: EditRegion.HandleContext) -> DragTool) {
    MOVE_AXIAL(::MoveToolAxial),
    MOVE_PLANAR(::MoveToolPlanar)
}