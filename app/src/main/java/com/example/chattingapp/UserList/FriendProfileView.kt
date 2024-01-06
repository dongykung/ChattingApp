package com.example.chattingapp.UserList

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.chattingapp.ChatList.ChatRoomItem
import com.example.chattingapp.ChatRoom.ChatActivity
import com.example.chattingapp.ChatRoom.ChatItem
import com.example.chattingapp.Key
import com.example.chattingapp.Key.Companion.userInfo
import com.example.chattingapp.R
import com.example.chattingapp.databinding.BottomsheetFriendprofileviewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Firebase
import com.google.firebase.database.database
import java.util.UUID

class FriendProfilebottomsheet(private val friendinfo:UserItem):BottomSheetDialogFragment() {
    private lateinit var binding : BottomsheetFriendprofileviewBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = BottomsheetFriendprofileviewBinding.inflate(inflater,container,false)
        binding.friendProfileChatmeButton.setOnClickListener {
            val myuserUid = userInfo.userUid ?:""
            val otheruserUid = friendinfo.userUid ?:""
            val chatRoomDb = Firebase.database.reference.child(Key.DB_CHAT_ROOMS).child(myuserUid).child(otheruserUid)
            val chatotheruserRoomDB=Firebase.database.reference.child(Key.DB_CHAT_ROOMS).child(otheruserUid).child(myuserUid)
            chatRoomDb.get().addOnSuccessListener {
                Log.e("test",it.toString())
                var chatRoomId = ""
                if (it.value != null) {
                    val chatRoom = it.getValue(ChatRoomItem::class.java)
                    chatRoomId = chatRoom?.chatRoomId ?: ""
                } else {
                    chatRoomId = UUID.randomUUID().toString()
                    val newChatRoom = ChatRoomItem(
                        chatRoomId = chatRoomId,
                        lastMessage = "",
                        otheruserUid = otheruserUid,
                        otheruserName = friendinfo.username ?: "",
                        otheruserprofileurl = friendinfo.profileurl ?:""
                    )
                    val otheruserChatRoom = ChatRoomItem(
                        chatRoomId = chatRoomId,
                        lastMessage = "",
                        otheruserUid = myuserUid,
                        otheruserprofileurl = userInfo.profileurl,
                        otheruserName = userInfo.username
                    )
                    chatRoomDb.setValue(newChatRoom)
                }

                val intent = Intent(requireContext(),ChatActivity::class.java)
                intent.putExtra(ChatActivity.EXTRA_CHAT_ROOM_ID,chatRoomId)
                intent.putExtra(ChatActivity.EXTRA_OTHER_USER_UID, friendinfo.userUid)
                intent.putExtra(ChatActivity.EXTRA_OTHER_UESR_PROFILEURL,friendinfo.profileurl)
                intent.putExtra(ChatActivity.EXTRA_OTHER_USER_NAME,friendinfo.username)
                startActivity(intent)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val peekHeight = (height * 1.0).toInt()
        dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?.layoutParams?.height = peekHeight
        dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?.let { bottomSheet ->
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.isDraggable = true
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        loadFriendInfo()
    }
    private fun loadFriendInfo(){
        binding.closefriendProfileButton.setOnClickListener{
            dismiss()
        }
        binding.friendProfileName.text=friendinfo.username
        binding.friendProfileImage.load(friendinfo.profileurl){
            placeholder(R.drawable.customcircle)
            crossfade(true)
            transformations(RoundedCornersTransformation(5.0f))
        }
        binding.friendbackgroundProfile.load(friendinfo.backgroundurl){
            placeholder(R.drawable.customcircle)
            crossfade(true)
            transformations(RoundedCornersTransformation(5.0f))
        }
    }
}

interface friendProfileView{
    fun clickfriendProfile(friendinfo : UserItem)
}