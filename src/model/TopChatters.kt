/*
* Joonas Kauppinen ID: 1706859
*
* Stores top chatters in a map and returns them in descending order.
*/

package model

import org.json.JSONArray
import org.json.JSONObject
import utils.JsonUtils
import java.util.*

object TopChatters {

  private var topChattersMap = HashMap<String, Int?>()
  private lateinit var sortedMap: Map<String, Int?>
  private var topChattersJSONArray = JSONArray()

  //TODO("add ability to send top chatters to android client")

  fun addUser(user: String) {
    topChattersMap.put(user, 0)
  }

  fun removeUser(user: String) {
    topChattersMap.remove(user)
  }

  fun updateUserScore(update: JSONObject) {
    if (update.getString("type") == "message") {
      val user = update.getString("user")
//    topChattersMap[user]?.plus(1) as Int
      val currentVal: Int? = topChattersMap[user]
      topChattersMap[user] = currentVal?.plus(1)
      sortTopChatters()
    }
  }

  fun sortTopChatters() {
    sortedMap = topChattersMap.toList().sortedBy { (_, value) -> value }.reversed().toMap()
    topChattersJSONArray = JSONArray()
    sortedMap.forEach { (key, value) ->
      kotlin.run {
        topChattersJSONArray.put(
                JsonUtils.makeTopChatObject(
                        name = key,
                        msgAmount = value.toString()
                )
        )
      }
    }
    //println(topChattersJSONArray.toString(2))
  }

//  fun getSortedTopChatters(): String {
//    var topChatString = "Top Chat:\n"
//    if (sortedMap != null) {
//      sortedMap.forEach{ (key, value) -> topChatString += "\t$key: $value messages\n" }
//    }
//    return topChatString
//  }

  fun containsUser(user: String): Boolean {
    return topChattersMap.containsKey(user)
  }

  fun topChatAsJSONArray(): String {
    return topChattersJSONArray.toString()
  }

  // Only updates topChattersMap if chat history entry is of type "message".
//  override fun update(o: Observable?, arg: Any?) {
//    if (arg is JSONObject && arg.getString("type") == "message") updateUserScore(arg)
//  }

}