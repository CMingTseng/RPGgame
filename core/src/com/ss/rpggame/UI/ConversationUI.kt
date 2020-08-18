package com.ss.rpggame.UI

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.List
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.*
import com.ss.rpggame.Actor.Enemies.NPC
import com.ss.rpggame.Base.BaseActor
import com.ss.rpggame.Data.ConversationConfig
import com.ss.rpggame.Util.Utility.STATUS_UI_SKIN
import ktx.json.fromJson
import java.util.*


class ConversationUI(x:Float, y:Float, stage: Stage) :BaseActor(x, y, stage) {
    companion object{
        private val TAG = ConversationUI::class.java.simpleName
    }

    var window : Window = Window("Dialog", STATUS_UI_SKIN, "solidbackground")
    private val dialogLabel: Label = Label("No Conversation this lagetegfdasgdsadfasfd", STATUS_UI_SKIN)
    private val listItems: List<ConversationConfig.ConversationChoices?> = List(STATUS_UI_SKIN)
    private val scrollPane = ScrollPane(listItems, STATUS_UI_SKIN, "inventoryPane")
    val closeButton: TextButton = TextButton("X", STATUS_UI_SKIN)
    private val padding = 20f
    private var listener: ClickListener = ClickListener()

    var conversations: Hashtable<String, ConversationConfig.Conversation> =  Hashtable()
    var associatedChoices : Hashtable<String,ArrayList<ConversationConfig.ConversationChoices>> = Hashtable()
    var currentConversationID :String = ""
    var choice = ConversationConfig.ConversationCommandEvent.NONE
    var loadDialog = true
    init {

        closeButton.setSize(16f,16f)

        listItems.style.selection.bottomHeight = 8f

        scrollPane.apply {
            setOverscroll(false, false)
            fadeScrollBars = false
            setScrollingDisabled(true, false)
            setForceScroll(true, false)
            setScrollBarPositions(true, true)
            style.background.apply {
                topHeight = 10f
                bottomHeight = 2f
            }
            /*
            style.hScroll.apply {
                topHeight = 1f
                bottomHeight = 1f
                rightWidth = 1f
                leftWidth = 1f
            }
            style.hScrollKnob.apply {
                topHeight = 1f
                bottomHeight = 1f
                rightWidth = 1f
                leftWidth = 1f
            }
            style.vScroll.apply {
                topHeight = 1f
                bottomHeight = 1f
                rightWidth = 1f
                leftWidth = 1f
            }
            style.vScrollKnob.apply {
                topHeight = 1f
                bottomHeight = 1f
                rightWidth = 1f
                leftWidth = 1f
            }

             */
        }

        //layout
        window.apply{
            add()
            add(closeButton)
            row()
            defaults().expand().fill()
            add(dialogLabel).pad(10f, 10f, 10f, 10f)
            row()
            add(scrollPane).pad(10f, 10f, 10f, 10f)
            row()
            //debug()
            pack()
        }

        dialogLabel.setWrap(true)
        dialogLabel.setAlignment(Align.center)

        window.setSize(stage.width/2,stage.height/2)

        //Listeners

        listener = object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                val choices = listItems.selected ?: return
                currentConversationID = choices.destinationId
                choice = choices.conversationCommandEvent ?:return
                populateConversationDialog(currentConversationID)
            }
        }

        listItems.addListener(listener)


    }

    private fun setText(text: String?){
        if (text.isNullOrBlank()) dialogLabel.setText("")
        else dialogLabel.setText(text)
    }

    fun setFontScale(scale:Float){
        dialogLabel.setFontScale(scale)
    }

    fun setFontColor(color: Color?) {
        dialogLabel.color = color
    }

    fun setBackgroundColor(color: Color) {
        this.color = color
    }

    fun alignTopLeft() { dialogLabel.setAlignment(Align.topLeft) }

    fun alignCenter() { dialogLabel.setAlignment(Align.center) }

    private fun isValid(conversationID: String) : Boolean{
        val conversation = conversations[conversationID] ?: return false
        return true
    }

    private fun getConversationByID(conversationID: String) : ConversationConfig.Conversation? {
        if (!isValid(conversationID)) {
            return null
        }
        return conversations[conversationID]
    }

    private fun getCurrentChoices() : ArrayList<ConversationConfig.ConversationChoices>? = associatedChoices[currentConversationID]

    private fun setChoices(currentConversationID:String){
        val choices = getCurrentChoices() ?: return
        listItems.setItems(* choices.toTypedArray())
    }

    private fun clearDialog(){
        setText("")
        listItems.clearItems()
    }

    private fun populateConversationDialog(conversationID: String) {
        clearDialog()
        getConversationByID(conversationID)
        val dialog = conversations[conversationID]?.dialog ?: return
        setText(dialog)
        setChoices(conversationID)
        loadDialog = false
    }

    fun loadConversation(dialogPath:String?){
        // title
        window.titleLabel.setText(NPC.ID)

        if (dialogPath.isNullOrBlank()) return
        val file = Gdx.files.internal(dialogPath)
        val conversationConfig =  Json().fromJson<ConversationConfig>(file)

        currentConversationID = conversationConfig.currentConversationID

        // conversation
        val conversationArray = conversationConfig.conversation
        conversationArray.iterator().forEach {
            conversations[it.id] = it
        }
        // conversation choices
        val associatedChoicesArray = conversationConfig.associatedChoices
        val map = associatedChoicesArray.groupBy({ it.sourceId }, { it })
        map.forEach { associatedChoices[it.key] = ArrayList(it.value) }




    }

    override fun clear(){
        clearDialog()
        loadDialog = true
        conversations.clear()
        associatedChoices.clear()
        currentConversationID = ""
    }

    override fun toString(): String{
        val outputString = StringBuilder()
        var numberTotalChoices : Int = 0

        val keys: Set<String> = associatedChoices.keys

        for (id in keys){
            outputString.append(String.format("[%s]: ", id))

            for (choice in associatedChoices[id]!!){
                numberTotalChoices++
                outputString.append(String.format("[%s]: ", choice.destinationId))
            }
            outputString.append(System.getProperty("line.separator"))
        }
        outputString.append(String.format("Number conversations: %d ", conversations.size))
        outputString.append(String.format(", Number of choices: %d ", numberTotalChoices))
        outputString.append(System.getProperty("line.separator"))

        return outputString.toString()
    }

    override fun act(delta: Float) {
        super.act(delta)

        stage.addActor(window)
        window.setPosition(x,y)

        if (!NPC.path.isBlank())loadConversation(NPC.path)
        if (loadDialog && !NPC.path.isBlank()) { populateConversationDialog(currentConversationID) }

    }
}