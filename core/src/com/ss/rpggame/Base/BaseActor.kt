package com.ss.rpggame.Base

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.*
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Window
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.ss.rpggame.Data.ConversationConfig
import com.ss.rpggame.Data.EntitiesConfig
import com.ss.rpggame.Util.Utility
import ktx.json.fromJson
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.abs

open class BaseActor(x: Float, y: Float, stage: Stage) : Group() {
    private var animation : Animation<TextureRegion>? = null
    private var elapsedTime : Float = 0f
    private var animationPaused : Boolean = false
    var velocityVec : Vector2
    private var accelerationVec : Vector2
    private var acceleration :Float = 0f
    private var maxSpeed : Float = 1000f
    private var deceleration : Float = 0f
    private var boundaryPolygon: Polygon? = null

    enum class Direction{
        UP,DOWN,LEFT,RIGHT;
        companion object{
            fun getRandomNext(): Direction {
                return values()[MathUtils.random(values().size - 1)]
            }
        }
        fun getOpposite() : Direction? = when (this) {
            LEFT -> RIGHT
            RIGHT -> LEFT
            UP -> DOWN
            else -> UP
        }
    }

    enum class State{
        IDLE,WALKING, IMMOBILE; // This should always be last
        companion object{
            fun getRandomNext(): State = values()[MathUtils.random(values().size - 2)]
        }

    }

    enum class AnimationType{
        WALK_LEFT,WALK_RIGHT,WALK_UP,WALK_DOWN,IDLE,IMMOBILE;
        companion object{
            fun getRandomNext(): AnimationType {
                return values()[MathUtils.random(Direction.values().size - 1)]
            }
        }
    }

    init {
        setPosition(x,y)
        stage.addActor(this)
        velocityVec = Vector2(0f,0f)
        accelerationVec = Vector2(0f,0f)
    }

    companion object{
        private lateinit var worldBounds : Rectangle
        fun getList(stage: Stage, theClass : Class<*>) : MutableList<BaseActor> {
            val list: MutableList<BaseActor> = mutableListOf()
            for (a in stage.actors) {
                if (theClass.isInstance(a)) list.add(a as BaseActor)
            }
            return list
        }

        fun count(stage: Stage, theClass : Class<*>) = getList(stage, theClass).size

        fun setWorldBounds(width:Float,height:Float){
            worldBounds = Rectangle(0f,0f,width, height)
        }

        fun setWorldBounds(baseActor: BaseActor){
            setWorldBounds(baseActor.width, baseActor.height)
        }

        fun getWorldBounds(): Rectangle = worldBounds

        fun loadAnimationFromJson(baseActor: BaseActor, entitiesConfig: EntitiesConfig) : HashMap<String, Hashtable<AnimationType, Animation<TextureRegion>>>{
            val animationTable = Hashtable<AnimationType, Animation<TextureRegion>>()
            val hashMap : HashMap<String, Hashtable<AnimationType, Animation<TextureRegion>>> = HashMap()
            for (animationConfig in entitiesConfig.animationConfig) {
                val textureNames = animationConfig.texturePaths
                val points = animationConfig.gridPoints
                val frameDuration = animationConfig.frameDuration
                var animation: Animation<TextureRegion>? = null
                if (textureNames.size == 1) animation = baseActor.loadAnimationFromJson(textureNames[0], points, frameDuration)
                else if (textureNames.size == 2) animation = baseActor.loadAnimationFromJson(textureNames[0], textureNames[1], points, frameDuration)
            }
            return hashMap
        }

        fun getEntitiesConfigFromJsonPath(jsonPath:String) : EntitiesConfig = Json().fromJson(Gdx.files.internal(jsonPath))

        fun getEntitiesConfigJsonArrayFromJsonPath(jsonPath:String) : Array<EntitiesConfig> {
            val configs : Array<EntitiesConfig> = Array()
            val list =  Json().fromJson<Array<JsonValue>>( Gdx.files.internal(jsonPath))
            for (jsonVal in list) {
                configs.add(Json().readValue(EntitiesConfig::class.java,jsonVal))
            }
            return configs

        }

        fun getConversationFromJsonPath(jsonPath:String) : ConversationConfig = Json().fromJson(Gdx.files.internal(jsonPath))

        fun getConversationArrayFromJsonPath(jsonPath:String) : Array<ConversationConfig> {
            val configs : Array<ConversationConfig> = Array()
            val list =  Json().fromJson<Array<JsonValue>>( Gdx.files.internal(jsonPath))
            for (jsonVal in list) {
                configs.add(Json().readValue(ConversationConfig::class.java,jsonVal))
            }
            return configs

        }

    }

