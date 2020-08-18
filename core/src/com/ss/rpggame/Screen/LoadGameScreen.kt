package com.ss.rpggame.Screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.ss.rpggame.Base.BaseGame
import com.ss.rpggame.Base.BaseScreen
import com.ss.rpggame.Profile.ProfileManager
import com.ss.rpggame.Util.Utility

class LoadGameScreen (private val game: BaseGame) : BaseScreen()  {
    private lateinit var loadButton : TextButton
    private lateinit var backButton : TextButton
    private lateinit var listItems : List<String>

    override fun initialize() {

        ProfileManager.profileManager.storeAllProfiles()
        loadButton = TextButton("Load", Utility.STATUS_UI_SKIN)
        backButton = TextButton("Back", Utility.STATUS_UI_SKIN)
        val list = ProfileManager.profileManager.getProfileList()

        listItems  = List(Utility.STATUS_UI_SKIN, "inventory")
        listItems.setItems(list)

        val scrollPane = ScrollPane(listItems)
        scrollPane.apply {
            setOverscroll(false, false)
            fadeScrollBars = false
            setScrollingDisabled(true, false)
            setScrollbarsOnTop(true)
        }

        val table = Table()
        val bottomTable = Table()

        // Layout
        table.apply {
            center()
            setFillParent(true)
            padBottom(loadButton.height)
            add(scrollPane).center()
        }

        bottomTable.apply {
            height = loadButton.height
            width = Gdx.graphics.width.toFloat()
            center()
            add(loadButton).padRight(50f).padBottom(50f)
            add(backButton).padBottom(50f)
        }

        loadButton.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                loadGame()
                return true
            }
        })

        backButton.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                game.setScreen(game.getScreenType(BaseGame.ScreenType.MainMenu)::class.java)
                return true
            }
        })

        uiTable.apply {
            addActor(table)
            addActor(bottomTable)
        }

    }

    override fun update(delta: Float) {

    }

    fun loadGame(){
        val fileName = listItems.selected.toString()
        val file = ProfileManager.profileManager.getProfileFile(fileName)
        if (file != null) {
            ProfileManager.profileManager.setCurrentProfile(fileName)
            ProfileManager.profileManager.loadProfile()
            game.setScreen(game.getScreenType(BaseGame.ScreenType.MainGame)::class.java)
        }
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Input.Keys.ESCAPE){
            Gdx.app.exit()
            dispose()
        }

        if (keycode == Input.Keys.ENTER){
            loadGame()
            dispose()
        }

        return false
    }


}
