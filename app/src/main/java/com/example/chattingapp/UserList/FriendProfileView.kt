package com.example.chattingapp.UserList

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.chattingapp.R
import com.example.chattingapp.databinding.BottomsheetFriendprofileviewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FriendProfilebottomsheet(private val friendinfo:UserItem):BottomSheetDialogFragment() {
    private lateinit var binding : BottomsheetFriendprofileviewBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomsheetFriendprofileviewBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val displayMetrics = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val peekHeight = (height * 1.0).toInt()
        dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?.layoutParams?.height = peekHeight
        dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?.let { bottomSheet ->
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.isDraggable = true
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        loadFriendInfo()
    }
    private fun loadFriendInfo(){
        binding.closefriendProfileButton.setOnClickListener{
            dismiss()
        }
        binding.friendProfileName.text=friendinfo.username
        binding.friendProfileImage.load(friendinfo.profileurl){
            placeholder(R.drawable.customcircle)
            crossfade(true)
            transformations(RoundedCornersTransformation(5.0f))
        }
        binding.friendbackgroundProfile.load(friendinfo.backgroundurl){
            placeholder(R.drawable.customcircle)
            crossfade(true)
            transformations(RoundedCornersTransformation(5.0f))
        }
    }
}

interface friendProfileView{
    fun clickfriendProfile(friendinfo : UserItem)
}