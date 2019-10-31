package project.app.pocketsocket.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ServerSocket

class ServerViewModel: ViewModel() {

    private lateinit var server: ServerSocket
    private var scope: CoroutineScope = CoroutineScope(Job()+Dispatchers.IO)
    private var clientNo = 0
    var messageFromClient : MutableLiveData<String> = MutableLiveData()
    private lateinit var toClient: ObjectOutputStream
    private lateinit var fromClient: ObjectInputStream
    init {
        try {
            server = ServerSocket(7777)
            Log.d("ServerViewModel", "Server initiated!")
        }
        catch (e: IOException){
            Log.d("ServerViewModel", "Server could not be initiated: $e")
        }
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
        try {
            server.close()
            fromClient.close()
            toClient.close()
            Log.d("ServerViewModel", "Server closed!")
        }
        catch (e: IOException){
            Log.d("ServerViewModel", "Server could not be closed: $e")
        }
    }

    fun connection(){
        scope.launch {
            try {
                while (true){
                    val socket = server.accept()
                    Log.d("ServerViewModel", "Server started!")
                    fromClient = ObjectInputStream(socket.getInputStream())
                    toClient = ObjectOutputStream(socket.getOutputStream())
                    clientNo++
                    receiveMessage()
                }
            }
            catch (e: IOException){
                Log.d("ServerViewModel", "Server could not be started: $e")
            }
        }
    }

    private fun receiveMessage(){
        scope.launch {
            try {
                while (true){
                    val message = fromClient.readObject() as String
                    messageFromClient.postValue("Client $clientNo: $message\n")
                }
            }
            catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    fun sendMessage(message: String){
        scope.launch {
            try {
                toClient.writeObject(message)
                toClient.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}