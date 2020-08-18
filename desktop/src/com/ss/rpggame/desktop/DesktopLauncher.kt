package com.ss.rpggame.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.ss.rpggame.Base.BaseGame
import com.ss.rpggame.Base.BaseGame.Companion.HEIGHT
import com.ss.rpggame.Base.BaseGame.Companion.WIDTH

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration()
        config.apply {
            width = WIDTH
            height = HEIGHT
            title = "RPG Game"
        }
        LwjglApplication(BaseGame(), config)
    }
}