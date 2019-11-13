package project.app.pocketsocket.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.*
import project.app.pocketsocket.model.Message
import project.app.pocketsocket.utils.Constants.RECEIVED_TYPE
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ServerSocket
import java.net.Socket

class ServerViewModel: ViewModel() {

    private var server: ServerSocket? = null
    private val allClients = ArrayList<ClientHandler>()
    private var scope: CoroutineScope = CoroutineScope(Job()+Dispatchers.IO)
    private val msgList = ArrayList<Message>()
    val messageData : MutableLiveData<Message> = MutableLiveData()
    val messageDataList : MutableLiveData<ArrayList<Message>> = MutableLiveData()

    companion object{
        fun toObj(msg: String): Message{
            return Gson().fromJson(msg, Message::class.java)
        }
    }

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
            allClients.forEach { client ->
                client.socket?.close()
                client.fromClient?.close()
                client.toClient?.close()
            }

            allClients.clear()
            msgList.clear()
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
                    val fromClient = ObjectInputStream(socket?.getInputStream())
                    val toClient = ObjectOutputStream(socket?.getOutputStream())

                    val client = ClientHandler(socket, toClient, fromClient, this, messageData, msgList, allClients)
                    client.start()
                    allClients.add(client)
                }
            }
            catch (e: IOException){
                //e.printStackTrace()
            }
        }
    }

    fun sendMessage(message: Message){
        msgList.add(message)
        scope.launch {
            try {
                allClients.forEach { client ->
                    client.socket?.isClosed?.let {isClosed ->
                        if(!isClosed) {
                            client.toClient?.writeObject(message.toString())
                            client.toClient?.flush()
                        }
                    }
                }
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

    class ClientHandler(val socket: Socket?, val toClient: ObjectOutputStream?, val fromClient: ObjectInputStream?,
                        private val scope: CoroutineScope, private val messageData : MutableLiveData<Message>,
                        private val msgList: ArrayList<Message>, private val allClients: ArrayList<ClientHandler>){

        fun start() {
            scope.launch {
                try {
                    while (true) {
                        val message = fromClient?.readObject() as String
                        val msgObj = toObj(message)
                        msgObj.type = RECEIVED_TYPE
                        messageData.postValue(msgObj)
                        msgList.add(msgObj)
                        allClients.forEach { client ->
                            if(!client.socket?.isClosed!!) {
                                if (!client.fromClient?.equals(fromClient)!!) {
                                    client.toClient?.writeObject(message)
                                    client.toClient?.flush()
                                }
                            }
                            else{
                                allClients.remove(client)
                            }
                        }
                    }
                } catch (e: IOException) {
                    allClients.remove(this@ClientHandler)
                }
            }
        }
    }
}