package project.app.pocketsocket.view

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import project.app.pocketsocket.R
import project.app.pocketsocket.databinding.TextBinding
import project.app.pocketsocket.model.Message
import project.app.pocketsocket.utils.Constants.SENT_TYPE
import project.app.pocketsocket.utils.Util
import project.app.pocketsocket.viewmodel.ClientViewModel

class ClientActivity : AppCompatActivity() {

    private lateinit var binding: TextBinding
    private lateinit var clientViewModel: ClientViewModel
    private lateinit var name: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.text)

        val ip = Util.getIp(this)

        clientViewModel = ViewModelProviders.of(this).get(ClientViewModel::class.java)

        if(ip != getString(R.string.no_wifi)) {
            val prefix = ip.substring(0, ip.lastIndexOf(".") + 1)
            binding.etconnect.setText(prefix)
            binding.btconnect.setOnClickListener {
                if(TextUtils.isEmpty(binding.name.text.toString())){
                    binding.name.error = getString(R.string.empty_field)
                    return@setOnClickListener
                }

                if(TextUtils.isEmpty(binding.etconnect.text.toString())){
                    binding.etconnect.error = getString(R.string.empty_field)
                    return@setOnClickListener
                }

                else{
                    name = binding.name.text.toString().trim()
                    clientViewModel.connection(binding.etconnect.text.toString().trim())
                }
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
                binding.etconnect.error = getString(R.string.invalid_server)
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
            if(message.errorStatus){
                binding.edit.textSize = 14f
                binding.edit.setText(message.errorMessage)
                binding.edit.isEnabled = false
                binding.send.isEnabled = false
            }
            else {
                messages.add(message)
                adapter.notifyDataSetChanged()
                layoutManager.smoothScrollToPosition(binding.messageList, RecyclerView.State(), messages.size - 1)
            }
        })

        clientViewModel.messageDataList.observe(this, Observer {messageList ->
            if(messageList.size > 0) {
                messages.clear()
                messages.addAll(messageList)
                adapter.notifyDataSetChanged()
                layoutManager.smoothScrollToPosition(binding.messageList, RecyclerView.State(), messages.size - 1)
            }
        })

        binding.messageList.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if(messages.size > 0) {
                if (oldBottom - bottom > 0) {
                    layoutManager.smoothScrollToPosition(binding.messageList, RecyclerView.State(), messages.size - 1)
                }
            }
        }

        binding.send.setOnClickListener {
            val messageBody = binding.edit.text.toString()
            val message = Message(messageBody, name, SENT_TYPE)
            clientViewModel.sendMessage(message)
            messages.add(message)
            adapter.notifyDataSetChanged()
            binding.edit.setText("")
            layoutManager.smoothScrollToPosition(binding.messageList, RecyclerView.State(), messages.size - 1)
        }
    }

    override fun onResume() {
        super.onResume()
        clientViewModel.getMessageList()
    }
}
