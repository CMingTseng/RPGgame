package com.ss.rpggame.UI

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.ss.rpggame.Actor.Enemies.NPC
import com.ss.rpggame.Base.BaseActor
import com.ss.rpggame.Data.ConversationConfig
import com.ss.rpggame.DragAndDrop.DropArea
import com.ss.rpggame.DragAndDrop.DropItem
import com.ss.rpggame.Profile.ProfileManager
import com.ss.rpggame.Profile.ProfileObserver.ProfileEvent.*
import com.ss.rpggame.Profile.ProfileObserver.ProfileEvent
import com.ss.rpggame.Profile.ProfileObserver
import com.ss.rpggame.Util.Utility
import java.awt.SystemColor.window

class PlayerHUD(x:Float, y:Float,mainStage: Stage,uiStage: Stage) : BaseActor(x,y,uiStage),ProfileObserver {
    private val TAG = PlayerHUD::class.java.simpleName
    private val statusUI : StatusUI = StatusUI(0f,0f,uiStage)
    private val inventoryUI : InventoryUI = InventoryUI(0f,0f,uiStage)
    private val conversationUI = ConversationUI(0f, 0f, stage)

    init {


        statusUI.apply {
            window.isVisible = true
            setPosition(0f, 0f)
        }

        inventoryUI.apply {
            window.isMovable = false
            window.isVisible = false
            setPosition(uiStage.width, 0f)
        }

        conversationUI.apply{
            window.isVisible = false
            setPosition(uiStage.width, 0f)
        }

        uiStage.apply {
            addActor(statusUI)
            addActor(inventoryUI)
            addActor(conversationUI)
        }





        // Button listener
        statusUI.inventoryButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (event !is InputEvent && (event as InputEvent).type == InputEvent.Type.touchDown) return
                inventoryUI.window.isVisible = !inventoryUI.window.isVisible
            }
        })

        conversationUI.closeButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                if (event !is InputEvent && (event as InputEvent).type == InputEvent.Type.touchDown) return
                NPC.showDialog = false
                //NPC.wasSelect = false
            }
        })



    }

    private fun dialogUpdate(delta: Float){
        conversationUI.window.isVisible = NPC.showDialog
        if (NPC.showDialog) {
            conversationUI.loadConversation(NPC.path)
        } else {
            conversationUI.clear()
        }
        when(conversationUI.choice){
            ConversationConfig.ConversationCommandEvent.LOAD_STORE_INVENTORY ->{
                //Gdx.app.log(TAG,"ConversationChoice : Load store ")
                NPC.showDialog = false
            }
            ConversationConfig.ConversationCommandEvent.EXIT_CONVERSATION->{
                //Gdx.app.log(TAG,"ConversationChoice : Exit ")
                NPC.showDialog = false
            }
            ConversationConfig.ConversationCommandEvent.NONE->{
                //Gdx.app.log(TAG,"ConversationChoice : None ")

            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onNotify(profileManager: ProfileManager, event: ProfileEvent) {
        when(event){
            PROFILE_LOADED -> {

            }

            SAVING_PROFILE -> {

            }
        }
    }

    override fun act(delta: Float) {
        dialogUpdate(delta)

        //inventoryUpdate(delta)
    }



}