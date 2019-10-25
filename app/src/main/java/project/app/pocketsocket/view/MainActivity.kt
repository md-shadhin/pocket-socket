package project.app.pocketsocket.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import project.app.pocketsocket.R
import project.app.pocketsocket.viewmodel.ServerViewModel

class MainActivity : AppCompatActivity() {

    lateinit var serverViewModel: ServerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        serverViewModel = ViewModelProviders.of(this).get(ServerViewModel::class.java)
    }
}
