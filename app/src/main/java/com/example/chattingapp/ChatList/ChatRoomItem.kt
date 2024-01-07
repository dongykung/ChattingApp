package com.example.chattingapp.ChatList

data class ChatRoomItem (
    val chatRoomId:String?=null,
    val lastMessage:String?=null,
    val lastMessageTime:Long?=null,
    val otheruserName:String?=null,
    val otheruserUid : String?=null,
    val otheruserprofileurl:String?=null
)