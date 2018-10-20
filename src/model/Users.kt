/*
* Joonas Kauppinen ID: 1706859
*
* Stores current users.
*/

package model

import model.ChatHistory.addToHistory
import utils.JsonUtils.makeMessageObject

object Users {

  private val users = arrayListOf<String>()

  // Lists current users for admin console.
  override fun toString(): String {
    var usersString = "Current users:\n"
    for (user in users) usersString += "\t$user\n"
    return usersString
  }

  fun addUser(newUser: String): String {
    if (!isExistingUser(newUser) && newUser != "") {
      users.add(newUser)
      TopChatters.addUser(newUser)
      addToHistory(
              makeMessageObject(
                      type = "update",
                      message = "'$newUser' joined"
              )
      )
      return "added '$newUser'\n"
    } else if(newUser == "") {
      return "ERROR: empty string"
    }
    return "ERROR: '$newUser' already exists\n"
  }

  fun removeUser(user: String): String {
    if (isExistingUser(user)) {
      users.remove(user)
      TopChatters.removeUser(user)
      addToHistory(
              makeMessageObject(
                      type = "update",
                      message = "'$user' left"
              )
      )
      ConnectedClients.closeClientConnection(user)
      return "removed '$user'"
    }
    return "ERROR: can't remove, '$user' doesn't exist"
  }

  fun isExistingUser(user: String): Boolean {
    return users.contains(user)
  }

}