/*
* Joonas Kauppinen ID: 1706859
*
* Creates socket server on port 9999 and starts to listen
* for incoming connections.
*/

import commandinterpreter.AdminConnection
import commandinterpreter.ClientConnection
import model.ChatHistory
import model.ConnectedClients
import model.TopChatters
import java.net.InetAddress
import java.net.ServerSocket
import kotlin.concurrent.thread

fun main(args: Array<String>) {
  serve()
}

private fun serve() {

  val host = InetAddress.getLocalHost().hostAddress

  val server = ServerSocket(9999)
  println("Server is running on port ${server.localPort}, host: $host\n")

  //ChatHistory.addObserver(TopChatters)

  //TODO("try using coroutine for admin connection")
  val admin = AdminConnection(System.`in`, System.out, server)
  ChatHistory.addObserver(admin)
  // Start new thread for admin connection.
  thread { admin.run() }

  var serverRunning = true
  while (serverRunning) {
    try {
      val socket = server.accept()
      println("Client connected: ${socket.inetAddress.hostAddress}")
      val client = ClientConnection(socket.getInputStream(), socket.getOutputStream())
      ChatHistory.addObserver(client)
      // Start new thread for client connection
      thread { client.run() }
    } catch (e: Exception) {
      serverRunning = false
      println("Closing clients...")
      for (client in ConnectedClients.getClientsAsArray()) client.closeConnection(client)
    }
  }

}



