package com.example.chattingapp.Login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.example.chattingapp.Key
import com.example.chattingapp.Key.Companion.DEFAULT_BACKGROUNDIMAGE
import com.example.chattingapp.Key.Companion.DEFAULT_PROFILEIMAGE
import com.example.chattingapp.R
import com.example.chattingapp.databinding.ActivitySignUpBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.database

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignUpBinding
    private lateinit var auth : FirebaseAuth
    var phoneNumber:String?=null
    var userId:String?=null
    var password:String?=null
    var userName:String?=null
    var userEmail:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        auth = Firebase.auth
        setContentView(binding.root)

        val toolbar = binding.topAppBar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.usersignupButton.setOnClickListener {
            phoneNumber=binding.phoneNumberEditText.text.toString()
            userId=binding.idEditText.text.toString()
            password=binding.passwordEditText.text.toString()
            userName=binding.userNameEditText.text.toString()
            userEmail=binding.emailEditText.text.toString()
            if(checkSignup()){
                auth.createUserWithEmailAndPassword(userEmail!!,password!!)
                    .addOnCompleteListener(this){task->
                        val currentUser = auth.currentUser
                        if(task.isSuccessful&&currentUser!=null){

                            val user = mutableMapOf<String,Any>()
                            user["userUid"] = currentUser.uid
                            user["userId"]=userId!!
                            user["username"] = userName!!
                            user["password"] = password!!
                            user["useremail"] = userEmail!!
                            user["userphonenumber"]=phoneNumber!!
                            user["profileurl"]=DEFAULT_PROFILEIMAGE
                            user["backgroundurl"]= DEFAULT_BACKGROUNDIMAGE
                            user["statusMessage"]=""
                            Firebase.database(Key.DB_URL).reference.child(Key.DB_USERS).child(currentUser.uid).updateChildren(user).addOnCompleteListener{
                                Toast.makeText(this@SignUpActivity,R.string.signupsuccess,Toast.LENGTH_SHORT).show()
                                finish()
                            }.addOnFailureListener{  Toast.makeText(this@SignUpActivity,R.string.signupfail,Toast.LENGTH_SHORT).show()}

                        }else{
                            Toast.makeText(this@SignUpActivity,R.string.signupfail,Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            else{
                Snackbar.make(binding.root,R.string.signuperror,Snackbar.LENGTH_SHORT).show()
            }
        }


    }

    private fun checkSignup():Boolean{
        if(phoneNumber?.isNotEmpty() == true && userId?.isNotEmpty() == true&&password?.isNotEmpty()==true
            &&userName?.isNotEmpty()==true&&userEmail?.isNotEmpty()==true){
            return true
        }
        return false
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
}