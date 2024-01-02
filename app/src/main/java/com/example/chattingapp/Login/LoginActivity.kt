package com.example.chattingapp.Login

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.chattingapp.Key
import com.example.chattingapp.Key.Companion.DB_URL
import com.example.chattingapp.Key.Companion.DB_USERS
import com.example.chattingapp.Key.Companion.userInfo
import com.example.chattingapp.MainActivity
import com.example.chattingapp.R
import com.example.chattingapp.UserList.UserItem
import com.example.chattingapp.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.google.firebase.database.getValue

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    var useremail:String?=null
    var password:String?=null
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        auth= Firebase.auth
        setContentView(binding.root)
        //회원가입 버튼 리스너
        binding.signupButton.setOnClickListener {
            val intent = Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }

        //로그인버튼 리스너
        binding.loginButton.setOnClickListener {
            useremail=binding.loginEmail.text.toString()
            password=binding.loginPassword.text.toString()
            if(checkLogin()){
                auth.signInWithEmailAndPassword(useremail!!,password!!)
                    .addOnCompleteListener(this){task->
                        val currentuser = auth.currentUser
                        if(task.isSuccessful&&currentuser!=null){
                            Firebase.database(Key.DB_URL).reference.child(Key.DB_USERS).child(currentuser.uid).get().addOnSuccessListener {
                                val user = it.getValue<UserItem>()
                                if(user!=null){
                                    userInfo = user
                                    Toast.makeText(baseContext, R.string.loginsuccess, Toast.LENGTH_SHORT,).show()
                                    val intent= Intent(this,MainActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        }else{
                            Toast.makeText(baseContext, R.string.loginfail, Toast.LENGTH_SHORT,).show()
                            Log.e("LoginActivity",task.exception.toString())
                        }
                    }
            }
        }


    }
    private fun checkLogin():Boolean{
        return !useremail.isNullOrEmpty()&&!password.isNullOrEmpty()
    }
}