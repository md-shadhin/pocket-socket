package project.app.pocketsocket.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import project.app.pocketsocket.model.Message
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket


class ClientViewModel: ViewModel() {

    private var scope: CoroutineScope = CoroutineScope(Job() + Dispatchers.IO)
    private var socket: Socket? = null
    private var toServer: ObjectOutputStream? = null
    private var fromServer: ObjectInputStream? = null
    var isServerAvailable : MutableLiveData<Boolean> = MutableLiveData()
    var messageData : MutableLiveData<Message> = MutableLiveData()

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
        isServerAvailable.postValue(false)
        try {
            socket?.close()
            fromServer?.close()
            toServer?.close()
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
                toServer = ObjectOutputStream(socket?.getOutputStream())
                fromServer = ObjectInputStream(socket?.getInputStream())
                receiveMessage()
                isServerAvailable.postValue(true)
            } catch (e: IOException) {
                isServerAvailable.postValue(false)
                e.printStackTrace()
            }
        }
    }

    private fun receiveMessage(){
        scope.launch {
            try {
                while (true){
                    val message = fromServer?.readObject() as String
                    messageData.postValue(Message(message, "Server: ", 1))
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
                toServer?.writeObject(message)
                toServer?.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}