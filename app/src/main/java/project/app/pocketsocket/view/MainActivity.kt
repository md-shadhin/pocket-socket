package project.app.pocketsocket.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import project.app.pocketsocket.R
import project.app.pocketsocket.databinding.ActivityMainBinding
import project.app.pocketsocket.utils.Util.convertViewToBitmap

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var startCount = 0


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.client.setOnTouchListener { v, event ->
            val bmp = convertViewToBitmap(v)
            val color: Int = bmp.getPixel(event.x.toInt(), event.y.toInt())
            if (color == Color.TRANSPARENT) {
                if(++startCount == 1){
                    startActivity(Intent(this, ServerActivity::class.java))
                }
                return@setOnTouchListener false
            }
            else {
                if(++startCount == 1){
                    startActivity(Intent(this, ClientActivity::class.java))
                }
                return@setOnTouchListener true
            }

        }
    }

    override fun onResume() {
        super.onResume()
        startCount = 0
        binding.serverClientContent.startWaveAnimation()
    }

    override fun onPause() {
        super.onPause()
        binding.serverClientContent.stopWaveAnimation()
    }
}
