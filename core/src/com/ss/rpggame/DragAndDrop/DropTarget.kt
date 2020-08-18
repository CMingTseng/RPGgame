package com.ss.rpggame.DragAndDrop

import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.ss.rpggame.Base.BaseActor

open class DropTarget(x:Float,y:Float,stage: Stage) : BaseActor(x, y, stage) {
    var targetable = true
}