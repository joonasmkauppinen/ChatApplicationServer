/*
* Joonas Kauppinen ID: 1706859
*
* Stores connected client threads and enables to kick individual clients by name.
* Also disconnects all currently connected clients when server is closed.
*/

package model

import commandinterpreter.ClientConnection

object ConnectedClients {

  private val clientMap = HashMap<String, ClientConnection>()

  fun addClient(user: String, client: ClientConnection) {
    clientMap.put(user, client)
    println(clientMap)
  }

  fun closeClientConnection(user: String) {
    clientMap.remove(user)
  }

  //TODO("add compatibility with android")
  fun kickClient(user: String) {
    clientMap[user]?.writeToClient("You were kicked out by the admin.")
    closeClientConnection(user)
  }

  fun getClientsAsArray(): List<ClientConnection> {
    return clientMap.values.toList()
  }

}