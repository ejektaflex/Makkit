package io.ejekta.makkit.client.editor.drag.tools.clipboard

import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.input.KeyStateHandler
import io.ejekta.makkit.common.enum.ClipboardMode

internal class CopyTool(region: EditRegion, binding: KeyStateHandler) : ClipboardTool(region, binding) {

    override val mode = ClipboardMode.COPY

}