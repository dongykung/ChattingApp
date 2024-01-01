package com.example.chattingapp

import com.example.chattingapp.UserList.UserItem

class Key {
    companion object{
        const val DB_URL ="https://chattingapp-c1231-default-rtdb.firebaseio.com/"
        const val DB_USERS = "Users"
        const val DB_CHAT_ROOMS="ChatRooms"
        const val DB_CHATS = "Chats"
        lateinit var userInfo : UserItem

        const val DEFAULT_PROFILEIMAGE = "https://firebasestorage.googleapis.com/v0/b/chattingapp-c1231.appspot.com/o/defaultImage%2Fdefaultimage.png?alt=media&token=6767ac5e-2e00-4caa-9c4d-dc6b513f6c76"
    }
}