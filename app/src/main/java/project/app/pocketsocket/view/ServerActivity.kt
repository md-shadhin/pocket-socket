package project.app.pocketsocket.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import project.app.pocketsocket.R
import project.app.pocketsocket.databinding.TextBinding
import project.app.pocketsocket.utils.Util
import project.app.pocketsocket.viewmodel.ServerViewModel

class ServerActivity : AppCompatActivity() {

    private lateinit var binding: TextBinding
    private lateinit var serverViewModel: ServerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.text)

        binding.connectLayout.visibility = View.GONE
        binding.messageLayout.visibility = View.VISIBLE
        binding.editLayout.visibility = View.VISIBLE

        binding.ip.text = getString(R.string.host_server, Util.getIp(this))
        serverViewModel = ViewModelProviders.of(this).get(ServerViewModel::class.java)

        serverViewModel.connection()

        serverViewModel.messageFromClient.observe(this, Observer {
            binding.ip.append(it)
        })

        binding.send.setOnClickListener {
            val message = binding.edit.text.toString()
            serverViewModel.sendMessage(message)
            binding.ip.append(message+"\n")
            binding.edit.setText("")
        }

    }

}
