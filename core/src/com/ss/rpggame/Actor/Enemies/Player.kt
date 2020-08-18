package com.ss.rpggame.Actor.Enemies

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Stage

import com.ss.rpggame.Base.BaseActor
import java.util.*


class Player(x:Float, y:Float, stage: Stage) : BaseActor(x, y, stage) {
    //private val texturePath = "sprites/characters/Warrior.png"
    private val jsonPath = "scripts/player.json"
    private val json = getEntitiesConfigFromJsonPath(jsonPath)
    private var table : Hashtable<BaseActor,Hashtable<AnimationType, Animation<TextureRegion>>> = Hashtable()
    private lateinit var north : Animation<TextureRegion>
    private lateinit var south : Animation<TextureRegion>
    private lateinit var east : Animation<TextureRegion>
    private lateinit var west : Animation<TextureRegion>
    var facingAngle = 0f

    init {

        /*
        val rows = 4
        val cols = 4
        val texture = Texture(Gdx.files.internal(texturePath),true)
        val frameWidth = texture.width / cols
        val frameHeight = texture.height / rows
        val frameDuration = 0.2f
        val temp = TextureRegion.split(texture,frameWidth,frameHeight)

        val textureArray = GdxArray<TextureRegion>()
        for (c in 0 until cols) textureArray.add(temp[0][c])
        south = Animation(frameDuration,textureArray,Animation.PlayMode.LOOP_PINGPONG)

        textureArray.clear()
        for (c in 0 until cols) textureArray.add(temp[1][c])
        west = Animation(frameDuration,textureArray,Animation.PlayMode.LOOP_PINGPONG)

        textureArray.clear()
        for (c in 0 until cols) textureArray.add(temp[2][c])
        east = Animation(frameDuration,textureArray,Animation.PlayMode.LOOP_PINGPONG)

        textureArray.clear()
        for (c in 0 until cols) textureArray.add(temp[3][c])
        north = Animation(frameDuration,textureArray,Animation.PlayMode.LOOP_PINGPONG)

         */
        table[this] = loadAnimationFromJson(this,json)
        for (player in table){
            val hashtable = player.value
            south = hashtable[AnimationType.WALK_DOWN]!!
            north = hashtable[AnimationType.WALK_UP]!!
            west = hashtable[AnimationType.WALK_LEFT]!!
            east = hashtable[AnimationType.WALK_RIGHT]!!

        }

        setAnimation(south)
        facingAngle = 270f


        setBoundaryPolygon(8)
        setAcceleration(400f)
        setMaxSpeed(100f)
        setDeceleration(400f)

    }

    override fun act(delta: Float) {
        super.act(delta)
        boundToWorld()
        alignCamera()
        animationUpdate()
        applyPhysics(delta)

        // controller direction
        controller()

    }

    private fun controller(){
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            accelerateAtAngle(180f)

        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            accelerateAtAngle(0f)

        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            accelerateAtAngle(90f)

        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            accelerateAtAngle(270f)

        }
    }

    private fun animationUpdate(){
        // pause animation when character not moving
        if (getSpeed() == 0f) setAnimationPaused(true)
        else{
            setAnimationPaused(false)
            // set direction animation
            when (getMotionAngle()) {
                in 45f..135f -> {
                    facingAngle = 90f
                    setAnimation(north)
                }
                in 135f..225f -> {
                    facingAngle = 180f
                    setAnimation(west)
                }
                in 225f..315f -> {
                    facingAngle = 270f
                    setAnimation(south)
                }
                else -> {
                    facingAngle = 0f
                    setAnimation(east)
                }

            }

        }

    }


}