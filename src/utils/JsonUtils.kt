/*
* Joonas Kauppinen ID: 1706859
*
* Utilities to make JSONObjects and format them to string.
*/

package utils

import org.json.JSONObject

object JsonUtils {
  fun makeMessageObject(type: String,
                        user: String = "",
                        message: String = "",
                        timeStamp: String = ""): JSONObject {
    return JSONObject()
            .put("timestamp", timeStamp)
            .put("message", message)
            .put("user", user)
            .put("type", type)
  }

  fun makeTopChatObject(name: String,
                        msgAmount: String): JSONObject {
    return JSONObject()
            .put("name", name)
            .put("msgAmount", msgAmount)
  }

  // Print format example: [John] @12:36 - Hello there!
  fun formatForTelnet(messageObject: JSONObject): String {
    return "[${messageObject.getString("user")}] " +
            "@${messageObject.getString("timestamp")} - " +
            "${messageObject.getString("message")}\r"
  }
}