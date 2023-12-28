package com.example.chattingapp.ChatList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chattingapp.databinding.ItemChatlistBinding

class ChatListAdatper:ListAdapter<ChatRoomItem,ChatListAdatper.ChatListViewHolder>(diffUtil) {

    inner class ChatListViewHolder(private val binding : ItemChatlistBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(item : ChatRoomItem){
            binding.friendName.text=item.friendName
            binding.chatroomLastmessage.text=item.lastMessage
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
}