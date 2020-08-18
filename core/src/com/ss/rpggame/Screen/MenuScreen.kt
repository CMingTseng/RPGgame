package com.ss.rpggame.Screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.ss.rpggame.Base.BaseActor
import com.ss.rpggame.Base.BaseGame
import com.ss.rpggame.Base.BaseGame.Companion.HEIGHT
import com.ss.rpggame.Base.BaseGame.Companion.WIDTH
import com.ss.rpggame.Base.BaseScreen
import com.ss.rpggame.Util.Utility

class MenuScreen(private val game: BaseGame) : BaseScreen() {

    lateinit var newGameButton : TextButton
    lateinit var loadGameButton : TextButton
    lateinit var exitButton : TextButton

    override fun initialize() {


        BaseActor(0f, 0f, mainStage).apply {
            loadTexture("background.png")
            setSize(WIDTH.toFloat(),HEIGHT.toFloat())
        }

        val title = Image(Utility.STATUS_UI_TEXTURE_ATLAS.findRegion("bludbourne_title"))
        newGameButton = TextButton("New Game", Utility.STATUS_UI_SKIN,"Menu")
        loadGameButton = TextButton("Load Game", Utility.STATUS_UI_SKIN,"Menu")
        exitButton = TextButton("Exit", Utility.STATUS_UI_SKIN,"Menu")

        // Listeners
        newGameButton.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                game.setScreen(game.getScreenType(BaseGame.ScreenType.NewGame)::class.java)
                return true
            }
        })

        loadGameButton.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                game.setScreen(game.getScreenType(BaseGame.ScreenType.LoadGame)::class.java)
                return true
            }
        })

        exitButton.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                Gdx.app.exit()
                return true
            }
        })

        uiTable.apply {
            add(title).spaceBottom(75f).row()
            add(newGameButton).spaceBottom(10f).row()
            add(loadGameButton).spaceBottom(10f).row()
            add(exitButton).spaceBottom(10f).row()
        }

    }


    override fun update(delta: Float) {

    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Input.Keys.ESCAPE){
            Gdx.app.exit()
            dispose()
        }

        if (keycode == Input.Keys.ENTER){
            game.setScreen(game.getScreenType(BaseGame.ScreenType.LoadGame)::class.java)
            dispose()
        }
        return true
    }
}