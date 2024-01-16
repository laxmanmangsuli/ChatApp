package com.example.chatapp.presentationlayer.adapter

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.chatapp.data.Message
import com.example.chatapp.utils.MessageDffUtil
import com.example.chatapp.utils.SharedPrefs
import com.example.chatapps.databinding.ItemDateHeaderLayoutBinding
import com.example.chatapps.databinding.ReceiveLayoutBinding
import com.example.chatapps.databinding.ReceiveLayoutImageBinding
import com.example.chatapps.databinding.SentLayoutBinding
import com.example.chatapps.databinding.SentLayoutImageBinding
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


class MessageAdapter : RecyclerView.Adapter<ViewHolder>() {

    private var oldList = emptyList<Message?>()

    private var isText = false
    private var isTextOther = false
    private var isImage = false
    private var isImageOther = false

    companion object {
        const val VIEW_TYPE_SEND = 1
        const val VIEW_TYPE_RECEIVE = 2
        const val VIEW_TYPE_SEND_IMAGE = 3
        const val VIEW_TYPE_RECEIVE_IMAGE = 4
        const val VIEW_TYPE_DATE_HEADER = 5
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

            VIEW_TYPE_SEND_IMAGE -> SendViewImageViewHolder(
                SentLayoutImageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            VIEW_TYPE_RECEIVE_IMAGE -> ReceiveImageViewHolder(
                ReceiveLayoutImageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            VIEW_TYPE_DATE_HEADER -> DateHeaderViewHolder(
                ItemDateHeaderLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = oldList[position]

        when (holder.itemViewType) {
            VIEW_TYPE_SEND -> (holder as SendViewHolder).bindSend(item)
            VIEW_TYPE_RECEIVE -> (holder as ReceiveViewHolder).bindReceive(item)
            VIEW_TYPE_SEND_IMAGE -> (holder as SendViewImageViewHolder).bindImageSend(item)
            VIEW_TYPE_RECEIVE_IMAGE -> (holder as ReceiveImageViewHolder).bindImageReceive(item)
            VIEW_TYPE_DATE_HEADER -> (holder as DateHeaderViewHolder).bindDateHeader(item)
        }
    }

    override fun getItemCount(): Int {
        return oldList.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getItemViewType(position: Int): Int {
        val currentUser = oldList[position]?.currentUser ?: ""
        val messageType = oldList[position]?.type ?: ""

        return getViewType(currentUser, messageType, position)
    }

    inner class SendViewHolder(val sBinding: SentLayoutBinding) :
        ViewHolder(sBinding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bindSend(message: Message?) {
            sBinding.messageTV.text = message?.content
            val time = message?.time?.let { convertMillisToHourMinuteFormatWithAMPM(it) }
            sBinding.tvTime.text = time
        }
    }

    inner class ReceiveViewHolder(val rBinding: ReceiveLayoutBinding) :
        ViewHolder(rBinding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bindReceive(message: Message?) {
            rBinding.messageTV.text = message?.content
            val time = message?.time?.let { convertMillisToHourMinuteFormatWithAMPM(it) }
            rBinding.tvTime.text = time
        }
    }

    inner class SendViewImageViewHolder(val sBinding: SentLayoutImageBinding) :
        ViewHolder(sBinding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bindImageSend(message: Message?) {
            val time = message?.time?.let { convertMillisToHourMinuteFormatWithAMPM(it) }
            sBinding.tvTime.text = time
            val imageUri = Uri.parse(message?.imageUri)
            Glide.with(sBinding.ivSender.context)
                .load(imageUri)
                .listener(object : RequestListener<Drawable> {
                    override fun onResourceReady(resource: Drawable, model: Any, target: com.bumptech.glide.request.target.Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        sBinding.progressBar.visibility = View.GONE
                        return false
                    }
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>, isFirstResource: Boolean): Boolean {
                        sBinding.progressBar.visibility = View.GONE
                        return false
                    }
                })
                .into(sBinding.ivSender)
        }
    }


    inner class ReceiveImageViewHolder(val rBinding: ReceiveLayoutImageBinding) :
        ViewHolder(rBinding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bindImageReceive(message: Message?) {
            val time = message?.time?.let { convertMillisToHourMinuteFormatWithAMPM(it) }
            rBinding.tvTime.text = time

            val imageUri = Uri.parse(message?.imageUri)
            try {
                Glide.with(rBinding.ivReceiver.context)
                    .load(imageUri)
                    .listener(object : RequestListener<Drawable> {
                        override fun onResourceReady(resource: Drawable, model: Any, target: com.bumptech.glide.request.target.Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                            rBinding.progressBar.visibility = View.GONE
                            return false
                        }
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>, isFirstResource: Boolean): Boolean {
                            rBinding.progressBar.visibility = View.GONE
                            return false
                        }
                    })
                    .into(rBinding.ivReceiver)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    inner class DateHeaderViewHolder(val dBinding: ItemDateHeaderLayoutBinding) :
        ViewHolder(dBinding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bindDateHeader(message: Message?) {
            if (isText) {
                dBinding.constraintLayout.visibility = View.VISIBLE
                val date = message?.time?.let { convertMillisToDate(it) }
                dBinding.tvDateHeader.text = date
                dBinding.messageTV.text = message?.content
                val time = message?.time?.let { convertMillisToHourMinuteFormatWithAMPM(it) }
                dBinding.tvTime.text = time
            } else if (isImage) {
                dBinding.constraintLayoutImageSend.visibility = View.VISIBLE
                val date = message?.time?.let { convertMillisToDate(it) }
                dBinding.tvDateHeader.text = date
                val time = message?.time?.let { convertMillisToHourMinuteFormatWithAMPM(it) }
                dBinding.tvTimeImage.text = time
                val imageUri = Uri.parse(message?.imageUri)
                try {
                    Glide.with(dBinding.ivSender.context)
                        .load(imageUri)
                        .listener(object : RequestListener<Drawable> {
                            override fun onResourceReady(resource: Drawable, model: Any, target: com.bumptech.glide.request.target.Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                                dBinding.progressBar.visibility = View.GONE
                                return false
                            }
                            override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>, isFirstResource: Boolean): Boolean {
                                dBinding.progressBar.visibility = View.GONE
                                return false
                            }
                        })
                        .into(dBinding.ivSender)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (isTextOther) {
                dBinding.constraintLayoutReceive.visibility = View.VISIBLE
                val date = message?.time?.let { convertMillisToDate(it) }
                dBinding.tvDateHeader.text = date
                dBinding.messageTvReceive.text = message?.content
                val time = message?.time?.let { convertMillisToHourMinuteFormatWithAMPM(it) }
                dBinding.tvTimeReceive.text = time
            } else if (isImageOther) {
                dBinding.constraintLayoutImageReceive.visibility = View.VISIBLE
                val date = message?.time?.let { convertMillisToDate(it) }
                dBinding.tvDateHeader.text = date
                val time = message?.time?.let { convertMillisToHourMinuteFormatWithAMPM(it) }
                dBinding.tvTimeImageReceive.text = time
                val imageUri = Uri.parse(message?.imageUri)
                try {
                    Glide.with(dBinding.ivReceiverImage.context)
                        .load(imageUri)
                        .listener(object : RequestListener<Drawable> {
                            override fun onResourceReady(resource: Drawable, model: Any, target: com.bumptech.glide.request.target.Target<Drawable>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                                dBinding.progressBarReceive.visibility = View.GONE
                                return false
                            }
                            override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Drawable>, isFirstResource: Boolean): Boolean {
                                dBinding.progressBar.visibility = View.GONE
                                return false
                            }
                        })
                        .into(dBinding.ivReceiverImage)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            isText = false
            isTextOther = false
            isImage = false
            isImageOther = false

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getViewType(id: String, messageType: String, position: Int): Int {
        return if (position == 0 || isDifferentDate(position)) {
            if (id == SharedPrefs.setUserCredential) {
                if (messageType == "txt") {
                    isText = true
                    VIEW_TYPE_DATE_HEADER
                } else {
                    isImage = true
                    VIEW_TYPE_DATE_HEADER
                }
            } else {
                if (messageType == "txt") {
                    isTextOther = true
                    VIEW_TYPE_DATE_HEADER
                } else {
                    isImageOther = true
                    VIEW_TYPE_DATE_HEADER
                }
            }
        } else {
            if (id == SharedPrefs.setUserCredential) {
                if (messageType == "txt") {
                    VIEW_TYPE_SEND
                } else {
                    VIEW_TYPE_SEND_IMAGE
                }
            } else {
                if (messageType == "txt") {
                    VIEW_TYPE_RECEIVE
                } else {
                    VIEW_TYPE_RECEIVE_IMAGE
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun isDifferentDate(position: Int): Boolean {
        if (position > 0) {
            val currentMessageTime = oldList[position]?.time ?: 0
            val previousMessageTime = oldList[position - 1]?.time ?: 0
            return !isSameDay(currentMessageTime, previousMessageTime)
        }
        return false
    }

    fun setData(newList: List<Message?>) {
        val diffUtil = MessageDffUtil(oldList, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        oldList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertMillisToHourMinuteFormatWithAMPM(milliseconds: Long): String {
        val instant = Instant.ofEpochMilli(milliseconds)
        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
        return localDateTime.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertMillisToDate(milliseconds: Long): String {
        val instant = Instant.ofEpochMilli(milliseconds)
        val localDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate()
        return if (isToday(localDate)) {
            "Today"
        } else {
            val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault())
            localDate.format(formatter)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isToday(date: LocalDate): Boolean {
        val today = LocalDate.now()
        return today.isEqual(date)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isSameDay(date1: Long, date2: Long): Boolean {
        val localDate1 =
            LocalDateTime.ofInstant(Instant.ofEpochMilli(date1), ZoneId.systemDefault())
                .toLocalDate()
        val localDate2 =
            LocalDateTime.ofInstant(Instant.ofEpochMilli(date2), ZoneId.systemDefault())
                .toLocalDate()
        return localDate1.isEqual(localDate2)
    }
}


//class MessageAdapter : RecyclerView.Adapter<ViewHolder>() {
//
//    private var oldList = emptyList<Message?>()
//
//    companion object {
//        const val VIEW_TYPE_SEND = 1
//        const val VIEW_TYPE_RECEIVE = 2
//        const val VIEW_TYPE_SEND_IMAGE = 3
//        const val VIEW_TYPE_RECEIVE_IMAGE = 4
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        return when (viewType) {
//            VIEW_TYPE_SEND -> SendViewHolder(
//                SentLayoutBinding.inflate(
//                    LayoutInflater.from(parent.context),
//                    parent,
//                    false
//                )
//            )
//
//            VIEW_TYPE_RECEIVE -> ReceiveViewHolder(
//                ReceiveLayoutBinding.inflate(
//                    LayoutInflater.from(parent.context),
//                    parent,
//                    false
//                )
//            )
//
//            VIEW_TYPE_SEND_IMAGE -> SendViewImageViewHolder(
//                SentLayoutImageBinding.inflate(
//                    LayoutInflater.from(parent.context),
//                    parent,
//                    false
//                )
//            )
//
//            VIEW_TYPE_RECEIVE_IMAGE -> ReceiveImageViewHolder(
//                ReceiveLayoutImageBinding.inflate(
//                    LayoutInflater.from(parent.context),
//                    parent,
//                    false
//                )
//            )
//
//            else -> throw IllegalArgumentException("Invalid view type")
//        }
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val item = oldList[position]
//
//        when (holder.itemViewType) {
//            VIEW_TYPE_SEND -> (holder as SendViewHolder).bindSend(item)
//            VIEW_TYPE_RECEIVE -> (holder as ReceiveViewHolder).bindReceive(item)
//            VIEW_TYPE_SEND_IMAGE -> (holder as SendViewImageViewHolder).bindImageSend(item)
//            VIEW_TYPE_RECEIVE_IMAGE -> (holder as ReceiveImageViewHolder).bindImageReceive(item)
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return oldList.size
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        val currentUser = oldList[position]?.currentUser ?: ""
//        val messageType = oldList[position]?.type ?: ""
//
//        return getViewType(currentUser, messageType)
//    }
//
//    inner class SendViewHolder(val sBinding: SentLayoutBinding) :
//        ViewHolder(sBinding.root) {
//        @RequiresApi(Build.VERSION_CODES.O)
//        fun bindSend(message: Message?) {
//                sBinding.messageTV.text = message?.content
//            val time=message?.time?.let { convertMillisToHourMinuteFormatWithAMPM(it) }
//            sBinding.tvTime.text = time
//
//        }
//    }
//
//    inner class ReceiveViewHolder(val rBinding: ReceiveLayoutBinding) :
//        ViewHolder(rBinding.root) {
//        @RequiresApi(Build.VERSION_CODES.O)
//        fun bindReceive(message: Message?) {
//            rBinding.messageTV.text = message?.content
//            val time=message?.time?.let { convertMillisToHourMinuteFormatWithAMPM(it) }
//            rBinding.tvTime.text = time
//        }
//    }
//
//    inner class SendViewImageViewHolder(val sBinding: SentLayoutImageBinding) :
//        ViewHolder(sBinding.root) {
//        @RequiresApi(Build.VERSION_CODES.O)
//        fun bindImageSend(message: Message?) {
//            Log.e("TAG", "bindImageSend: start", )
//            val time=message?.time?.let { convertMillisToHourMinuteFormatWithAMPM(it) }
//            sBinding.tvTime.text = time
//                val imageUri = Uri.parse(message?.imageUri)
//                Log.e("TAG", "bindImageSend: loading", )
//               try {
//                   Glide.with(sBinding.ivSender.context)
//                       .load(imageUri)
//                       .into(sBinding.ivSender)
//                   Log.e("TAG", "bindImageSend: Finish", )
//               }catch (e:Exception){
//                   e.printStackTrace()
//               }
//        }
//    }
//
//    inner class ReceiveImageViewHolder(val rBinding: ReceiveLayoutImageBinding) :
//        ViewHolder(rBinding.root) {
//        @RequiresApi(Build.VERSION_CODES.O)
//        fun bindImageReceive(message: Message?) {
//            val time=message?.time?.let { convertMillisToHourMinuteFormatWithAMPM(it) }
//            rBinding.tvTime.text = time
//                val imageUri = Uri.parse(message?.imageUri)
//                Log.e("TAG", "bindImageSend: $imageUri", )
//                Glide.with(rBinding.ivReceiver.context)
//                    .load(imageUri)
//                    .into(rBinding.ivReceiver)
//        }
//    }
//
//    private fun getViewType(id: String, messageType: String): Int {
//        return if (id == SharedPrefs.setUserCredential) {
//            if (messageType == "txt"){
//                VIEW_TYPE_SEND
//            }else{
//                VIEW_TYPE_SEND_IMAGE
//            }
//
//        } else {
//            if (messageType == "txt") {
//                VIEW_TYPE_RECEIVE
//            }else{
//                VIEW_TYPE_RECEIVE_IMAGE
//            }
//        }
//    }
//
//    fun setData(newList: List<Message?>) {
//        val diffUtil = MessageDffUtil(oldList, newList)
//        val diffResult = DiffUtil.calculateDiff(diffUtil)
//        oldList = newList
//        diffResult.dispatchUpdatesTo(this)
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    fun convertMillisToHourMinuteFormatWithAMPM(milliseconds: Long): String {
//        val instant = Instant.ofEpochMilli(milliseconds)
//        val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
//        val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.getDefault())
//        return localDateTime.format(formatter)
//    }
//
//}