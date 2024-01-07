package com.example.chattingapp.ChatRoom

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.chattingapp.Key.Companion.userInfo
import com.example.chattingapp.R
import com.example.chattingapp.convertChatTime
import com.example.chattingapp.databinding.ItemChatBinding
import com.example.chattingapp.databinding.ItemChatmeBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ChatAdapter(private val recyclerView: RecyclerView) : androidx.recyclerview.widget.ListAdapter<ChatItem, RecyclerView.ViewHolder>(diffUtil) {
    private val VIEW_TYPE_MYMESSAGE = 0
    private val VIEW_TYPE_YOUMESSAGE = 1
     var userprofileurl : String ?=""
    inner class MyMessageViewHolder(private val binding: ItemChatmeBinding) : RecyclerView.ViewHolder(binding.root) {
        // 내가 보낸 메시지에 대한 뷰홀더 처리 로직 구현
        fun bindMyMessage(chatItem: ChatItem,position: Int) {
            val nowTime = chatItem.time
            val recyclerView = recyclerView
            if (position > 0 && sameTimeMessage(currentList[position], currentList[position - 1])) {
                val previousViewHolder =
                    recyclerView.findViewHolderForAdapterPosition(position - 1) as? MyMessageViewHolder
                if (nowTime != null) {
                    binding.chatmetime.text = convertChatTime(nowTime)
                }
                binding.chatbubbleme.text = chatItem.message
                previousViewHolder?.binding?.chatmetime?.isVisible=false
            } else {

                if (nowTime != null) {
                    binding.chatmetime.text = convertChatTime(nowTime)
                }
                binding.chatbubbleme.text = chatItem.message
            }
        }
    }

    inner class OtherMessageViewHolder(private val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root) {
        // 상대방이 보낸 메시지에 대한 뷰홀더 처리 로직 구현
        fun bindOtherMessage(chatItem: ChatItem,position: Int) {
            Log.e("chattest2",chatItem.message.toString())
            val nowTime = chatItem.time
            if(position>0&&sameTimeMessage(currentList[position],currentList[position-1])){
                binding.chatuserImageView.isVisible=false
                binding.chatuserName.isVisible=false
                binding.chatbubbleyou.text=chatItem.message
                if(nowTime!=null) binding.chatyoutime.text=convertChatTime(nowTime)
                val previousViewHolder =
                    recyclerView.findViewHolderForAdapterPosition(position - 1) as? OtherMessageViewHolder
                previousViewHolder?.binding?.chatyoutime?.isVisible=false

            }else {
                if (nowTime != null) {
                    binding.chatyoutime.text = convertChatTime(nowTime)
                }
                binding.chatuserName.text = chatItem.username
                binding.chatbubbleyou.text = chatItem.message
                binding.chatuserImageView.load(userprofileurl) {
                    placeholder(R.drawable.customcircle)
                    crossfade(true)
                    transformations(RoundedCornersTransformation(15.0f))
                }
            }
        }
    }

    companion object {
        var diffUtil = object : DiffUtil.ItemCallback<ChatItem>() {
            override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
                return oldItem.chatId == newItem.chatId
            }

            override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
                return oldItem.userUid == newItem.userUid && oldItem.message == newItem.message
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentChat = getItem(position)
        return if (currentChat.userUid == userInfo.userUid) {
            VIEW_TYPE_MYMESSAGE
        } else {
            VIEW_TYPE_YOUMESSAGE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_MYMESSAGE -> {
                val binding = ItemChatmeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MyMessageViewHolder(binding)
            }
            VIEW_TYPE_YOUMESSAGE -> {
                val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                OtherMessageViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyMessageViewHolder -> {
                holder.bindMyMessage(getItem(position),position)
            }
            is OtherMessageViewHolder -> holder.bindOtherMessage(getItem(position),position)
        }
    }
    private fun sameTimeMessage(nowItem:ChatItem,previousItem:ChatItem):Boolean{
        return (nowItem.userUid==previousItem.userUid&&nowItem.time==previousItem.time)
    }
}
