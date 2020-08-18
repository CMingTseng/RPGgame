package com.ss.rpggame.MapTools

import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.maps.MapObject
import com.badlogic.gdx.maps.objects.RectangleMapObject
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TiledMapTile
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.ss.rpggame.Actor.Graphics.Solid
import com.ss.rpggame.Actor.Enemies.NPC
import com.ss.rpggame.Actor.Graphics.Portal
import com.ss.rpggame.Util.Utility
import com.ss.rpggame.Base.BaseActor
import kotlin.collections.ArrayList

abstract class TileMapActor(private val filename:String, stage: Stage) : Actor() {
    // window dimensions
    companion object{
        var windowWidth = 800
        var windowHeight = 600
    }

    private val TAG = TileMapActor::class.java.simpleName

    var currentTileMap : TiledMap
    private var tiledCamera : OrthographicCamera
    private var tiledMapRenderer : OrthogonalTiledMapRenderer

    // set up tile map, layer,renderer, and camera
    private var tileWidth : Int = 0
    private var tileHeight : Int = 0
    private var numTileHorizontal : Int = 0
    private var numTileVertical : Int = 0
    private var mapWidth : Int = 0
    private var mapHeight : Int = 0
    private var time = 0f

    // set up tile information
    var collisionList : ArrayList<Solid> = ArrayList()
    var npcList : ArrayList<NPC> = ArrayList()
    var portalMapObjectList : ArrayList<Portal> = ArrayList()
    var positionHashMap : HashMap<String,Vector2> = HashMap()

    init {
        Utility.loadMapAsset(filename)
        currentTileMap = Utility.getMapAsset(filename)!!
        tileWidth = currentTileMap.properties["tilewidth"] as Int
        tileHeight = currentTileMap.properties["tileheight"] as Int
        numTileHorizontal = currentTileMap.properties["width"] as Int
        numTileVertical = currentTileMap.properties["height"]  as Int
        mapWidth = tileWidth * numTileHorizontal
        mapHeight = tileHeight * numTileVertical


        BaseActor.setWorldBounds(mapWidth.toFloat(),mapHeight.toFloat())
        tiledMapRenderer = OrthogonalTiledMapRenderer(currentTileMap)
        //tiledMapRenderer = OrthoCachedTiledMapRenderer(currentTileMap)
        tiledCamera = OrthographicCamera()
        tiledCamera.setToOrtho(false, windowWidth.toFloat(), windowHeight.toFloat())

        tiledCamera.update()
        stage.addActor(this)

        collisionList.clear()
        npcList.clear()
        portalMapObjectList.clear()

    }

    abstract fun playerPosition()

    abstract fun npcList()

    abstract fun solidList()

    abstract fun portalList()

    fun getRectangleList(propertyName: String) : ArrayList<MapObject> {
        val list = ArrayList<MapObject>()
        for (layer in currentTileMap.layers) {
            for (obj in layer.objects) {
                if (obj !is RectangleMapObject) continue
                val props = obj.getProperties()
                if (props.containsKey("name") && props["name"] == propertyName) list.add(obj)
            }
        }
        return list
    }

    fun getTileList(propertyName: String) : ArrayList<MapObject> {
        val list = ArrayList<MapObject>()
        for (layer in currentTileMap.layers) {
            for (obj in layer.objects) {
                if (obj !is TiledMapTileMapObject) continue
                val props = obj.getProperties()
                //  Default MapProperties are stored within associated Tile object
                // Instance-specific overrides are stored in MapObject
                val tiledMapTileMapObject : TiledMapTileMapObject = obj
                val t : TiledMapTile = tiledMapTileMapObject.tile
                val defaultProps = t.properties

                if (defaultProps.containsKey("name") && defaultProps["name"] == propertyName) list.add(obj)
                // get list of default property keys
                val propertyKeys : Iterator<String> = defaultProps.keys

                // iterate over keys; copy default values into props if needed
                while (propertyKeys.hasNext()){
                    val key : String = propertyKeys.next()

                    // check if value already exists; if not , create property with default value
                    if (props.containsKey(key)) continue else{
                        val value = defaultProps[key]
                        props.put(key, value)
                    }
                }
            }
        }
        return list
    }

    override fun act(delta: Float) {
        super.act(delta)
        time += delta
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.end()
        val mainCamera = stage.camera
        tiledCamera.position.x = mainCamera.position.x
        tiledCamera.position.y = mainCamera.position.y
        tiledCamera.update()
        tiledMapRenderer.setView(tiledCamera)
        // need the following code to force batch order,
        // otherwise it is batched and rendered last

        tiledMapRenderer.render()
        batch.begin()
    }


}