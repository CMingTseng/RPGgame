package com.ss.rpggame.DragAndDrop

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.ss.rpggame.Util.Utility

class DropItem(
        x: Float,
        y: Float,
        stage: Stage,
        private var filterItemType: ItemUseType = ItemUseType.ARMOR_CHEST,
        private var customBackgroundDecal: TextureAtlas.AtlasRegion = Utility.ITEMS_TEXTURE_ATLAS.findRegion("empty")
) : DragAndDrop(x, y, stage) {

    private val TAG = DropItem::class.java.simpleName

    enum class ItemAttribute(val attribute: Int) {
        CONSUMABLE(1),
        EQUIPPABLE(2),
        STACK(4);
    }

    enum class ItemUseType(val itemUseType:Int){
        ITEM_RESTORE_HEALTH(1),
        ITEM_RESTORE_MP(2),
        ITEM_DAMAGE(4),
        WEAPON_ONE_HAND(8),
        WEAPON_TWO_HAND(16),
        WAND_ONE_HAND(32),
        WAND_TWO_HAND(64),
        ARMOR_SHIELD(128),
        ARMOR_HELMET(256),
        ARMOR_CHEST(512),
        ARMOR_FEET(1024);
    }

    enum class ItemTypeID {
        ARMOR01, ARMOR02, ARMOR03, ARMOR04, ARMOR05,
        BOOTS01, BOOTS02, BOOTS03, BOOTS04, BOOTS05,
        HELMET01, HELMET02, HELMET03, HELMET04, HELMET05,
        SHIELD01, SHIELD02, SHIELD03, SHIELD04, SHIELD05,
        WANDS01, WANDS02, WANDS03, WANDS04, WANDS05,
        WEAPON01, WEAPON02, WEAPON03, WEAPON04, WEAPON05,
        POTIONS01, POTIONS02, POTIONS03,
        SCROLL01, SCROLL02, SCROLL03
    }

    private var itemAttributes : Int = 0
    var itemUseType : Int = 0
    var itemTypeID : ItemTypeID? = null
    var itemShortDescription = ""
    var itemValue : Int = 0
    var row = 0
    var col = 0
    var dropArea: DropArea? = null

    init {
        ItemUseType.values().forEach {
            if (filterItemType.itemUseType == it.itemUseType) {
                itemAttributes = it. itemUseType

            }
        }
        loadAtlasRegion(customBackgroundDecal)
        setSize(width, height)
        setBoundaryRectangle()

    }

    private fun clearPuzzleArea() {
        dropArea = null
    }

    private fun hasDropSlotArea(): Boolean = dropArea != null


    // override method from DragAndDropActor class

    override fun onDragStart() {
        if (hasDropSlotArea()) {
            val slotArea = dropArea?:return
            slotArea.targetable = true
            clearPuzzleArea()
        }
    }



    // override method from DragAndDropActor class
    override fun onDrop() {
        if (dropTarget!=null) {
            val slotArea = dropTarget as DropArea
            moveToActor(slotArea)
            Gdx.app.log(TAG,"Move to Slot")
        }else{
            // avoid blocking view of pile when incorrect
            moveToStart()
            Gdx.app.log(TAG,"Move to Start")
        }
    }


    override fun act(delta: Float) {
        super.act(delta)
    }
}