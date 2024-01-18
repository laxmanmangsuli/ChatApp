package com.example.chatapp.presentationlayer.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.chatapp.data.Message
import com.example.chatapp.data.User
import com.example.chatapp.presentationlayer.view.ChatActivity
import com.example.chatapp.utils.UserDiffUtil
import com.example.chatapps.databinding.ItemUserBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OtherUserAdapter(private val context: Context) : RecyclerView.Adapter<OtherUserAdapter.UserViewHolder>() {
    private var oldList = mutableListOf<User>()

    private val userLastMessages = mutableMapOf<String, Message?>()
    private val userUnreadMessageCounts = mutableMapOf<String, Int>()


    class UserViewHolder(val binding: ItemUserBinding):ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
            return UserViewHolder(ItemUserBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }

        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
            val item = oldList[position]

//            val unreadCount = userUnreadMessageCounts[item.userid] ?: 0
//            holder.binding.tvUnreadMessage.text = unreadCount.toString()
            holder.binding.tvUnreadMessage.text = item.unreadMessageCount.toString()

//            if (item.unreadMessageCount > 0){
//                holder.binding.constraintLayoutUnreadMessage.visibility = View.VISIBLE
//                holder.binding.tvUnreadMessage.text = item.unreadMessageCount.toString()
//            }else{
//                holder.binding.constraintLayoutUnreadMessage.visibility = View.GONE
//            }

            holder.binding.usernameTV.text = item.username
            val lastMessage = userLastMessages[item.userid]
            if (lastMessage?.type == "img"){
                holder.binding.lastMsgTV.text = "Photo"
            }else{
                holder.binding.lastMsgTV.text = lastMessage?.content ?:""
            }
            holder.itemView.setOnClickListener {
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("userid",item.userid)
                intent.putExtra("username",item.username)
                context.startActivity(intent)
            }
        }

    override fun getItemCount(): Int {
        return oldList.size
    }

    fun setData(newList: MutableList<User>){
        val diffUtil = UserDiffUtil(oldList,newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        oldList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    fun setLastMessage(user: User, lastMessage: Message?) {
        userLastMessages[user.userid] = lastMessage
        CoroutineScope(Dispatchers.Main).launch {
            notifyDataSetChanged()
        }
    }

    fun setTotalMessages(userId: String, unreadMessage: Int) {
        val userIndex = oldList.indexOfFirst { it.userid == userId }
        if (userIndex != -1) {
            oldList[userIndex].unreadMessageCount = unreadMessage
            notifyItemChanged(userIndex)
        }
    }

}

//fun updateUnreadMessageCount(userId: String, unreadMessageCount: Int) {
//    val userToUpdate = oldList.find { it.userid == userId }
//    userToUpdate?.unreadMessageCount = unreadMessageCount
//    notifyItemChanged(oldList.indexOf(userToUpdate))
//}


