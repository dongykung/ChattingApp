package com.example.chattingapp.ChatList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chattingapp.Key.Companion.DB_USERS
import com.example.chattingapp.UserList.UserAdapter
import com.example.chattingapp.UserList.UserItem
import com.example.chattingapp.databinding.FragmentChatlistBinding
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class ChatListFragment:Fragment() {
    private lateinit var binding : FragmentChatlistBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding  = FragmentChatlistBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val chatlistAdapter = ChatListAdatper()
        binding.chatRoomRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatlistAdapter
        }

        chatlistAdapter.submitList(mutableListOf<ChatRoomItem>().apply {
            add(ChatRoomItem("11","22","33"))
        })


    }



}