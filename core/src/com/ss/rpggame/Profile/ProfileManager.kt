@file:Suppress("UNCHECKED_CAST")
package com.ss.rpggame.Profile

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.ObjectMap
import ktx.json.fromJson
import java.util.*
import com.badlogic.gdx.utils.Array as GdxArray

open class ProfileManager : ProfileSubject() {
    companion object{
        val profileManager = ProfileManager()
    }
    private val TAG = ProfileManager::class.java.simpleName
    private val _json = Json()
    private val _profiles : Hashtable<String, FileHandle> = Hashtable()
    var profileProperties : ObjectMap<String, Any> = ObjectMap()

    private val saveGameSuffix = ".sav"
    private var profileName = "default"
    private val saveDocument = "save/"

    init {
        _profiles.clear()
        storeAllProfiles()
    }

    fun getProfileList() : GdxArray<String>{
        val profiles : GdxArray<String> = GdxArray()
        val e = _profiles.keys()
        while (e.hasMoreElements()) profiles.add(e.nextElement())
        return profiles
    }

    fun getProfileFile(profile:String) : FileHandle?{
        if (!doesProfileExist(profile)) return null
        return _profiles[profile]
    }


    fun storeAllProfiles() {
        if (Gdx.files.isLocalStorageAvailable){
            val files = Gdx.files.local("Save.").list(saveGameSuffix)
            for (file in files){
                _profiles[file.nameWithoutExtension()] = file
            }
        }else return
    }

    fun doesProfileExist(profileName:String): Boolean = _profiles.containsKey(profileName)

    fun writeProfileToStorage(profileName: String,fileData:String,overwrite:Boolean){
        val fullFileName = saveDocument + profileName + saveGameSuffix
        val localFileExists = Gdx.files.internal(fullFileName).exists()

        // If we cannot overwrite and the file exists, exit
        if (localFileExists && !overwrite) return
        var file : FileHandle? = null
        if (Gdx.files.isLocalStorageAvailable){
            file = Gdx.files.local(fullFileName)
            file.writeString(fileData,!overwrite)
        }
        _profiles[profileName] = file
    }

    fun setProperty(key:String,obj: Any){ profileProperties.put(key,obj)}

    fun <T : Any?> getProperty(key: String?, type: Class<T>?): T? {
        var property: T? = null
        if (!profileProperties.containsKey(key)) {
            return property
        }
        property = profileProperties.get(key) as T?
        return property
    }

    fun saveProfile(){
        notify(this,ProfileObserver.ProfileEvent.SAVING_PROFILE)
        val text = _json.prettyPrint(_json.toJson(profileProperties))
        writeProfileToStorage(profileName,text,true)
    }

    fun loadProfile(){
        val fullFileName = saveDocument + profileName + saveGameSuffix
        val doesProfileExist = Gdx.files.internal(fullFileName).exists()

        // If we cannot overwrite and the file exists, exit
        if (!doesProfileExist) {
            Gdx.app.log(TAG,"File doesn't exist!")
            return
        }
        profileProperties = _json.fromJson(_profiles[profileName]!!)

        notify(this,ProfileObserver.ProfileEvent.PROFILE_LOADED)
    }

    fun setCurrentProfile(profileName: String){
        if (doesProfileExist(profileName)) this.profileName = profileName else this.profileName = "default"
    }

}

