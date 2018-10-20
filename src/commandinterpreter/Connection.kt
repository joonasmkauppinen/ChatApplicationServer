/*
* Joonas Kauppinen ID: 1706859
*
* Provides base functionality for admin and client connections.
*/

package commandinterpreter

import model.ChatHistory
import model.TopChatters
import org.json.JSONObject
import utils.JsonUtils.formatForTelnet
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.util.*
import kotlin.Exception

const val CONNECTION_TYPE_TELNET = 0
const val CONNECTION_TYPE_ANDROID = 1

open class Connection(private val input: InputStream,
                      private val output: OutputStream) : Observer {

  var connectionType = CONNECTION_TYPE_TELNET
  var userLoggedIn = false

  fun getCommand(input: String): String {
    return input.substringBefore(" ")
  }

  fun getCommandValue(input: String): String {
    return try {
      input.substring(getCommand(input).length + 1)
    } catch (e: Exception) {
      return ""
    }
  }

  fun writeToClient(message: String) {
    output.write( ("$message\n").toByteArray(Charset.defaultCharset()) )
  }

  open fun closeConnection(connection: Connection) {
    ChatHistory.deleteObserver(connection)
    input.close()
    output.close()
  }

  // Gets called when chat history changes.
  override fun update(o: Observable?, arg: Any?) {
    when (connectionType) {
      CONNECTION_TYPE_ANDROID -> {
        writeToClient( arg.toString() )
        if (userLoggedIn) {
          writeToClient( TopChatters.topChatAsJSONArray() )
        }
      }
      CONNECTION_TYPE_TELNET  -> writeToClient( formatForTelnet(arg as JSONObject) )
    }
  }

}