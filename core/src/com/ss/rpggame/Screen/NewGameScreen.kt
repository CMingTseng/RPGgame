package com.ss.rpggame.Screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.InputListener
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.ss.rpggame.Util.Utility
import com.ss.rpggame.Base.BaseGame
import com.ss.rpggame.Base.BaseScreen
import com.ss.rpggame.Profile.ProfileManager

class NewGameScreen (private val game: BaseGame) : BaseScreen()  {
    override fun initialize() {
        val profileName = Label("Enter Profile Name:", Utility.STATUS_UI_SKIN)
        // InputText
        val profileText = TextField("", Utility.STATUS_UI_SKIN,"inventory")
        profileText.maxLength = 20

        val overwriteDialog = Dialog("Overwrite?", Utility.STATUS_UI_SKIN, "solidbackground")
        val overwriteLabel = Label("Overwrite existing profile name?", Utility.STATUS_UI_SKIN)
        val cancelButton = TextButton("Cancel", Utility.STATUS_UI_SKIN, "inventory")

        val overwriteButton = TextButton("Overwrite", Utility.STATUS_UI_SKIN, "inventory")
        overwriteDialog.setKeepWithinStage(true)
        overwriteDialog.isModal = true
        overwriteDialog.isMovable = false
        overwriteDialog.text(overwriteLabel)

        val startButton = TextButton("Start", Utility.STATUS_UI_SKIN)
        val backButton = TextButton("Back", Utility.STATUS_UI_SKIN)

        //Dialog Layout
        overwriteDialog.row()
        overwriteDialog.button(overwriteButton).bottom().left()
        overwriteDialog.button(cancelButton).bottom().right()

        val topTable = Table()
        topTable.setFillParent(true)
        topTable.add(profileName).center()
        topTable.add(profileText).center()

        val bottomTable = Table()
        bottomTable.height = startButton.height
        bottomTable.width = Gdx.graphics.width.toFloat()
        bottomTable.center()
        bottomTable.add<TextButton>(startButton).padRight(50f).padBottom(Gdx.graphics.height.toFloat()/2)
        bottomTable.add<TextButton>(backButton).padBottom(Gdx.graphics.height.toFloat()/2)

        var messageText = ""
        messageText = if (messageText.isBlank()) "default" else profileText.text

        // Listeners
        startButton.addListener(object : InputListener(){
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                // check to see if the current profile matches one that already exists
                var exists = false
                exists = ProfileManager.profileManager.doesProfileExist(messageText)

                if (exists){
                    // Pop up dialog for Overwrite
                    overwriteDialog.show(uiStage)
                }else {
                    ProfileManager.profileManager.writeProfileToStorage(messageText, "", false)
                    ProfileManager.profileManager.setCurrentProfile(messageText)
                    ProfileManager.profileManager.saveProfile()
                    ProfileManager.profileManager.loadProfile()

                    game.setScreen(game.getScreenType(BaseGame.ScreenType.MainGame)::class.java)
                }


                return true
            }
        })

        backButton.addListener(object : InputListener(){
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                game.setScreen(game.getScreenType(BaseGame.ScreenType.MainMenu)::class.java)
                return true
            }
        })

        overwriteButton.addListener(object : InputListener(){
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                ProfileManager.profileManager.writeProfileToStorage(messageText,"",true)
                ProfileManager.profileManager.setCurrentProfile(messageText)
                ProfileManager.profileManager.saveProfile()
                ProfileManager.profileManager.loadProfile()
                game.setScreen(game.getScreenType(BaseGame.ScreenType.MainGame)::class.java)
                return true
            }
        })

        cancelButton.addListener(object : InputListener(){
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                overwriteDialog.hide()
                return true
            }
        })

        uiTable.apply {
            addActor(topTable)
            addActor(bottomTable)
        }

    }

    override fun update(delta: Float) {

    }

}
