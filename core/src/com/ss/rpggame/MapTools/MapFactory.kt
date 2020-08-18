package com.ss.rpggame.MapTools

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.ss.rpggame.Maps.CastleDoomMap
import com.ss.rpggame.Maps.TopWorldMap
import com.ss.rpggame.Maps.TownMap
import java.util.*
class MapFactory {


    enum class MapType{ TopWorldMap,TownMap,CastleDoomMap }

    companion object{
        private val TAG = MapFactory::class.java.simpleName
        private val _mapTable : Hashtable<MapType, TileMapActor> = Hashtable()

        fun getMap(mapType: MapType, stage: Stage) : TileMapActor?{
            var map : TileMapActor? = null
            when(mapType) {
                MapType.TopWorldMap -> {
                    map = _mapTable[MapType.TopWorldMap]
                    if (map == null) {
                        map = TopWorldMap(stage)
                    }
                }
                MapType.TownMap -> {
                    map = _mapTable[MapType.TownMap]
                    if (map == null) {
                        map = TownMap(stage)
                    }
                }
                MapType.CastleDoomMap -> {
                    map = _mapTable[MapType.CastleDoomMap]
                    if (map == null) {
                        map = CastleDoomMap(stage)
                    }
                }
            }
            //Gdx.app.log(TAG,"mapType:$mapType \t map:$map")
            return map

        }
    }

}