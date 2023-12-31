package com.example.chattingapp.ChatRoom

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chattingapp.ChatList.ChatRoomItem
import com.example.chattingapp.Key
import com.example.chattingapp.Key.Companion.DB_CHATS
import com.example.chattingapp.Key.Companion.DB_CHAT_ROOMS
import com.example.chattingapp.Key.Companion.userInfo
import com.example.chattingapp.R
import com.example.chattingapp.UserList.UserItem
import com.example.chattingapp.convertMillsSecond
import com.example.chattingapp.databinding.ActivityChatBinding
import com.example.chattingapp.isScrollable
import com.example.chattingapp.setStackFromEnd
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.database
import java.text.SimpleDateFormat

class ChatActivity : AppCompatActivity() {
    private lateinit var binding : ActivityChatBinding
    private var chatRoomId : String =""
    private var otheruserId : String =""
    private var chatusername :String=""
    private var otherprofileurl:String=""
    private var myuserUid :String = userInfo.userUid.toString()
    private var chatItemList = mutableListOf<ChatItem>( )
    private lateinit var chatRecyclerView : RecyclerView
    private var isOpen = false // 키보드 올라왔는지 확인
private lateinit var chatAdapter : ChatAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
       // window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        val toolbar = binding.chattopAppBar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.chatsendButton.setOnClickListener{
            uploadChat()
        }
        binding.chatedidttextlayout.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            binding.chatedidttextlayout.getWindowVisibleDisplayFrame(rect)
            val rootViewHeight= binding.chatedidttextlayout.rootView.height
            val heightDiff = rootViewHeight-rect.height()
            isOpen = heightDiff > rootViewHeight * 0.25 // true == 키보드 올라감
        }
        chatRecyclerView=binding.chatrecyclerView
        chatAdapter =ChatAdapter(chatRecyclerView)

        //intent로 넘어오는 값들
        chatRoomId = intent.getStringExtra(EXTRA_CHAT_ROOM_ID)?:return
        otheruserId = intent.getStringExtra(EXTRA_OTHER_USER_UID)?:return
        chatusername = intent.getStringExtra(EXTRA_OTHER_USER_NAME)?:return
        otherprofileurl = intent.getStringExtra(EXTRA_OTHER_UESR_PROFILEURL)?:return


        //툴바 타이틀에 친구이름 넣기
        toolbar.title=chatusername
//        Firebase.database.reference.child(Key.DB_USERS).child(myuserUid).get()
//            .addOnSuccessListener {
//                val myuserItem = it.getValue(UserItem::class.java)
//                val myuserName = myuserItem?.username
//            }
//
//        Firebase.database.reference.child (Key.DB_USERS).child(otheruserId).get()
//            .addOnSuccessListener {
//                val otheruserItem = it.getValue(UserItem::class.java )
//
//                chatadapter.otherUserItem = otheruserItem
//            }
        //채팅 내용 가져오기리스너
        Firebase.database.reference.child(Key.DB_CHATS).child(chatRoomId).addChildEventListener(object:ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
               val chatItem = snapshot.getValue(ChatItem::class.java)
                chatItem?:return

                chatItemList.add(chatItem)
                chatAdapter.submitList(chatItemList)
                if(isOpen){
                    Log.e("키보드가 올라와 있다면 발동","키보드올라옴")
                    chatAdapter.notifyItemInserted(chatItemList.size - 1)
                }
                //chatAdapter.notifyDataSetChanged()
                scrollBottom()
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}

        })

        //리사이클러뷰 초기화
         initRecyclerView()
        //상대방의 프사가 바뀌는지 확인

    }
    private fun initRecyclerView(){
        chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
            addOnLayoutChangeListener(onLayoutChangeListener)

            viewTreeObserver.addOnScrollChangedListener {
                if (isScrollable() && !isOpen) { // 스크롤이 가능하면서 키보드가 닫힌 상태일 떄만
                    setStackFromEnd()
                    removeOnLayoutChangeListener(onLayoutChangeListener)
                }
            }
        }
    }
    private fun uploadChat(){
        val message = binding.chatEditText.text.toString()
        if(message.isNullOrEmpty()){
            Snackbar.make(binding.root,"빈 메시지를 전송할 수 없습니다", Snackbar.LENGTH_SHORT).show()
            return
        }
        val newchatItem = ChatItem(
            userUid = userInfo.userUid,
            message=message,
            username = userInfo.username,
            time = convertMillsSecond()
        )
        Firebase.database.reference.child(Key.DB_CHATS).child(chatRoomId).push().apply {
            newchatItem.chatId=key
            setValue(newchatItem)
        }
        val myInfoUpdate = mutableMapOf<String,Any>()
        myInfoUpdate["lastMessage"]=message
        myInfoUpdate["lastMessageTime"] = convertMillsSecond()
        Firebase.database.reference.child(Key.DB_CHAT_ROOMS).child(myuserUid).child(otheruserId).updateChildren(myInfoUpdate)
        val chatotheruserRoomDB=Firebase.database.reference.child(Key.DB_CHAT_ROOMS).child(otheruserId).child(myuserUid)
        chatotheruserRoomDB.get().addOnSuccessListener {
            if(it.value==null){
                val otheruserChatRoom = ChatRoomItem(
                    chatRoomId = chatRoomId,
                    lastMessage = message,
                    lastMessageTime= convertMillsSecond(),
                    otheruserUid = myuserUid,
                    otheruserprofileurl = userInfo.profileurl,
                    otheruserName = userInfo.username
                )
                chatotheruserRoomDB.setValue(otheruserChatRoom)
            }else{
                val userInfoUpdate = mutableMapOf<String, Any>()
                userInfoUpdate["lastMessage"] = message
                userInfoUpdate["lastMessageTime"]= convertMillsSecond()
                chatotheruserRoomDB.updateChildren(userInfoUpdate)
            }
        }

        binding.chatEditText.text.clear()
        scrollBottom()
    }
    private fun scrollBottom(){
        val lastItemPosition = chatAdapter.itemCount - 1
        if (lastItemPosition >= 0) {
            chatRecyclerView.smoothScrollToPosition(lastItemPosition)
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
    private val onLayoutChangeListener =
        View.OnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            // 키보드가 올라와 높이가 변함
            if (bottom < oldBottom) {
                chatRecyclerView.scrollBy(0, oldBottom - bottom) // 스크롤 유지를 위해 추가
            }
        }

    companion object{
        const val EXTRA_CHAT_ROOM_ID = "chatroomId"
        const val EXTRA_OTHER_USER_UID = "otheruserId"
        const val EXTRA_OTHER_USER_NAME ="otheruserName"
        const val EXTRA_OTHER_UESR_PROFILEURL ="otheruserProfileUrl"
    }
}