package com.ss.rpggame.Maps

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.ss.rpggame.Actor.Enemies.NPC
import com.ss.rpggame.Actor.Graphics.Portal
import com.ss.rpggame.Actor.Graphics.Solid
import com.ss.rpggame.MapTools.TileMapActor
import java.util.ArrayList

class TopWorldMap internal constructor(stage: Stage,mapPath:String = "maps/topWorld.tmx"): TileMapActor(mapPath,stage) {
    private val TAG = TopWorldMap::class.java.simpleName
    private var npcMapObjectList : ArrayList<MapObject> = ArrayList()
    init {

        npcMapObjectList = getTileList("NPC")

        npcList()

        solidList()

        portalList()

        playerPosition()

    }
    override fun playerPosition() {
        var from = ""
        for( obj in getRectangleList("start")){
            val props = obj.properties
            from = props["From"] as String
            positionHashMap[from] = Vector2(props["x"] as Float,props["y"] as Float)
        }
    }

    override fun npcList() {
        for( obj in npcMapObjectList){
            val props = obj.properties
            val npc = NPC(props["x"] as Float, props["y"] as Float, stage)
            /*
            when(props["state"] as String ){
                "IMMOBILE" -> {
                    for (config in npcFolk) {
                        if (props["entityID"] as String == config.entityID) {
                            npc.loadAnimationFromJson(npc,config,8,15)
                            npcList.add(npc)
                        }
                    }
                }
                "WALKING" -> {
                    guardTable[npc] = npc.loadAnimationFromJson(npc,npcGuard)
                    npcList.add(npc)
                }
                else -> Gdx.app.log(TAG,"idle:" + props["entityID"] as String)
            }
            npc.setBoundaryPolygon(8)
            */
        }
    }

    override fun solidList() {
        for( obj in getRectangleList("Solid")){
            val props = obj.properties
            val solid = Solid(props["x"] as Float, props["y"] as Float, props["width"] as Float, props["height"] as Float,stage)
            collisionList.add(solid)
        }

    }

    override fun portalList() {
        for( obj in getRectangleList("Portal")){
            val props = obj.properties
            val portal = Portal(props["x"] as Float, props["y"] as Float, props["width"] as Float, props["height"] as Float, stage)
            portal.toMap = props["To"] as String
            portalMapObjectList.add(portal)
            portal.setBoundaryPolygon(8)
        }
    }


}
