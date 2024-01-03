package com.example.chattingapp.UserList

import com.example.chattingapp.SetProfile.ChangeMyStatusMessage

data class UserItem(
    var password:String?=null,
    var profileurl:String?=null,
    var userId:String?=null,
    var userUid:String?=null,
    var useremail:String?=null,
    var username:String?=null,
    var userphonenumber:String?=null,
    var backgroundurl:String?=null,
    var statusMessage: String?=null
)
