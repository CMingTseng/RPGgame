package com.ss.rpggame.Data

import com.badlogic.gdx.math.GridPoint2
import com.badlogic.gdx.utils.Array
import com.ss.rpggame.Base.BaseActor


class EntitiesConfig internal constructor() {
    var state = BaseActor.State.IDLE
    var entityID: String = ""
    var direction : String = ""
    var conversationConfigPath: String = ""
    var animationConfig: Array<AnimationConfig> = Array()
    fun addAnimationConfig(animationConfig: AnimationConfig) {
        this.animationConfig.add(animationConfig)
    }

    class AnimationConfig {
        var frameDuration = 1.0f
        var animationType: BaseActor.AnimationType = BaseActor.AnimationType.IDLE
        var texturePaths: Array<String> = Array()
        var gridPoints: Array<GridPoint2> = Array()
    }

}