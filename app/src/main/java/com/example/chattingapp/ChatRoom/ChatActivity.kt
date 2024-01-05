package com.example.chattingapp.ChatRoom

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chattingapp.Key
import com.example.chattingapp.Key.Companion.userInfo
import com.example.chattingapp.R
import com.example.chattingapp.UserList.UserItem
import com.example.chattingapp.databinding.ActivityChatBinding
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.database

class ChatActivity : AppCompatActivity() {
    private lateinit var binding : ActivityChatBinding
    private var chatRoomId : String =""
    private var otheruserId : String =""
    private var myuserUid :String = userInfo.userUid.toString()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.chattopAppBar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        chatRoomId = intent.getStringExtra("chatroomId")?:return
        otheruserId = intent.getStringExtra("otheruserId")?:return

        Firebase.database.reference.child(Key.DB_USERS).child(myuserUid).get()
            .addOnSuccessListener {
                val myuserItem = it.getValue(UserItem::class.java)
                val myuserName = myuserItem?.username
            }

        Firebase.database.reference.child(Key.DB_USERS).child(otheruserId).get()
            .addOnSuccessListener {
                val otheruserItem = it.getValue(UserItem::class.java )
            }

        Firebase.database.reference.child(Key.DB_CHATS).child(chatRoomId).addChildEventListener(object:ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
               val chatItem = snapshot.getValue(ChatItem::class.java)
                chatItem?:return

            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}

        })
         initRecyclerView()
    }
    private fun initRecyclerView(){
        binding.chatrecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                setResult(RESULT_OK)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}