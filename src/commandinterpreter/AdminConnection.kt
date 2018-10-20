/*
* Joonas Kauppinen ID: 1706859
*
* CommandInterpreter for admin.
* Inherits base functionality from Connection.kt
* Has more functionality than normal client connection.
* Receives all messages from clients and prints them to console.
* Shows server updates in console E.g. "added user 'username'",
* "removed 'user'", "'user' left' and "Closing server..."
*
* Can:
*   - send messages to chat (clients see them as ADMIN messages)
*   - add/remove/kick users from server manually
*   - list current active users
*   - show chat history
*   - show top chatters
*   - close the ChatServer, stops listening for incoming connections
*/

package commandinterpreter

import model.ChatHistory
import model.ConnectedClients
import model.TopChatters
import model.Users
import utils.JsonUtils
import utils.JsonUtils.makeMessageObject
import java.io.InputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AdminConnection(private val input:  InputStream,
                                  output: OutputStream,
                      private val server: ServerSocket): Connection(input, output){

  private val formatter = DateTimeFormatter.ofPattern("HH:mm")

  fun run() {
    do {
      val userInput = Scanner(input).nextLine()
      if (userInput != "") handleInput(userInput)
    } while (userInput != ":exit")
    closeConnection()
  }

  private fun handleInput(input: String) {
    when (input[0]) {

      // If input starts with ':' it's a sever command.
      ':'  -> handleServerCommand(input)
      else -> {

        // Only add admin to TopChatters if admin sends any messages.
        if (!TopChatters.containsUser("ADMIN")) {
          TopChatters.addUser("ADMIN")
        }

        // Build a message object in JSON format to add to chat history.
        ChatHistory.addToHistory(
                makeMessageObject(
                        type = "message",
                        user = "ADMIN",
                        message = input,
                        timeStamp = LocalDateTime.now().format(formatter)
                ))
      }
    }
  }

  private fun handleServerCommand(command: String) {
    when (getCommand(command)) {
      ":user"       -> writeToClient( Users.addUser(getCommandValue(command)) )
      ":removeuser" -> ConnectedClients.kickClient(getCommandValue(command))
      ":users"      -> writeToClient( Users.toString() )
      ":messages"   -> writeToClient( ChatHistory.toString() )
      //":topchat"    -> writeToClient( TopChatters.getSortedTopChatters() )
      ":exit"       -> return
      else          -> writeToClient("ERROR: invalid command ${getCommand(command)}")
    }
  }

  // Immediately stops server from listening incoming connections.
  private fun closeConnection() {
    println("Closing server...")
    server.close()
  }

}