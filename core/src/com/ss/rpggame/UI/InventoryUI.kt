package com.ss.rpggame.UI

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.ss.rpggame.Base.BaseActor
import com.ss.rpggame.DragAndDrop.DragAndDrop
import com.ss.rpggame.DragAndDrop.DropArea
import com.ss.rpggame.DragAndDrop.DropItem
import com.ss.rpggame.Util.Utility

import com.ss.rpggame.Util.Utility.STATUS_UI_SKIN
import com.badlogic.gdx.utils.Array as GdxArray

class InventoryUI(x:Float,y:Float,stage: Stage) : BaseActor(x, y, stage) {

    companion object{
        private val TAG = InventoryUI::class.java.simpleName
        private const val slotWidth : Float = 52f
        private const val slotHeight : Float = 52f
    }

    private val numSlots : Int = 50
    private val rowSlots : Int = 5
    private val colSlots : Int = 10
    private val lengthSlotRow : Int = 10

    private val playerSlotsTable : Table = Table()
    private val inventorySlotsTable : Table = Table()
    private val equipSlotsTable : Table = Table()

    var window : Window = Window("Dialog", STATUS_UI_SKIN, "solidbackground")
    private var headSlot : DropArea = DropArea(450f, 0f, stage, DropItem.ItemUseType.ARMOR_HELMET.itemUseType, Utility.ITEMS_TEXTURE_ATLAS.findRegion("inv_helmet"))

    private var leftArmSlot : DropArea = DropArea(0f, 0f, stage, DropItem.ItemUseType.WEAPON_ONE_HAND.itemUseType or
                                        DropItem.ItemUseType.WEAPON_TWO_HAND.itemUseType or
                                        DropItem.ItemUseType.ARMOR_SHIELD.itemUseType or
                                        DropItem.ItemUseType.WAND_ONE_HAND.itemUseType or
                                        DropItem.ItemUseType.WAND_TWO_HAND.itemUseType, Utility.ITEMS_TEXTURE_ATLAS.findRegion("inv_weapon"))

    private var rightArmSlot : DropArea = DropArea(0f, 0f, stage, DropItem.ItemUseType.WEAPON_ONE_HAND.itemUseType or
                                        DropItem.ItemUseType.WEAPON_TWO_HAND.itemUseType or
                                        DropItem.ItemUseType.ARMOR_SHIELD.itemUseType or
                                        DropItem.ItemUseType.WAND_ONE_HAND.itemUseType or
                                        DropItem.ItemUseType.WAND_TWO_HAND.itemUseType, Utility.ITEMS_TEXTURE_ATLAS.findRegion("inv_shield"))

    private var chestSlot : DropArea = DropArea(0f, 0f, stage, DropItem.ItemUseType.ARMOR_CHEST.itemUseType, Utility.ITEMS_TEXTURE_ATLAS.findRegion("inv_chest"))

    private var legsSlot : DropArea = DropArea(0f, 0f, stage, DropItem.ItemUseType.ARMOR_FEET.itemUseType, Utility.ITEMS_TEXTURE_ATLAS.findRegion("inv_boot"))

    var item : DropItem = DropItem(450f, 100f, stage, DropItem.ItemUseType.ARMOR_HELMET, Utility.ITEMS_TEXTURE_ATLAS.findRegion("HELMET01"))

    init {


        inventorySlotsTable.name = "Inventory_Slot_Table"
        equipSlotsTable.name = "Equipment_Slot_Table"
        equipSlotsTable.defaults().space(10f)

        equipSlotsTable.apply{
            add()
            add(headSlot)
            row()
            add(leftArmSlot)
            add(chestSlot)
            add(rightArmSlot)
            row()
            add()
            right().add(legsSlot)
            pack()
            debugAll()
        }

        playerSlotsTable.background = Image(NinePatch(Utility.STATUS_UI_TEXTURE_ATLAS.createPatch("dialog"))).drawable
        playerSlotsTable.apply {
            add(equipSlotsTable)
            pack()
        }

        inventorySlotsTable.add(item)

        window.apply {
            add(playerSlotsTable)
            row()
            add(item)
            pack()
            debugAll()
        }

        stage.addActor(window)
    }

    override fun act(delta: Float) {
        super.act(delta)
        window.setPosition(x,y)
    }

}