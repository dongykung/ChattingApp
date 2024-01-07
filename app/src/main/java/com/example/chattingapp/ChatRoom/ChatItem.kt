package com.example.chattingapp.ChatRoom

data class ChatItem(
    var chatId:String?=null,
    var userUid:String?=null,
    var message:String?=null,
    var username:String?=null,
    var time:Long?=null
)
