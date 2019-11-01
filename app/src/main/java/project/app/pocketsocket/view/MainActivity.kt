package project.app.pocketsocket.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import project.app.pocketsocket.R
import project.app.pocketsocket.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.server.setOnClickListener {
            startActivity(Intent(this, ServerActivity::class.java))
        }
        binding.client.setOnClickListener {
            startActivity(Intent(this, ClientActivity::class.java))
        }
    }
}
