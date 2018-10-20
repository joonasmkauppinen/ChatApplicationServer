/*
* Joonas Kauppinen ID: 1706859
*
* CommandInterpreter for client.
* Inherits base functionality from Connection.kt
* Has less functionality than admin.
*
* Can:
*   - check if connection is android or telnet
*   - add new user to Users if not existing user
*   - add messages to chat history
*   - remove user from Users when end device disconnects
*/

package commandinterpreter

import model.ChatHistory
import model.ConnectedClients
import model.TopChatters
import model.Users
import org.json.JSONObject
import utils.JsonUtils
import utils.JsonUtils.makeMessageObject
import java.io.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class ClientConnection(private val inputStream: InputStream,
                                        output: OutputStream) : Connection(inputStream, output) {

  private var connectionClosed = false
  lateinit var user: String

  private val formatter = DateTimeFormatter.ofPattern("HH:mm")

  fun run() {

    // If user disconnects before handleLogin() finishes
    // close socket and terminate code execution here.
    try {
      handleLogin( Scanner(inputStream).nextLine() )
    } catch (e: Exception) {
      closeConnection(this)
      return
    }

    ConnectedClients.addClient(user, this)
    println(Users.addUser(user))

    // If connection type is android send a confirmation
    // message to the client which then opens the chat
    // on the android device.
    if (connectionType == CONNECTION_TYPE_ANDROID) {
      writeToClient(
              makeMessageObject(
                      type = "command",
                      message = ":success"
              ).toString()
      )
    }

    // Send chat history to new client
    when (connectionType) {
      CONNECTION_TYPE_ANDROID -> {
        writeToClient( ChatHistory.getMessagesJSONArray() )
        writeToClient( TopChatters.topChatAsJSONArray() )
      }
      CONNECTION_TYPE_TELNET  -> writeToClient( ChatHistory.toString() )
    }


    // If connection type is telnet just send a simple
    // info string to signal successful login
    if (connectionType == CONNECTION_TYPE_TELNET) {
      writeToClient("Type a message...\n\r")
    }

    userLoggedIn = true

    // Listen for coming messages or exit command from client.
    do {
      var userInput: String
      // If client disconnects while logged in, catch block
      // issues an exit command and exits the loop.
      try {
        userInput = Scanner(inputStream).nextLine()
        if (connectionType == CONNECTION_TYPE_ANDROID) {
          ChatHistory.addToHistory(JSONObject(userInput))
        } else if (connectionType == CONNECTION_TYPE_TELNET) {
          handleInput(userInput)
        }
      } catch (e: Exception) {
        userInput = ":exit"
      }
    } while (userInput != ":exit")

    if (!connectionClosed) closeConnection(this)

    println(Users.removeUser(user))
    println("$user left server")

  }

  // Determines if connection is android or telnet.
  private fun handleLogin(input: String) {
    // If input can be constructed into JSONObject
    // connection is android otherwise it's telnet.
    try {
      val androidInput = JSONObject(input)
      connectionType = CONNECTION_TYPE_ANDROID
      if (androidInput.getString("type") == "command") {
        handleAndroidConnection( androidInput.getString("message") )
      }
    } catch (e: Exception) {
      handleTelnetConnection()
    }
  }

  // Asks client for username until valid username is given.
  private fun handleAndroidConnection(input: String) {
    user = getCommandValue(input)
    while (Users.isExistingUser(user)) {
      if (Users.isExistingUser(user)) {
        writeToClient(
                makeMessageObject(
                        type = "error",
                        message = "'$user' already exists"
                ).toString()
        )
      }
      user = getCommandValue(
              JSONObject(Scanner(inputStream).nextLine())
                      .getString("message")
      )

    }
  }

  // Asks client for username until valid username is given.
  private fun handleTelnetConnection() {
    do {
      writeToClient("Enter username:\r")
      user = Scanner(inputStream).nextLine()
      if (Users.isExistingUser(user)) {
        writeToClient("'$user' already exists.\r")
      }
    } while (Users.isExistingUser(user) || user == "")
  }

  private fun handleInput(input: String) {
    when (getCommand(input)) {
      ":exit" -> return
      else -> {
        // Builds a JSONObject from input and adds it to chat history.
        ChatHistory.addToHistory(
                makeMessageObject(
                        type = "message",
                        user = user,
                        message = input,
                        timeStamp = LocalDateTime.now().format(formatter)
                )
        )
      }
    }
  }

}