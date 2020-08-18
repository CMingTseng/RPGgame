package com.ss.rpggame.UI

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Window

import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.ss.rpggame.Base.BaseActor
import com.ss.rpggame.Util.Utility

class StatusUI(x:Float,y:Float,stage: Stage) : BaseActor(x, y, stage) {
    val window = Window("stats", Utility.STATUS_UI_SKIN)

    // Groups
    private val group1 : WidgetGroup = WidgetGroup()
    private val group2 : WidgetGroup = WidgetGroup()
    private val group3 : WidgetGroup = WidgetGroup()

    // Attributes
    val levelVal = 1
    val goldVal = 0
    val hpVal = 50
    val mpVal = 50
    val xpVal = 0

    // Image
    private val hpBar : Image = Image(Utility.STATUS_UI_TEXTURE_ATLAS.findRegion("HP_Bar"))
    private val mpBar : Image = Image(Utility.STATUS_UI_TEXTURE_ATLAS.findRegion("MP_Bar"))
    private val xpBar : Image = Image(Utility.STATUS_UI_TEXTURE_ATLAS.findRegion("XP_Bar"))
    private val bar1 : Image = Image(Utility.STATUS_UI_TEXTURE_ATLAS.findRegion("Bar"))
    private val bar2 : Image = Image(Utility.STATUS_UI_TEXTURE_ATLAS.findRegion("Bar"))
    private val bar3 : Image = Image(Utility.STATUS_UI_TEXTURE_ATLAS.findRegion("Bar"))
    // Labels
    private val hpLabel = Label("hp:", Utility.STATUS_UI_SKIN)
    private val hp: Label = Label(hpVal.toString(), Utility.STATUS_UI_SKIN)
    private val mpLabel = Label("mp:", Utility.STATUS_UI_SKIN)
    private val mp: Label = Label(mpVal.toString(), Utility.STATUS_UI_SKIN)
    private val xpLabel = Label("xp:", Utility.STATUS_UI_SKIN)
    private val xp: Label = Label(xpVal.toString(), Utility.STATUS_UI_SKIN)
    private val levelLabel = Label("lv:", Utility.STATUS_UI_SKIN)
    private val level: Label = Label(levelVal.toString(), Utility.STATUS_UI_SKIN)
    private val goldLabel = Label("gp:", Utility.STATUS_UI_SKIN)
    private val gold: Label = Label(goldVal.toString(), Utility.STATUS_UI_SKIN)

    // Buttons
    val inventoryButton : ImageButton = ImageButton(Utility.STATUS_UI_SKIN,"inventory-button")

    init {

        inventoryButton.imageCell.size(32f,32f)
        // Align Images
        hpBar.setPosition(0f,0f)
        mpBar.setPosition(0f,0f)
        xpBar.setPosition(0f,0f)

        // add to widget groups
        group1.addActor(bar1)
        group1.addActor(hpBar)
        group2.addActor(bar2)
        group2.addActor(mpBar)
        group3.addActor(bar3)
        group3.addActor(xpBar)
        window.apply {
            // add to layout
            defaults().expand().fill()

            // account for the title padding

            pad( this.padTop + 25f,15f,15f,15f)
            add()
            add(inventoryButton).align(Align.topRight)
            row()

            // hp
            add(group1).size(bar1.width,bar1.height)
            add(hpLabel)
            add(hp).align(Align.left)
            row()

            // mp
            add(group2).size(bar2.width,bar2.height)
            add(mpLabel)
            add(mp).align(Align.left)
            row()

            // xp
            add(group3).size(bar3.width,bar3.height)
            add(xpLabel)
            add(xp).align(Align.left)
            row()

            // Level
            add(levelLabel).align(Align.left)
            add(level).align(Align.left)
            row()

            // Gold
            add(goldLabel).align(Align.left)
            add(gold).align(Align.left)

            // this.debug()
            pack()
        }
    }

    override fun act(delta: Float) {
        super.act(delta)
        stage.addActor(window)
        window.setPosition(x,y)
    }
}