package com.ss.rpggame.Actor.Graphics

import com.badlogic.gdx.scenes.scene2d.Stage
import com.ss.rpggame.Base.BaseActor

open class Solid(x:Float, y:Float, width:Float, height:Float, stage: Stage) : BaseActor(x, y, stage) {
    init {
        setSize(width, height)
        setBoundaryRectangle()
    }


}
