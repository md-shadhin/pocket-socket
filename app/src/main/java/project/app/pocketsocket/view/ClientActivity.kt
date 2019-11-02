package project.app.pocketsocket.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import project.app.pocketsocket.R
import project.app.pocketsocket.databinding.TextBinding
import project.app.pocketsocket.model.Message
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
                binding.messageList.visibility = View.VISIBLE
                binding.editLayout.visibility = View.VISIBLE
                title = getString(R.string.connected_server, binding.etconnect.text.toString())
                binding.edit.requestFocus()
            }
            else{
                Toast.makeText(this, getString(R.string.invalid_server), Toast.LENGTH_SHORT).show()
                binding.connectLayout.visibility = View.VISIBLE
                binding.messageList.visibility = View.GONE
                binding.editLayout.visibility = View.GONE
            }

        })

        val messages = ArrayList<Message>()

        val adapter = MessageAdapter(messages)
        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.messageList.layoutManager = layoutManager
        adapter.setHasStableIds(true)

        binding.messageList.adapter = adapter

        clientViewModel.messageData.observe(this, Observer {message ->
            messages.add(message)
            adapter.notifyDataSetChanged()
            layoutManager.smoothScrollToPosition(binding.messageList, RecyclerView.State(), messages.size - 1)
        })

        binding.send.setOnClickListener {
            val message = binding.edit.text.toString()
            clientViewModel.sendMessage(message)
            messages.add(Message(message, getString(R.string.sender_label), 2))
            adapter.notifyDataSetChanged()
            binding.edit.setText("")
            layoutManager.smoothScrollToPosition(binding.messageList, RecyclerView.State(), messages.size - 1)
        }
    }
}
