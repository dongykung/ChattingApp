package com.example.chattingapp.SetProfile

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.chattingapp.Key.Companion.userInfo
import com.example.chattingapp.databinding.BottomsheetMyprofileviewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MyProfileView :BottomSheetDialogFragment(){
    private lateinit var binding : BottomsheetMyprofileviewBinding
    var ImageUri : Uri ?= null
    var checkprofile : Boolean =false
    var checkbackground: Boolean =false
    val loadImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        updateImage(it)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomsheetMyprofileviewBinding.inflate(inflater,container,false)
        binding.userProfileName.text=userInfo.username
        binding.closeProfileButton.setOnClickListener{
            dismiss()
        }

        binding.userProfileEditButton.setOnClickListener {
            EditProfileMode()
        }
        binding.editProfileCancel.setOnClickListener{
            CancelEditMode()
        }
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
      openscrollBottomSheet()
    }

    private fun CancelEditMode(){
        binding.closeProfileButton.isVisible=true
        binding.userProfileChatmeButton.isVisible=true
        binding.userProfileEditButton.isVisible=true

        binding.editProfileCancel.isVisible=false
        binding.addProfilePhoto.isVisible=false
        binding.editComplete.isVisible=false
        binding.editPorfileImageButton.isVisible=false
        binding.editBackgroundButton.isVisible=false
        openscrollBottomSheet()
    }
    private fun EditProfileMode(){
        //프로필 편집모드 투명도 false
        binding.closeProfileButton.isVisible=false
        binding.userProfileChatmeButton.isVisible=false
        binding.userProfileEditButton.isVisible=false
        //편집에 필요한 ui 투명도 true
        binding.editProfileCancel.isVisible=true
        binding.addProfilePhoto.isVisible=true
        binding.editComplete.isVisible=true
        binding.editPorfileImageButton.isVisible=true
        binding.editBackgroundButton.isVisible=true
        binding.editPorfileImageButton.setOnClickListener {
            checkprofile = true
            checkbackground=false
            checkPermission()
        }
        binding.editBackgroundButton.setOnClickListener {
            checkprofile = false
            checkbackground=true
            checkPermission()
        }
        lockscrollBottomSheet()
    }
    private fun openscrollBottomSheet(){
        dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?.let { bottomSheet ->
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.isDraggable = true
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
    }
    private fun lockscrollBottomSheet(){
        dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?.let { bottomSheet ->
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.isDraggable = false
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
    }

    private fun checkPermission(){
        when{
            ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ->{
                loadImage()
            }
            shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) ->{
                showPermissionInfoDialog("no")
            }
            ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED->{
                showPermissionInfoDialog("first")
            }
            else ->{
                requestReadExternalStorage()
            }
        }
    }

    private fun showPermissionInfoDialog(type:String){
        AlertDialog.Builder(requireContext()).apply {
            setMessage("이미지를 가져오기 위해서, 외부 저장소 읽기 권한이 필요합니다")
            setNegativeButton("취소",null)
            setPositiveButton("동의") { _, _ ->
                if(type=="first")
                    requestReadExternalStorage()
                else{
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package",context.packageName,null))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
        }.show()
    }
    private fun requestReadExternalStorage(){
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),100)
    }

    private fun loadImage(){
        val intent = Intent(Intent.ACTION_GET_CONTENT).setType("image/*")
       loadImageLauncher.launch(intent)
    }
    private fun updateImage(resultData: ActivityResult){
        if(resultData.resultCode== AppCompatActivity.RESULT_OK&&resultData.data!=null){
            ImageUri = resultData.data?.data
            try {
                ImageUri?.let {
                    if(Build.VERSION.SDK_INT<28){
                        val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,ImageUri)
                        if(checkprofile){ binding.userProfileImage.setImageBitmap(bitmap)}
                        else if(checkbackground){binding.userbackgroundProfile.setImageBitmap(bitmap)}
                    }else{
                        val source =ImageDecoder.createSource(requireActivity().contentResolver,ImageUri!!)
                        val bitmap =ImageDecoder.decodeBitmap(source)
                        if(checkprofile){binding.userProfileImage.setImageBitmap(bitmap)}
                        else if(checkbackground){ binding.userbackgroundProfile.setImageBitmap(bitmap)}
                    }
                }
            }catch (e:Exception){
                Toast.makeText(requireActivity(), "사진 로드 실패", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(requireActivity(), "사진 선택 취소", Toast.LENGTH_LONG).show();
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            100 ->{
                val resultcode = grantResults.firstOrNull() ?: PackageManager.PERMISSION_DENIED
                if(resultcode==PackageManager.PERMISSION_GRANTED){
                    loadImage()
                }
            }
        }
    }
}

interface seeMyProfile{
    fun clickseeMyProfile()
}