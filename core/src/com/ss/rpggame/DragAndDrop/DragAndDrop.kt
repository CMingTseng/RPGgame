package com.ss.rpggame.DragAndDrop

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.ss.rpggame.Base.BaseActor
import com.badlogic.gdx.utils.Array as GdxArray



abstract class DragAndDrop(x:Float, y:Float, stage: Stage) : BaseActor(x, y, stage) {
    companion object{
        private val TAG = DragAndDrop::class.java.simpleName
        val dropArray = GdxArray<DropArea>()
    }
    //val self = this
    private var grabOffsetX = 0f
    private var grabOffsetY = 0f
    private var startPositionX = 0f
    private var startPositionY = 0f
    var dropTarget: DropTarget? = null
    var draggable = true
    val duration = 0.25f

    private lateinit var listener: ClickListener

    init {
        listener = object : ClickListener() {
            override fun touchDown(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                if (!this@DragAndDrop.draggable) return false
                toFront()
                grabOffsetX = x
                grabOffsetY = y
                startPositionX = this@DragAndDrop.x
                startPositionY = this@DragAndDrop.y
                Actions.scaleTo(1.5f, 1.5f, duration)

                onDragStart()
                return true
            }

            override fun touchDragged(event: InputEvent, x: Float, y: Float, pointer: Int) {

                val deltaX = x - grabOffsetX
                val deltaY = y - grabOffsetY

                /*
                if (rotation != 0f) {
                    deltaX = Gdx.input.x.toFloat() - width / 2
                    deltaY = Gdx.graphics.height.toFloat() - Gdx.input.y.toFloat() - height / 2
                    addAction(Actions.moveTo(deltaX, deltaY, 0.1f, Interpolation.pow3))
                } else moveBy(deltaX, deltaY)

                 */

                moveBy(deltaX, deltaY)
            }

            override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {

                this@DragAndDrop.dropTarget= null
                // keep track of distance to closest object
                var closetDistance = Float.MAX_VALUE
                for (dropArea in dropArray) {
                    Gdx.app.log(TAG,"name:${dropArea.customBackgroundDecal} \t dropTarget:${Vector2(dropArea.x,dropArea.y)}\t")
                    //val target = dropTarget as DropTarget
                    if (dropArea.targetable && this@DragAndDrop.overlaps(dropArea)) {
                        val currentDistance = Vector2.dst(this@DragAndDrop.x, this@DragAndDrop.y, dropArea.x, dropArea.y)
                        // check if this target is even closer
                        if (currentDistance < closetDistance) {
                            this@DragAndDrop.dropTarget = dropArea
                            closetDistance = currentDistance
                        }
                    }
                }
                addAction(Actions.scaleTo(1.0f, 1.0f, duration))
                val area = this@DragAndDrop.dropTarget ?:return
                area as DropArea
                Gdx.app.log(TAG,"dropTarget:${area.customBackgroundDecal}\t")
                onDrop()
            }

        }

        addListener(listener)

    }


    fun moveToActor(other : BaseActor){
        val duration = 0.5f
        val x = other.x + (other.width - this.width) /2
        val y = other.y + (other.height - this.height) /2
        addAction(Actions.moveTo(x,y,duration, Interpolation.pow3))
    }

    fun moveToStart(){
        val duration = 0.5f
        addAction(Actions.moveTo(startPositionX,startPositionY,duration, Interpolation.pow3))
    }

    open fun onDragStart(){}

    open fun onDrop(){}

    override fun act(delta: Float) {
        super.act(delta)
        setPosition(x,y)

    }



}