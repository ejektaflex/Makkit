package io.ejekta.makkit.client.editor.drag.tools

import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.drag.DragTool
import io.ejekta.makkit.client.editor.drag.tools.clipboard.CopyTool
import io.ejekta.makkit.client.editor.drag.tools.clipboard.PasteTool

enum class MakkitTool(val producer: (handleCtx: EditRegion.HandleContext) -> DragTool) {
    MOVE_AXIAL(::MoveToolAxial),
    MOVE_PLANAR(::MoveToolPlanar),
    RESIZE_AXIAL(::ResizeToolAxial),
    RESIZE_SYMMETRIC(::ResizeToolSymmetric),
    MIRROR(::MirrorToolAxial),
    PATTERN(::PatternToolAxial),
    COPY(::CopyTool),
    PASTE(::PasteTool)
}