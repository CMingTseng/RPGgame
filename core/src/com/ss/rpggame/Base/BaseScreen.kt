package com.ss.rpggame.Base

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Event
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.StretchViewport
import com.ss.rpggame.Base.BaseGame.Companion.HEIGHT
import com.ss.rpggame.Base.BaseGame.Companion.WIDTH
import ktx.app.KtxScreen


abstract class BaseScreen: KtxScreen, InputProcessor {
    protected var mainStage : Stage = Stage(StretchViewport(WIDTH.toFloat(), HEIGHT.toFloat()))
    protected var uiStage : Stage = Stage(StretchViewport(WIDTH.toFloat(), HEIGHT.toFloat()))
    protected var uiTable : Table = Table()

    private val inputMultiple = Gdx.input.inputProcessor as InputMultiplexer

    init{

        uiTable.setFillParent(true)
        uiStage.addActor(uiTable)
        initialize()
    }


    abstract fun initialize()

    abstract fun update(delta:Float)

    fun isTouchDownEvent(event: Event) = (event is InputEvent) && (event as InputEvent).type == InputEvent.Type.touchDown

    override fun show() {
        inputMultiple.addProcessor(this)
        inputMultiple.addProcessor(mainStage)
        inputMultiple.addProcessor(uiStage)
    }

    override fun render(delta: Float){
        uiStage.act(delta)
        mainStage.act(delta)

        update(delta)


        Gdx.gl.glClearColor(0f,0f,0f,1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        mainStage.draw()
        uiStage.draw()

    }

    // methods required by Screen interface

    override fun hide() {
        inputMultiple.removeProcessor(this)
        inputMultiple.removeProcessor(mainStage)
        inputMultiple.removeProcessor(uiStage)
        //mainStage!!.dispose()
        //uiStage!!.dispose()
    }
    override fun pause() {}
    override fun resume() {}
    override fun resize(width: Int, height: Int) {
        mainStage.viewport.update(width,height)
        uiStage.viewport.update(width,height)

    }
    override fun dispose() {

    }
    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        return false
    }

    override fun keyTyped(character: Char): Boolean {
        return false
    }

    override fun scrolled(amount: Int): Boolean {
        return false
    }

    override fun keyUp(keycode: Int): Boolean {
        return false
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return false
    }

    override fun keyDown(keycode: Int): Boolean {
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        return false
    }

}