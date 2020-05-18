package io.ejekta.makkit.client.editor.drag.tools.clipboard

import io.ejekta.makkit.client.config.MakkitConfig
import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.editor.input.KeyStateHandler
import io.ejekta.makkit.common.enums.ClipboardMode

internal class PasteTool(region: EditRegion) : ClipboardTool(region) {

    override val keyHandler: KeyStateHandler
        get() = MakkitConfig.Companion.Default.CHANGE_ME

    override val mode = ClipboardMode.PASTE

}