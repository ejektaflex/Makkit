package io.ejekta.makkit.client.editor.drag.tools.clipboard

import io.ejekta.kambrik.input.KambrikKeybind
import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.handle.Handle
import io.ejekta.makkit.common.enums.ClipboardMode

internal class PasteTool(handle: Handle) : ClipboardTool(handle) {
    override val mode = ClipboardMode.PASTE
}