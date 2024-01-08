package com.example.chattingapp.ChatList

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chattingapp.ChatRoom.ChatActivity
import com.example.chattingapp.Key
import com.example.chattingapp.Key.Companion.DB_USERS
import com.example.chattingapp.Key.Companion.userInfo
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
    private val userUid = userInfo.userUid
    private lateinit var chatlistAdapter :ChatListAdatper
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
        chatlistAdapter = ChatListAdatper(onclick = {
            val intent = Intent(requireContext(), ChatActivity::class.java)
            intent.putExtra(ChatActivity.EXTRA_CHAT_ROOM_ID,it.chatRoomId)
            intent.putExtra(ChatActivity.EXTRA_OTHER_USER_UID, it.otheruserUid)
            intent.putExtra(ChatActivity.EXTRA_OTHER_USER_NAME,it.otheruserName)
            intent.putExtra(ChatActivity.EXTRA_OTHER_UESR_PROFILEURL,it.otheruserprofileurl)
            startActivity(intent)
        })
        binding.chatRoomRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatlistAdapter
        }
        loadChatLlist()
    }

    private fun loadChatLlist(){
        val ChatRoomsDB = Firebase.database.reference.child(Key.DB_CHAT_ROOMS).child(userUid!!)
        ChatRoomsDB.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatRoomList = snapshot.children.map {
                    it.getValue(ChatRoomItem::class.java)
                }
                chatRoomList.sortedByDescending{ it?.lastMessageTime }
                chatlistAdapter.submitList(chatRoomList)
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }


}