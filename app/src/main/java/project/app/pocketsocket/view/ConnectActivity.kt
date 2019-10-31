package project.app.pocketsocket.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import project.app.pocketsocket.R
import project.app.pocketsocket.databinding.ActivityConnectBinding
import project.app.pocketsocket.utils.Util

class ConnectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConnectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_connect)

        val ip = Util.getIp(this)

        if(ip != getString(R.string.no_wifi)) {
            val prefix = ip.substring(0, ip.lastIndexOf(".") + 1)
            binding.etconnect.setText(prefix)
            binding.btconnect.setOnClickListener {
                val intent = Intent(this, ClientActivity::class.java)
                intent.putExtra("ip", binding.etconnect.text.toString())
                startActivity(intent)
                finish()
            }
        }
    }
}
