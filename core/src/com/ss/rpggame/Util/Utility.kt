package com.ss.rpggame.Util

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.TextureLoader
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.maps.tiled.TiledMap
import com.badlogic.gdx.maps.tiled.TmxMapLoader
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import ktx.json.fromJson

object Utility {
    private val TAG = Utility::class.java.simpleName
    private var _assetManager : AssetManager = AssetManager()
    private var _filePathResolver : InternalFileHandleResolver = InternalFileHandleResolver()
    private const val STATUS_UI_TEXTURE_ATLAS_PATH = "skins/status_ui.atlas"
    private const val STATUS_UI_SKIN_PATH = "skins/status_ui.json"
    private const val ITEM_TEXTURE_ATLAS_PATH = "skins/items.atlas"
    private const val ITEM_SKIN_PATH = "skins/item.json"
    val STATUS_UI_TEXTURE_ATLAS = TextureAtlas(STATUS_UI_TEXTURE_ATLAS_PATH)
    val ITEMS_TEXTURE_ATLAS = TextureAtlas(ITEM_TEXTURE_ATLAS_PATH)
    val STATUS_UI_SKIN = Skin(Gdx.files.internal(STATUS_UI_SKIN_PATH), STATUS_UI_TEXTURE_ATLAS)

    fun unloadAsset(assetFilenamePath:String){
        // once the asset manager is done loading
        if (_assetManager.isLoaded(assetFilenamePath)){
            _assetManager.unload(assetFilenamePath)
        }else{
            Gdx.app.log(TAG,"Asset is not loaded; Nothing to unload: $assetFilenamePath")
        }
    }

    fun loadCompleted() : Float = _assetManager.progress

    fun numberAssetsQueued() : Int = _assetManager.queuedAssets

    fun isAssetLoaded(fileName:String) : Boolean = _assetManager.isLoaded(fileName)

    fun loadMapAsset(mapFilenamePath : String?){
        if (mapFilenamePath.isNullOrEmpty()) return
        // load asset
        _filePathResolver = InternalFileHandleResolver()

        if (_filePathResolver.resolve(mapFilenamePath).exists()){
            _assetManager.apply {
                setLoader(TiledMap::class.java, TmxMapLoader(_filePathResolver))
                load(mapFilenamePath, TiledMap::class.java)
                finishLoadingAsset<String>(mapFilenamePath)
            }
            //Until we add loading screen, just block until we load the map
            //Gdx.app.log(TAG,"Map loaded!: $mapFilenamePath")
        }else{
            Gdx.app.log(TAG,"Map doesn't exist!: $mapFilenamePath")
        }


    }

    fun getMapAsset(mapFilenamePath: String?) : TiledMap? {
        var map : TiledMap? = null
        // once the asset manager is done loading
        if (_assetManager.isLoaded(mapFilenamePath)){
            map = _assetManager[mapFilenamePath, TiledMap::class.java]
        }else{
            Gdx.app.log(TAG,"Map is not loaded: $mapFilenamePath")
        }
        return map
    }

    fun loadTextureAsset(textureFilenamePath:String?){
        if (textureFilenamePath.isNullOrEmpty()) return
        // load asset
        _filePathResolver = InternalFileHandleResolver()
        if ( _filePathResolver.resolve(textureFilenamePath).exists() ){
            _assetManager.apply {
                setLoader(Texture::class.java, TextureLoader(_filePathResolver))
                load(textureFilenamePath, Texture::class.java)
                finishLoadingAsset<String>(textureFilenamePath)
            }
        }else{
            Gdx.app.log(TAG,"Texture doesn't exist!: $textureFilenamePath")
        }
    }

    fun getTextureAsset(textureFilenamePath: String) : Texture?{
        var texture : Texture? = null
        // once the asset manager is done loading
        if ( _assetManager.isLoaded(textureFilenamePath)){
            texture = _assetManager.get(textureFilenamePath, Texture::class.java)
        }else{
            Gdx.app.log(TAG,"Texture is not loaded: $textureFilenamePath")
        }

        return texture
    }



}