    fun bounceOff(other: BaseActor){
        val vector = preventOverlaps(other)!!
        if (abs(vector.x) > abs(vector.y)) velocityVec.x *=-1 else velocityVec.y *= -1 // horizontal bounce else vertical bounce
    }

    fun isWithinDistance(distance:Float,other: BaseActor) : Boolean{
        val polygon1 = this.getBoundaryPolygon()
        val scaleX = (this.width + 2*distance) / this.width
        val scaleY = (this.height + 2*distance) / this.height
        polygon1.setScale(scaleX,scaleY)
        val polygon2 = other.getBoundaryPolygon()
        // initial text to improve performance
        if (!polygon1.boundingRectangle.overlaps(polygon2.boundingRectangle)) return false
        return Intersector.overlapConvexPolygons(polygon1,polygon2)
    }

    fun alignCamera(){
        val cam = this.stage.camera
        val viewport = this.stage.viewport
        // center camera on actor
        cam.position.set(Vector3(this.x + this.originX,this.y + this.originY,0f))

        // bound camera to layout
        cam.position.x = MathUtils.clamp(cam.position.x,cam.viewportWidth/2, worldBounds.width - cam.viewportWidth/2)
        cam.position.y = MathUtils.clamp(cam.position.y,cam.viewportHeight/2, worldBounds.height - cam.viewportHeight/2)
        cam.update()
    }

    fun boundToWorld(){
        // check left edge
        if (x<0f) x = 0f

        // check right edge
        if (x + width > worldBounds.width) x = worldBounds.width - width

        // check bottom edge
        if (y<0f) y = 0f

        // check top edge
        if (y + height > worldBounds.height) y = worldBounds.height - height
    }

    private fun centerAtPosition(x:Float, y:Float){
        setPosition(x - width/2, y - height/2)
    }

    fun centerAtActor(other: BaseActor){
        centerAtPosition(other.x + other.width/2,other.y + other.height/2)
    }

    fun setOpacity(opacity:Float){
        color.a = opacity
    }

    fun setBoundaryRectangle(){
        val w = width
        val h = height
        val vertices = floatArrayOf(0f,0f,w,0f,w,h,0f,h)
        boundaryPolygon = Polygon(vertices)
    }

    fun setBoundaryPolygon(numSides:Int){
        val w = width
        val h = height
        val vertices = FloatArray(2 * numSides)
        for (i in 0 until numSides){
            val angle = i * 6.28f / numSides
            // ellipse
            // x-coordinate
            vertices[2*i] = w/2 * MathUtils.cos(angle) + w/2
            // y-coordinate
            vertices[2*i+1] = h/2 * MathUtils.sin(angle) + h/2
        }
        boundaryPolygon = Polygon(vertices)

    }

    private fun getBoundaryPolygon(): Polygon {
        boundaryPolygon!!.setPosition(x,y)
        boundaryPolygon!!.setOrigin(originX, originY)
        boundaryPolygon!!.rotation = rotation
        boundaryPolygon!!.setScale(scaleX, scaleY)
        return boundaryPolygon!!
    }

    fun overlaps(other: BaseActor):Boolean{
        val polygon1 = this.getBoundaryPolygon()
        val polygon2 = other.getBoundaryPolygon()
        // initial test to improve performance
        if (!polygon1.boundingRectangle.overlaps(polygon2.boundingRectangle)) return false
        return Intersector.overlapConvexPolygons(polygon1,polygon2)
    }

