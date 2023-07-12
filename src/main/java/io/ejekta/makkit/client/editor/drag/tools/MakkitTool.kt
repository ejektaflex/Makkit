package io.ejekta.makkit.client.editor.drag.tools

import io.ejekta.makkit.client.editor.drag.DragTool
import io.ejekta.makkit.client.editor.handle.Handle

enum class MakkitTool(val producer: (handle: Handle) -> DragTool) {
    MOVE_AXIAL(::MoveToolAxial),
    MOVE_PLANAR(::MoveToolPlanar)
}