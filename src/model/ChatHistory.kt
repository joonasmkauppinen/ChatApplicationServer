/*
* Joonas Kauppinen ID: 1706859
*
* Stores the chat history in JSON format.
*/

package model

import org.json.JSONArray
import org.json.JSONObject
import utils.JsonUtils.formatForTelnet
import java.util.*

object ChatHistory: Observable() {

  private val messagesJSON = JSONArray()

  fun addToHistory(messageObject: JSONObject) {
    messagesJSON.put(messageObject)
    TopChatters.updateUserScore(messageObject)
    setChanged()
    notifyObservers(messageObject)
  }

  // Used for android clients to get chat history.
  fun getMessagesJSONArray(): String {
    return messagesJSON.toString()
  }

  // Used for telnet clients and for admin to get chat history.
  override fun toString(): String {
    var messagesToString = ""
    for (i in 0 until messagesJSON.length()) {
      messagesToString += formatForTelnet( messagesJSON.getJSONObject(i) ) + "\n"
    }
    return messagesToString
  }

}