    fun preventOverlaps(other: BaseActor): Vector2? {
        val polygon1 = this.getBoundaryPolygon()
        val polygon2 = other.getBoundaryPolygon()
        // initial test to improve performance
        if (!polygon1.boundingRectangle.overlaps(polygon2.boundingRectangle)) return null
        val mtv  = Intersector.MinimumTranslationVector()
        val polygonOverlap = Intersector.overlapConvexPolygons(polygon1,polygon2,mtv)
        if (!polygonOverlap) return null
        this.moveBy(mtv.normal.x * mtv.depth ,mtv.normal.y * mtv.depth)
        return mtv.normal

    }

    // Speed function
    fun setSpeed(speed:Float){
        // if length is zero, then assume motion angle is zero degrees
        if (velocityVec.len() == 0f) velocityVec.set(speed,0f)
        else velocityVec.setLength(speed)
    }

    fun getSpeed():Float = velocityVec.len()

    fun setMotionAngle(angle:Float){
        velocityVec.setAngle(angle)
    }

    //getMotionAngle method is particularly convenient for making an object face the direction
    fun getMotionAngle():Float = velocityVec.angle()

    fun isMoving():Boolean = getSpeed()>0

    // Acceleration function
    fun setAcceleration(acc:Float){
        acceleration = acc
    }

    fun accelerateAtAngle(angle: Float){
        accelerationVec.add(Vector2(acceleration,0f).setAngle(angle))
    }

    fun accelerateForward(){
        accelerateAtAngle(rotation)
    }

    fun setMaxSpeed(ms:Float){
        maxSpeed = ms
    }

    fun setDeceleration(dec:Float){
        deceleration = dec
    }

    fun applyPhysics(delta: Float){
        // apply acceleration
        velocityVec.add(accelerationVec.x *delta, accelerationVec.y * delta)

        var speed : Float = getSpeed()

        //decrease speed(decelerate) when not accelerating
        if (accelerationVec.len() == 0f) speed -= deceleration * delta

        // keep speed within set bounds
        speed = MathUtils.clamp(speed,0f,maxSpeed)

        // update velocity
        setSpeed(speed)

        // apply velocity
        moveBy(velocityVec.x *delta, velocityVec.y *delta)

        //reset acceleration
        accelerationVec.set(0f,0f)
    }

    fun setAnimation(anim: Animation<TextureRegion>){
        animation = anim
        val textureRegion: TextureRegion = animation!!.getKeyFrame(0f)
        val width = textureRegion.regionWidth
        val height = textureRegion.regionHeight
        setSize(width.toFloat(),height.toFloat())
        setOrigin(width.toFloat()/2f,height.toFloat()/2f)
        if (boundaryPolygon == null) setBoundaryRectangle()
    }

    fun loadAtlasRegion(atlasRegion: TextureAtlas.AtlasRegion): Animation<TextureRegion> {
        val frameDuration = 0.1f
        val textureArray : Array<TextureRegion> = Array()
        textureArray.add(TextureRegion(atlasRegion))
        val anim : Animation<TextureRegion> = Animation(frameDuration,textureArray)
        anim.playMode = Animation.PlayMode.NORMAL
        if (animation == null) setAnimation(anim)
        return anim
    }

    fun loadAnimationFromFiles(fileNames: Array<String>, frameDuration:Float, loop:Boolean) : Animation<TextureRegion> {
        val fileCount = fileNames.size
        val textureArray : Array<TextureRegion> = Array()
        for (n in 0 until fileCount){
            val fileName = fileNames[n]
            Utility.loadTextureAsset(fileName)
            val texture = Utility.getTextureAsset(fileName)!!
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
            textureArray.add(TextureRegion(texture))
        }
        val anim : Animation<TextureRegion> = Animation(frameDuration,textureArray)
        if (loop) anim.playMode = Animation.PlayMode.LOOP
        else anim.playMode = Animation.PlayMode.NORMAL
        if (animation == null) setAnimation(anim)
        return anim
    }

