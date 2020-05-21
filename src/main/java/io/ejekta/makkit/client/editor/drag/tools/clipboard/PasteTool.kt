package io.ejekta.makkit.client.editor.drag.tools.clipboard

import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.input.KeyStateHandler
import io.ejekta.makkit.common.enums.ClipboardMode

internal class PasteTool(region: EditRegion) : ClipboardTool(region) {

    override val keyHandler: KeyStateHandler
        get() = MakkitClient.config.pasteKey

    override val mode = ClipboardMode.PASTE

}