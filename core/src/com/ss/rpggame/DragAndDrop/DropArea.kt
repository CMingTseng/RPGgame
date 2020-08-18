package com.ss.rpggame.DragAndDrop

import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align

import com.ss.rpggame.Util.Utility

class DropArea(
        x:Float,
        y:Float,
        stage: Stage,
        private var filterItemType: Int = 0,
        var customBackgroundDecal:TextureAtlas.AtlasRegion = Utility.ITEMS_TEXTURE_ATLAS.findRegion("empty")
): DropTarget(x,y,stage) {

    companion object{
        private val TAG = DropArea::class.java.simpleName
    }
    private val stack = Stack()
    private var _numItemsVal : Int = 0
    private val _numItemsLabel : Label = Label(_numItemsVal.toString(), Utility.STATUS_UI_SKIN,"inventory-item-count")
    val image = loadAtlasRegion(customBackgroundDecal)
    private val boarder = Image(NinePatch(Utility.STATUS_UI_TEXTURE_ATLAS.createPatch("dialog")))

    init {
        _numItemsLabel.apply {
            setFillParent(true)
            setAlignment(Align.bottomRight)
            setFontScale(1.5f)
            isVisible = true
        }

        stack.apply {
            setFillParent(true)
            add(boarder)
            add(_numItemsLabel)
        }
        stack.toBack()
        addActor(stack)
        setSize(width, height)
        setBoundaryRectangle()
        DragAndDrop.dropArray.add(this)
    }


    override fun act(delta: Float) {
        super.act(delta)
        setPosition(x,y)

    }
}