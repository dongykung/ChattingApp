package com.example.chattingapp.ChatList

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.chattingapp.Key
import com.example.chattingapp.R
import com.example.chattingapp.convertChatTime
import com.example.chattingapp.databinding.ItemChatlistBinding
import java.text.SimpleDateFormat
import java.util.Calendar

class ChatListAdatper(private val onclick : (ChatRoomItem) -> Unit):ListAdapter<ChatRoomItem,ChatListAdatper.ChatListViewHolder>(diffUtil) {

    inner class ChatListViewHolder(private val binding : ItemChatlistBinding) : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SimpleDateFormat")
        fun bind(item : ChatRoomItem){
            val nowTime= item.lastMessageTime
            if(nowTime!=null){
                val timetext= convertChatTime(nowTime)
                val currentDate = Calendar.getInstance()
                currentDate.timeInMillis = System.currentTimeMillis()
                val result = when {
                    isSameDay(currentDate, Calendar.getInstance()) -> timetext
                    isSameDay(currentDate, getYesterday()) -> "어제"
                    else -> SimpleDateFormat("MM월dd일").format(currentDate.time)
                }
                binding.lastMessageTimeText.text=result
            }
            binding.friendName.text=item.otheruserName
            binding.chatroomLastmessage.text=item.lastMessage
            val userinfo = Key.friendInfo.find { item.otheruserUid==it.userUid }
            binding.chatroomProfileImageView.load(userinfo?.profileurl){
                placeholder(R.drawable.customcircle)
                crossfade(true)
                transformations(RoundedCornersTransformation(5.0f))
            }
            binding.clickchatlist.setOnClickListener{
                onclick(item)
            }
        }
    }

    companion object{
        val diffUtil = object : DiffUtil.ItemCallback<ChatRoomItem>(){
            override fun areItemsTheSame(oldItem: ChatRoomItem, newItem: ChatRoomItem): Boolean {
               return oldItem.chatRoomId == newItem.chatRoomId
            }
            override fun areContentsTheSame(oldItem: ChatRoomItem, newItem: ChatRoomItem): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        return ChatListViewHolder(ItemChatlistBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
    private fun isSameDay(calendar1: Calendar, calendar2: Calendar): Boolean {
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
                calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH)
    }

    // 어제의 날짜를 얻는 함수
    private fun getYesterday(): Calendar {
        val yesterday = Calendar.getInstance()
        yesterday.add(Calendar.DAY_OF_MONTH, -1)
        return yesterday
    }
}