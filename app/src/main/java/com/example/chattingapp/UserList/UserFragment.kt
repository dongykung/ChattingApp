package com.example.chattingapp.UserList

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.chattingapp.Key
import com.example.chattingapp.Key.Companion.userInfo
import com.example.chattingapp.R
import com.example.chattingapp.SetProfile.MyProfileView
import com.example.chattingapp.SetProfile.seeMyProfile
import com.example.chattingapp.databinding.FragmentUserlistBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class UserFragment(private val listener : seeMyProfile):Fragment() {
    private lateinit var binding : FragmentUserlistBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var userlistAdapter : UserAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserlistBinding.inflate(inflater,container,false)
        auth = Firebase.auth
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("test","Test")
         userlistAdapter = UserAdapter()
        binding.userlistRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter =userlistAdapter
        }
        binding.userName.text = userInfo.username

        binding.userProfileView.setOnClickListener{
            listener.clickseeMyProfile()
        }
        binding.userProfileImageView.load(userInfo.profileurl){
            placeholder(R.drawable.customcircle)
            crossfade(true)
            transformations(RoundedCornersTransformation(5.0f))
        }

        loadUserList()

    }

    private fun loadUserList(){
        Firebase.database.reference.child(Key.DB_USERS).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.e("userlist",snapshot.toString())

                val userItemList = mutableListOf<UserItem>()
                snapshot.children.forEach{
                   val user = it.getValue(UserItem::class.java)
                   user?:return
                    if(user.userUid!=userInfo.userUid){
                        userItemList.add(user)
                    }
                }
                userlistAdapter.submitList(userItemList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("userlist",error.toString())
            }

        })
    }
}