package project.app.pocketsocket.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import project.app.pocketsocket.BR
import project.app.pocketsocket.R
import project.app.pocketsocket.model.Message
import project.app.pocketsocket.utils.Constants.RECEIVED_TYPE

class MessageAdapter(private val dataList: ArrayList<Message>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == RECEIVED_TYPE){
            ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.message_receive_item, parent, false))
        }
        else{
            ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.message_send_item, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is ViewHolder) {
            holder.bind(dataList[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return dataList[position].type
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class ViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(message: Message){
            binding.setVariable(BR.message, message)
        }
    }
}