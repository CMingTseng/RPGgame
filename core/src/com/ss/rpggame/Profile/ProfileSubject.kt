package com.ss.rpggame.Profile

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Array as GdxArray
open class ProfileSubject {
    private val TAG = ProfileSubject::class.java.simpleName
    private var observers : GdxArray<ProfileObserver> = GdxArray()

    fun addObserver(profileObserver: ProfileObserver){ observers.add(profileObserver)}

    fun removeObserver(profileObserver: ProfileObserver){ observers.removeValue(profileObserver,true)}

    fun notify(profileManager: ProfileManager, event: ProfileObserver.ProfileEvent){
        for ( observer in observers) {
            observer.onNotify(profileManager, event)

        }
    }
}