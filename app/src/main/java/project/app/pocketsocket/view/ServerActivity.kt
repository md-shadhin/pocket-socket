package project.app.pocketsocket.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import project.app.pocketsocket.R
import project.app.pocketsocket.databinding.TextBinding
import project.app.pocketsocket.model.Message
import project.app.pocketsocket.utils.Util
import project.app.pocketsocket.viewmodel.ServerViewModel

class ServerActivity : AppCompatActivity() {

    private lateinit var binding: TextBinding
    private lateinit var serverViewModel: ServerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.text)

        binding.connectLayout.visibility = View.GONE
        binding.messageList.visibility = View.VISIBLE
        binding.editLayout.visibility = View.VISIBLE

        title = getString(R.string.host_server, Util.getIp(this))


        serverViewModel = ViewModelProviders.of(this).get(ServerViewModel::class.java)

        serverViewModel.connection()

        val messages = ArrayList<Message>()

        val adapter = MessageAdapter(messages)
        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.messageList.layoutManager = layoutManager
        adapter.setHasStableIds(true)

        binding.messageList.adapter = adapter

        serverViewModel.messageData.observe(this, Observer {message ->
            messages.add(message)
            adapter.notifyDataSetChanged()
            layoutManager.smoothScrollToPosition(binding.messageList, RecyclerView.State(), messages.size - 1)
        })

        binding.send.setOnClickListener {
            val message = binding.edit.text.toString()
            serverViewModel.sendMessage(message)
            messages.add(Message(message, getString(R.string.sender_label), 2))
            adapter.notifyDataSetChanged()
            binding.edit.setText("")
            layoutManager.smoothScrollToPosition(binding.messageList, RecyclerView.State(), messages.size - 1)
        }

    }

}
