package project.app.pocketsocket.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import project.app.pocketsocket.R
import project.app.pocketsocket.databinding.TextBinding
import project.app.pocketsocket.utils.Util
import project.app.pocketsocket.viewmodel.ClientViewModel

class ClientActivity : AppCompatActivity() {

    private lateinit var binding: TextBinding
    private lateinit var clientViewModel: ClientViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.text)

        val ip = Util.getIp(this)

        clientViewModel = ViewModelProviders.of(this).get(ClientViewModel::class.java)

        if(ip != getString(R.string.no_wifi)) {
            val prefix = ip.substring(0, ip.lastIndexOf(".") + 1)
            binding.etconnect.setText(prefix)
            binding.btconnect.setOnClickListener {
                clientViewModel.connection(binding.etconnect.text.toString())
            }
        }
        clientViewModel.isServerAvailable.observe(this, Observer {isServerAvailable ->
            if(isServerAvailable){
                binding.connectLayout.visibility = View.GONE
                binding.messageLayout.visibility = View.VISIBLE
                binding.editLayout.visibility = View.VISIBLE
                binding.ip.text = getString(R.string.connected_server, binding.etconnect.text.toString())
            }
            else{
                Toast.makeText(this, "Server not valid!", Toast.LENGTH_SHORT).show()
                binding.connectLayout.visibility = View.VISIBLE
                binding.messageLayout.visibility = View.GONE
                binding.editLayout.visibility = View.GONE
            }

        })
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
