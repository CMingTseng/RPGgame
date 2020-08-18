package com.ss.rpggame.Screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import com.ss.rpggame.Actor.Enemies.Player
import com.ss.rpggame.Base.BaseGame
import com.ss.rpggame.Base.BaseScreen
import com.ss.rpggame.MapTools.MapManager
import com.ss.rpggame.MapTools.TileMapActor
import com.ss.rpggame.Profile.ProfileManager
import com.ss.rpggame.UI.PlayerHUD


class GameScreen (private val game: BaseGame) : BaseScreen()  {
    private val TAG = GameScreen::class.java.simpleName
    private lateinit var _mapManager : MapManager
    private var tilemap : TileMapActor? = null
    private lateinit var player: Player
    private var pos : Vector2 = Vector2(0f,0f)
    private lateinit var playerHUD: PlayerHUD
    override fun initialize() {
        _mapManager = MapManager(mainStage)

        tilemap = _mapManager.getCurrentMap(mainStage)

        playerHUD = PlayerHUD(0f,0f,mainStage,uiStage)

        ProfileManager.profileManager.addObserver(playerHUD)

        ProfileManager.profileManager.addObserver(_mapManager)

    }

    override fun update(delta: Float) {

        if (_mapManager._mapChanged){
            _mapManager._previousMap = tilemap
            tilemap = _mapManager.getCurrentMap(mainStage)

            // get Player Position
            pos = when {
                _mapManager._currentMap!!.positionHashMap.containsKey("currentPosition")  -> _mapManager._currentMap!!.positionHashMap["currentPosition"]!!
                _mapManager._previousMap.toString() == tilemap.toString() -> _mapManager._currentMap!!.positionHashMap.getValue("Init")
                else -> {
                    _mapManager._currentMap!!.positionHashMap.getValue(_mapManager._previousMap.toString())
                }
            }
           player = Player(pos.x, pos.y, mainStage)

            _mapManager._mapChanged = false
        }

        _mapManager.update(player)

    }


    override fun resume() {
        ProfileManager.profileManager.loadProfile()
    }

    override fun pause() {
        if (!_mapManager._mapChanged) ProfileManager.profileManager.saveProfile()
    }

    override fun keyDown(keycode: Int): Boolean {
        if (keycode == Input.Keys.ESCAPE){
            Gdx.app.exit()
            dispose()
        }

        if (keycode == Input.Keys.S) {
            _mapManager._currentMap!!.positionHashMap["currentPosition"] = Vector2(player.x, player.y)
            ProfileManager.profileManager.saveProfile()
        }

        return false
    }

}