package io.ejekta.makkit.client.editor.drag.tools.clipboard

import io.ejekta.makkit.client.data.BoxTraceResult
import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.input.KeyStateHandler
import io.ejekta.makkit.common.enum.ClipboardMode
import net.minecraft.util.math.BlockPos

internal class CopyTool(region: EditRegion, binding: KeyStateHandler) : ClipboardTool(region, binding) {

    override val mode = ClipboardMode.COPY

    override fun onStartDragging(start: BoxTraceResult) {
        region.copyBox = region.selection.offset(BlockPos.ORIGIN) // dumb copy
        super.onStartDragging(start)
    }

}