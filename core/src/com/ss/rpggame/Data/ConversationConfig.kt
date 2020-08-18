package com.ss.rpggame.Data

import com.badlogic.gdx.utils.Array

class ConversationConfig internal constructor() {
    var currentConversationID: String = ""

    var conversation: Array<Conversation> = Array()

    var associatedChoices: Array<ConversationChoices> = Array()

    class Conversation{
        val id :String= ""
        val dialog:String= ""
    }

    class ConversationChoices{
        val sourceId :String= ""
        val destinationId :String= ""
        val choicePhrase :String= ""
        val conversationCommandEvent : ConversationCommandEvent?= null

        override fun toString(): String {
            return choicePhrase
        }

    }

    enum class ConversationCommandEvent{
        LOAD_STORE_INVENTORY,
        EXIT_CONVERSATION,
        NONE
    }

}