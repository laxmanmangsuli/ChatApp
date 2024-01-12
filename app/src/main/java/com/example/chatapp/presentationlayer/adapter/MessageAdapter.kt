package com.example.chatapp.presentationlayer.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.chatapp.utils.SharedPrefs
import com.example.chatapp.databinding.ReceiveLayoutBinding
import com.example.chatapp.databinding.SentLayoutBinding
import com.example.chatapp.data.Message
import com.example.chatapp.utils.MessageDffUtil

class MessageAdapter : RecyclerView.Adapter<ViewHolder>() {

    private var oldList = emptyList<Message?>()

    companion object {
        const val VIEW_TYPE_SEND = 1
        const val VIEW_TYPE_RECEIVE = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SEND -> SendViewHolder(
                SentLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            VIEW_TYPE_RECEIVE -> ReceiveViewHolder(
                ReceiveLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = oldList[position]

        when (holder.itemViewType) {
            VIEW_TYPE_SEND -> (holder as SendViewHolder).bindSend(item)
            VIEW_TYPE_RECEIVE -> (holder as ReceiveViewHolder).bindReceive(item)
        }
    }

    override fun getItemCount(): Int {
        return oldList.size
    }

    override fun getItemViewType(position: Int): Int {
        return getViewType(oldList[position]?.currentUser ?: "")
    }

    inner class SendViewHolder(val sBinding: SentLayoutBinding) :
        ViewHolder(sBinding.root) {
        fun bindSend(message: Message?) {
            sBinding.messageTV.text = message?.content
        }
    }

    inner class ReceiveViewHolder(val rBinding: ReceiveLayoutBinding) :
        ViewHolder(rBinding.root) {
        fun bindReceive(message: Message?) {
            rBinding.messageTV.text = message?.content
        }
    }

    private fun getViewType(id: String): Int {
        return if (id == SharedPrefs.setUserCredential) {
            VIEW_TYPE_SEND
        } else {
            VIEW_TYPE_RECEIVE
        }
    }

    fun setData(newList: List<Message?>) {
        val diffUtil = MessageDffUtil(oldList, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        oldList = newList
        diffResult.dispatchUpdatesTo(this)
    }

}