package com.example.chattingapp.SetProfile

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.example.chattingapp.Key
import com.example.chattingapp.Key.Companion.userInfo
import com.example.chattingapp.R
import com.example.chattingapp.databinding.BottomsheetStatusmessageBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database

class MyStatusMessage(private val updateui : ()->Unit) : BottomSheetDialogFragment() {
    private lateinit var binding : BottomsheetStatusmessageBinding
    private lateinit var statusEditText : EditText
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomsheetStatusmessageBinding.inflate(inflater,container,false)
        statusEditText=binding.statusMessageEditText

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        statusEditText.setText(userInfo.statusMessage)
        statusEditText.addTextChangedListener {
            if(userInfo.statusMessage.equals(it.toString())){
                completeButtonDisabled()
            }else{
                completeButtonActivate(it.toString())
            }
        }
    }

    private fun completeButtonActivate(message:String){
        binding.completeChangeStatusButton.apply {
           setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey3))
            setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
            setOnClickListener{
                val currentuser = Firebase.auth.currentUser
                val userInfoUpdate = mutableMapOf<String, Any>()
                userInfoUpdate["statusMessage"] = message
                if(currentuser!=null){
                    Firebase.database(Key.DB_URL).reference.child(Key.DB_USERS).child(currentuser.uid).updateChildren(userInfoUpdate)
                        .addOnSuccessListener{
                            userInfo.statusMessage = message
                            updateui()
                            dismiss()
                      }
                }
            }
        }
    }
    private fun completeButtonDisabled(){
        binding.completeChangeStatusButton.apply {
            setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey))
            setTextColor(ContextCompat.getColor(requireContext(),R.color.grey2))
            setOnClickListener(null)
        }
    }
}

interface ChangeMyStatusMessage{
    fun changeStatusMessage()
}