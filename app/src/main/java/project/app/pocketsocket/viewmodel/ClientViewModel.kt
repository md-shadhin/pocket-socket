package project.app.pocketsocket.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket


class ClientViewModel: ViewModel() {

    private var scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
    private lateinit var socket: Socket
    private lateinit var toServer: ObjectOutputStream
    private lateinit var fromServer: ObjectInputStream
    var messageFromServer : MutableLiveData<String> = MutableLiveData()

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
        try {
            socket.close()
            fromServer.close()
            toServer.close()
            Log.d("ClientViewModel", "Client closed!")
        }
        catch (e: IOException){
            Log.d("ClientViewModel", "Client could not be closed: $e")
        }
    }

    fun connection(ip: String){
        scope.launch {
            try {
                socket = Socket(ip, 7777)
                toServer = ObjectOutputStream(socket.getOutputStream())
                fromServer = ObjectInputStream(socket.getInputStream())
                receiveMessage()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun receiveMessage(){
        scope.launch {
            try {
                while (true){
                    val message = fromServer.readObject() as String
                    Log.d("ClientViewModel", "message: $message")
                    messageFromServer.postValue("Server: $message\n")
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
                toServer.writeObject(message)
                toServer.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}