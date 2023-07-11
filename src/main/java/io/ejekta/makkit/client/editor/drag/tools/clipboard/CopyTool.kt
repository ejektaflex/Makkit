package io.ejekta.makkit.client.editor.drag.tools.clipboard

import io.ejekta.kambrik.input.KambrikKeybind
import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.client.data.BoxTraceResult
import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.handle.Handle
import io.ejekta.makkit.common.enums.ClipboardMode
import net.minecraft.util.math.BlockPos

internal class CopyTool(handle: Handle) : ClipboardTool(handle) {

    override val mode = ClipboardMode.COPY

    override fun onStartDragging(start: BoxTraceResult) {
        region.copyBox = region.selection.offset(BlockPos.ORIGIN) // dumb copy
        super.onStartDragging(start)
    }

}