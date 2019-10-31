package project.app.pocketsocket.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import project.app.pocketsocket.R
import project.app.pocketsocket.databinding.TextBinding
import project.app.pocketsocket.viewmodel.ClientViewModel

class ClientActivity : AppCompatActivity() {

    private lateinit var binding: TextBinding
    private lateinit var clientViewModel: ClientViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.text)
        val ip = intent?.extras?.getString("ip")!!
        binding.ip.text = getString(R.string.connected_server, ip)

        clientViewModel = ViewModelProviders.of(this).get(ClientViewModel::class.java)
        clientViewModel.connection(ip)

        clientViewModel.messageFromServer.observe(this, Observer {
            binding.ip.append(it)
        })

        binding.send.setOnClickListener {
            val message = binding.edit.text.toString()
            clientViewModel.sendMessage(message)
            binding.ip.append(message+"\n")
            binding.edit.setText("")
        }
    }
}
