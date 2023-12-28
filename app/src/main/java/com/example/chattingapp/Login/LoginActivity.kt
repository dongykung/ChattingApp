package com.example.chattingapp.Login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.chattingapp.MainActivity
import com.example.chattingapp.R
import com.example.chattingapp.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    var email:String?=null
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
            email=binding.loginEmail.text.toString()
            password=binding.loginPassword.text.toString()
            if(checkLogin()){
                auth.signInWithEmailAndPassword(email!!,password!!)
                    .addOnCompleteListener(this){task->
                        if(task.isSuccessful){
                            Toast.makeText(baseContext, R.string.loginsuccess, Toast.LENGTH_SHORT,).show()
                            val intent= Intent(this,MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }else{
                            Toast.makeText(baseContext, R.string.loginfail, Toast.LENGTH_SHORT,).show()
                            Log.e("LoginActivity",task.exception.toString())
                        }
                    }
            }
        }


    }
    private fun checkLogin():Boolean{
        return !email.isNullOrEmpty()&&!password.isNullOrEmpty()
    }
}