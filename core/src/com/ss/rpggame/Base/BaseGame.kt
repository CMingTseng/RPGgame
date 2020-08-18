package com.ss.rpggame.Base

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.ss.rpggame.Screen.GameScreen
import com.ss.rpggame.Screen.LoadGameScreen
import com.ss.rpggame.Screen.MenuScreen
import com.ss.rpggame.Screen.NewGameScreen
import ktx.app.KtxGame

open class BaseGame: KtxGame<Screen>() {
    private lateinit var menuScreen : MenuScreen
    private lateinit var gameScreen : GameScreen
    private lateinit var loadGameScreen : LoadGameScreen
    private lateinit var newGameScreen : NewGameScreen

    enum class ScreenType{
        MainMenu,MainGame,LoadGame,NewGame
    }

    companion object{
        var WIDTH = 800
        var HEIGHT = 600
    }


    fun getScreenType(screenType: ScreenType) : Screen {
        return when(screenType){
            ScreenType.MainMenu -> menuScreen
            ScreenType.MainGame -> gameScreen
            ScreenType.LoadGame -> loadGameScreen
            ScreenType.NewGame -> newGameScreen
        }

    }

    override fun create() {

        // prepare for multiple classes/stages to receive discrete input
        val inputMultiple = InputMultiplexer()
        Gdx.input.inputProcessor = inputMultiple

        // set initScreen

        menuScreen = MenuScreen(this)
        gameScreen = GameScreen(this)
        loadGameScreen = LoadGameScreen(this)
        newGameScreen = NewGameScreen(this)
        addScreen(menuScreen)
        addScreen(gameScreen)
        addScreen(loadGameScreen)
        addScreen(newGameScreen)
        setScreen(menuScreen::class.java)

    }

    override fun dispose() {
        menuScreen.dispose()
        gameScreen.dispose()
        loadGameScreen.dispose()
        newGameScreen.dispose()
    }
}