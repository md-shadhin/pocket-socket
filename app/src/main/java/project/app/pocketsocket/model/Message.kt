package project.app.pocketsocket.model

import com.google.gson.GsonBuilder

class Message{
    var body: String
    var user: String
    var type: Int
    var errorStatus: Boolean
    var errorMessage: String

    constructor(body: String, user: String, type: Int) {
        this.body = body
        this.user = user
        this.type = type
        this.errorStatus = false
        this.errorMessage = String()
    }

    constructor(errorMessage: String) {
        this.body = String()
        this.user = String()
        this.type = 0
        this.errorStatus = true
        this.errorMessage = errorMessage
    }

    override fun toString(): String {
        val gson = GsonBuilder().setPrettyPrinting().create()
        return gson.toJson(this)
    }
}