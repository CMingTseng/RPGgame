package com.ss.rpggame.MapTools

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue
import com.ss.rpggame.Actor.Enemies.NPC
import com.ss.rpggame.Actor.Enemies.Player
import com.ss.rpggame.Profile.ProfileManager
import com.ss.rpggame.Profile.ProfileObserver


class MapManager(var stage: Stage) : ProfileObserver{
    private val TAG = MapManager::class.java.simpleName
    var _previousMap : TileMapActor? = null
    var _currentMap : TileMapActor? = null
    var _mapChanged = false
    lateinit var player: Player
    //private var _stage = stage

    private fun loadMap(mapType: MapFactory.MapType, stage: Stage){
        val map = MapFactory.getMap(mapType,stage) ?: return
        _currentMap = map
        _mapChanged = true
    }

    fun getCurrentMap(stage: Stage) : TileMapActor? {

        if (_currentMap == null) loadMap(MapFactory.MapType.TownMap,stage)

        return _currentMap
    }

    fun update(player: Player){
        // change Map
        for (portal in _currentMap?.portalMapObjectList!!) {
            if (player.overlaps(portal)){
                when (portal.toMap) {
                    "TownMap" -> loadMap(MapFactory.MapType.TownMap, stage)
                    "TopWorldMap" -> loadMap(MapFactory.MapType.TopWorldMap, stage)
                    "CastleDoomMap" -> loadMap(MapFactory.MapType.CastleDoomMap, stage)
                }
            }

        }

        // collision NPC
        for (i in 0 until _currentMap!!.npcList.size) {
            for (npc in _currentMap!!.npcList) {

                if (npc.overlaps(_currentMap!!.npcList[i]) && npc != _currentMap!!.npcList[i]) {
                    (_currentMap!!.npcList[i]).preventOverlaps(npc)
                }

                if (player.isWithinDistance(player.width*2,npc) && npc.select){
                    //NPC.wasSelect = true // send selected Message to PlayerHUD
                    npc.select = false // clear selected option of
                    NPC.showDialog = true
                    Gdx.app.log(TAG,"ID:${npc.npcID} \t")
                }
            }
        }

        // collision Solid
        for (solid in _currentMap!!.collisionList) {
            if (player.overlaps(solid)) player.preventOverlaps(solid)
            for (npc in _currentMap!!.npcList) {
                if (npc.overlaps(solid)) npc.preventOverlaps(solid)
                if (player.overlaps(npc)) player.preventOverlaps(npc)
            }
        }

    }

    override fun onNotify(profileManager: ProfileManager, event: ProfileObserver.ProfileEvent) {
        when(event){
            ProfileObserver.ProfileEvent.PROFILE_LOADED ->{
                val currentMap = profileManager.getProperty("currentMapType",String::class.java)
                val mapType: MapFactory.MapType
                mapType = if (currentMap == null || currentMap.isEmpty()) MapFactory.MapType.TownMap else MapFactory.MapType.valueOf(currentMap)
                loadMap(mapType,stage)
                val currentPosition = profileManager.getProperty("currentPosition", JsonValue::class.java)
                if (currentPosition !=null ) {
                    val positionMap  = Json().readValue(HashMap::class.java,currentPosition)
                    for (map in positionMap)
                        _currentMap!!.positionHashMap[map.key as String] = map.value as Vector2
                }
            }

            ProfileObserver.ProfileEvent.SAVING_PROFILE ->{
                profileManager.setProperty("currentMapType",_currentMap!!.toString())
                if (_currentMap!!.positionHashMap.containsKey("currentPosition"))
                    profileManager.setProperty("currentPosition",_currentMap!!.positionHashMap)


            }

        }
    }


}