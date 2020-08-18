package com.ss.rpggame.Maps


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Json
import com.ss.rpggame.Actor.Graphics.Solid
import com.ss.rpggame.Actor.Enemies.NPC
import com.ss.rpggame.Actor.Graphics.Portal
import com.ss.rpggame.MapTools.TileMapActor
import com.ss.rpggame.Base.BaseActor
import com.ss.rpggame.Data.EntitiesConfig
import java.util.*
import com.badlogic.gdx.utils.Array as GdxArray

class TownMap internal constructor(stage: Stage,mapPath:String = "maps/town.tmx"): TileMapActor(mapPath,stage) {
    private val TAG = TownMap::class.java.simpleName
    private var _json : Json = Json()
    private val folkPath = "scripts/town_folk.json"
    private val guardPath = "scripts/town_guard_walking.json"
    private val npcGuard  = BaseActor.getEntitiesConfigFromJsonPath(guardPath)
    private val npcFolk:GdxArray<EntitiesConfig> = BaseActor.getEntitiesConfigJsonArrayFromJsonPath(folkPath)
    private var frameTime = 0f
    private var guardTable : Hashtable<BaseActor,Hashtable<BaseActor.AnimationType, Animation<TextureRegion>>> = Hashtable()
    private var npcMapObjectList : ArrayList<MapObject> = ArrayList()
    init {

        npcMapObjectList = getTileList("NPC")

        npcList()

        solidList()

        portalList()

        playerPosition()

    }

    override fun playerPosition(){
        var from = ""
        for( obj in getRectangleList("start")){
            val props = obj.properties
            from = props["From"] as String
            positionHashMap[from] = Vector2(props["x"] as Float,props["y"] as Float)
        }

    }

    override fun portalList() {
        for( obj in getRectangleList("Portal")){
            val props = obj.properties
            val portal = Portal(props["x"] as Float, props["y"] as Float, props["width"] as Float, props["height"] as Float, stage)
            portal.toMap = props["To"] as String
            portalMapObjectList.add(portal)
        }
    }

    override fun solidList() {
        for( obj in getRectangleList("Solid")){
            val props = obj.properties
            val solid = Solid(props["x"] as Float, props["y"] as Float, props["width"] as Float, props["height"] as Float,stage)
            collisionList.add(solid)
        }
    }

    override fun npcList() {
        for( obj in npcMapObjectList){
            val props = obj.properties
            val npc = NPC(props["x"] as Float, props["y"] as Float, stage)
            for (config in npcFolk) {
                npc.npcID = props["entityID"] as String
                if (npc.npcID == config.entityID) {
                    npc.dialogPath = config.conversationConfigPath
                    when (props["state"] as String) {
                        "IMMOBILE" -> {
                            npc.loadAnimationFromJson(npc, config, 8, 15)
                            npcList.add(npc)
                        }
                        "WALKING" -> {
                            guardTable[npc] = npc.loadAnimationFromJson(npc, npcGuard)
                            npcList.add(npc)
                        }
                        else -> Gdx.app.log(TAG, "idle:" + props["entityID"] as String)
                    }
                }
                npc.setBoundaryPolygon(8)

            }

        }
    }

    private fun npcPhysics(delta: Float){
        for ( npc in guardTable) {
            if(frameTime >=1f) {
                npc.key.apply {
                    setAnimationPaused(false)
                    applyPhysics(delta)
                    setSpeed(30f)
                }
            }
            else npc.key.setAnimationPaused(true)
            npc.key.apply {
                boundToWorld()
                if (MathUtils.round(MathUtils.random(0, 50).toFloat()) == 1)
                    when (BaseActor.AnimationType.getRandomNext()) {
                        BaseActor.AnimationType.WALK_RIGHT -> setMotionAngle(0f)
                        BaseActor.AnimationType.WALK_UP -> setMotionAngle(90f)
                        BaseActor.AnimationType.WALK_LEFT -> setMotionAngle(180f)
                        else -> setMotionAngle(-90f)
                    }
                when (getMotionAngle()) {
                    in 0f..89f -> setAnimation(npc.value[BaseActor.AnimationType.WALK_RIGHT]!!)
                    in 90f..179f -> setAnimation(npc.value[BaseActor.AnimationType.WALK_UP]!!)
                    in 180f..269f -> setAnimation(npc.value[BaseActor.AnimationType.WALK_LEFT]!!)
                    else -> setAnimation(npc.value[BaseActor.AnimationType.WALK_DOWN]!!)
                }
            }
        }
    }

    override fun act(delta: Float) {
        super.act(delta)
        frameTime = (frameTime + delta) % 5
        npcPhysics(delta)
    }


}