package ejektaflex.makkit.client.editor.drag.tools.clipboard

import ejektaflex.makkit.client.data.BoxTraceResult
import ejektaflex.makkit.client.editor.EditRegion
import ejektaflex.makkit.client.editor.input.KeyStateHandler
import ejektaflex.makkit.common.enum.ClipboardMode
import ejektaflex.makkit.common.network.pakkits.server.ClipboardIntentPacket
import net.minecraft.util.math.Vec3d

internal class CopyTool(region: EditRegion, binding: KeyStateHandler) : ClipboardTool(region, binding) {

    override val mode = ClipboardMode.COPY

}