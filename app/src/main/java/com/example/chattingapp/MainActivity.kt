package com.example.chattingapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.chattingapp.ChatList.ChatListFragment
import com.example.chattingapp.Login.LoginActivity
import com.example.chattingapp.SetProfile.MyProfileView
import com.example.chattingapp.SetProfile.seeMyProfile
import com.example.chattingapp.UserList.UserFragment
import com.example.chattingapp.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity(),seeMyProfile {
    private lateinit var binding : ActivityMainBinding
    private lateinit var auth : FirebaseAuth
    private var userFragment = UserFragment(this)
    private var chatFragment = ChatListFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth= Firebase.auth
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUser = Firebase.auth.currentUser
        if(currentUser==null){
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }



        replaceFragment(userFragment)
        binding.homeBottomNavigation.setOnItemSelectedListener {
            when(it.itemId){
                R.id.action_home ->{
                    replaceFragment(userFragment)
                    binding.topAppBar.title="친구"
                    return@setOnItemSelectedListener true
                }
                R.id.action_chatting ->{
                    replaceFragment(chatFragment)
                    binding.topAppBar.title="채팅"
                    return@setOnItemSelectedListener true
                }
                R.id.action_more ->{
                    return@setOnItemSelectedListener true
                }
                else -> {
                    return@setOnItemSelectedListener false
                }
            }
        }



    }


    //FramgeLayout에 fragment를 교체해주는 함수
    private fun replaceFragment(fragment: Fragment){
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.mainFrame,fragment)
                commit()
            }
    }

    override fun clickseeMyProfile() {
        val myProfileBottomSheet = MyProfileView()
        myProfileBottomSheet.show(supportFragmentManager, "Test")
    }

}