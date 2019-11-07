package project.app.pocketsocket.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import project.app.pocketsocket.model.Message
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ServerSocket

class ServerViewModel: ViewModel() {

    private var server: ServerSocket? = null
    private var scope: CoroutineScope = CoroutineScope(Job()+Dispatchers.IO)
    private var clientNo = 0
    private var toClient: ObjectOutputStream? = null
    private var fromClient: ObjectInputStream? = null
    var messageData : MutableLiveData<Message> = MutableLiveData()
    var messageDataList : MutableLiveData<ArrayList<Message>> = MutableLiveData()
    private val msgList = ArrayList<Message>()

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
            server?.close()
            fromClient?.close()
            toClient?.close()
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
                    val socket = server?.accept()
                    Log.d("ServerViewModel", "Server started!")
                    fromClient = ObjectInputStream(socket?.getInputStream())
                    toClient = ObjectOutputStream(socket?.getOutputStream())
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
                    val message = fromClient?.readObject() as String
                    messageData.postValue(Message(message, "Client $clientNo: ", 1))
                    msgList.add(Message(message, "Client $clientNo: ", 1))
                }
            }
            catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

    fun sendMessage(message: Message){
        msgList.add(message)
        scope.launch {
            try {
                toClient?.writeObject(message.body)
                toClient?.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun getMessageList(){
        viewModelScope.launch {
            messageDataList.postValue(msgList)
        }
    }
}