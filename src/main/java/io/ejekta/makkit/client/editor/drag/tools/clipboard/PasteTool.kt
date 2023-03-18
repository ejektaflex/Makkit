package io.ejekta.makkit.client.editor.drag.tools.clipboard

import io.ejekta.kambrik.input.KambrikKeybind
import io.ejekta.makkit.client.MakkitClient
import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.common.enums.ClipboardMode

internal class PasteTool(region: EditRegion) : ClipboardTool(region) {

    override val kambrikKeybind: KambrikKeybind
        get() = MakkitClient.config.pasteKey

    override val mode = ClipboardMode.PASTE

}