    fun loadAnimationFromJson(baseActor: BaseActor, entitiesConfig: EntitiesConfig, cols: Int = 4, rows: Int = 4) : Hashtable<AnimationType, Animation<TextureRegion>> {
        val guardAnimations: Hashtable<AnimationType, Animation<TextureRegion>> = Hashtable()
        for (animationConfig in entitiesConfig.animationConfig) {
            val textureNames = animationConfig.texturePaths
            val points = animationConfig.gridPoints
            val animationType = animationConfig.animationType
            val frameDuration = animationConfig.frameDuration
            var animation: Animation<TextureRegion>? = null
            if (textureNames.size == 1) animation = baseActor.loadAnimationFromJson(textureNames[0], points, frameDuration)
            else if (textureNames.size == 2) animation = baseActor.loadAnimationFromJson(textureNames[0], textureNames[1], points, frameDuration,cols, rows)
            guardAnimations[animationType] = animation
        }
        return guardAnimations
    }
    fun loadAnimationFromJson(textureName:String, points: Array<GridPoint2>, frameDuration:Float, cols: Int = 4, rows: Int = 4) : Animation<TextureRegion> {
        Utility.loadTextureAsset(textureName)
        val texture  =  Utility.getTextureAsset(textureName)!!
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        val frameWidth = texture.width / cols
        val frameHeight = texture.height / rows
        val textureFrames = TextureRegion.split(texture, frameWidth, frameHeight)
        val animationKeyFrames = Array<TextureRegion>(points.size)
        for ( point in points) animationKeyFrames.add(textureFrames[point.x][point.y])
        val anim = Animation(frameDuration,animationKeyFrames, Animation.PlayMode.LOOP)
        if (animation == null) setAnimation(anim)
        return anim
    }

    fun loadAnimationFromJson(firstTexture:String, secondTexture:String, points: Array<GridPoint2>, frameDuration:Float, cols: Int = 4, rows: Int = 4) : Animation<TextureRegion> {
        Utility.loadTextureAsset(firstTexture)
        val texture1 = Utility.getTextureAsset(firstTexture)!!
        Utility.loadTextureAsset(secondTexture)
        val texture2 = Utility.getTextureAsset(secondTexture)!!

        val frameWidth = texture1.width / cols
        val frameHeight = texture1.height / rows
        val texture1Frames = TextureRegion.split(texture1,frameWidth , frameHeight)
        val texture2Frames = TextureRegion.split(texture2, frameWidth , frameHeight)
        val animationKeyFrames = Array<TextureRegion>(2)
        val point = points.first()
        animationKeyFrames.add(texture1Frames[point.x][point.y])
        animationKeyFrames.add(texture2Frames[point.x][point.y])
        val anim = Animation(frameDuration,animationKeyFrames, Animation.PlayMode.LOOP)
        if (animation == null) setAnimation(anim)
        return anim
    }

    fun loadAnimationFromSheet(fileName:String,rows:Int,cols:Int,frameDuration:Float,loop:Boolean) : Animation<TextureRegion> {
        Utility.loadTextureAsset(fileName)
        val texture = Utility.getTextureAsset(fileName)!!
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear)
        val frameWidth = texture.width / cols
        val frameHeight = texture.height / rows
        val temp = TextureRegion.split(texture,frameWidth,frameHeight)
        val textureArray = Array<TextureRegion>()
        for (row in 0 until rows)
            for (col in 0 until cols)
                textureArray.add(temp[row][col])

        val anim : Animation<TextureRegion> = Animation(frameDuration,textureArray)

        if (loop) anim.playMode = Animation.PlayMode.LOOP
        else anim.playMode = Animation.PlayMode.NORMAL
        if (animation == null) setAnimation(anim)

        return anim
    }

    fun loadTexture(fileName:String): Animation<TextureRegion> {
        val fileNames  = Array<String>(1)
        fileNames.add(fileName)
        return loadAnimationFromFiles(fileNames,1f,true)
    }

    fun isAnimationFinished() = animation!!.isAnimationFinished(elapsedTime)

    fun setAnimationPaused(pause:Boolean) {
        animationPaused = pause
    }

    override fun act(delta: Float) {
        super.act(delta)
        if (!animationPaused) elapsedTime += delta
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)

        // apply color tint effect
        batch.color = color
        if (animation != null && isVisible){
            batch.draw(animation!!.getKeyFrame(elapsedTime),x, y, originX, originY, width, height, scaleX, scaleY, rotation)
        }

    }

}