package com.ss.rpggame.Actor.Items

import com.badlogic.gdx.scenes.scene2d.Stage
import com.ss.rpggame.Base.BaseActor

class Arrow(x:Float,y:Float,stage: Stage) : BaseActor(x, y, stage) {
    init {
        //loadTexture("arrow.png")
        setSpeed(400f)
    }

    override fun act(delta: Float) {
        super.act(delta)
        applyPhysics(delta)
    }


}