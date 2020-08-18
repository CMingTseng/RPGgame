package com.ss.rpggame.Actor.Enemies

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.ss.rpggame.Base.BaseActor

class NPC(x:Float,y:Float,stage: Stage) : BaseActor(x, y, stage) {
    companion object{
        private val TAG = NPC::class.java.simpleName
        var path = ""
        var showDialog = false
        var ID = ""
        //var wasSelect = false
    }

    var npcID = ""
    var dialogPath = ""
    var select = false
    private lateinit var listener: ClickListener
    init {
        listener = object : ClickListener(){
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                select = true
                ID = npcID
                path = dialogPath
                return true
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int) {
                select = false
            }

        }
        addListener(listener)
    }


}