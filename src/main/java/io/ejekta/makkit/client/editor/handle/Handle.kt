package io.ejekta.makkit.client.editor.handle

import io.ejekta.makkit.client.editor.EditRegion
import io.ejekta.makkit.client.render.AnimBox
import net.minecraft.util.math.Box
import net.minecraft.util.math.Direction

abstract class Handle(val region: EditRegion, val dirs: Set<Direction>) {

    val oppositeDirs: Set<Direction> by lazy {
        dirs.map { it.opposite }.toSet()
    }

    abstract val handleBox: Box

}