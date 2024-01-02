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
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.chattingapp.Key
import com.example.chattingapp.Key.Companion.userInfo
import com.example.chattingapp.R
import com.example.chattingapp.databinding.BottomsheetMyprofileviewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Firebase
import com.google.firebase.app
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.google.firebase.storage.storageMetadata

class MyProfileView(private val updatemyInfo : () -> Unit) :BottomSheetDialogFragment(){
    private lateinit var binding : BottomsheetMyprofileviewBinding
    private lateinit var stroage :FirebaseStorage
    private var profileImageUrl :Uri = Uri.parse(userInfo.profileurl)
    private var backgroundImageUrl :Uri = Uri.parse(userInfo.backgroundurl)
    var ImageUri : Uri ?= null
    var checkprofile : Boolean =false
    var checkbackground: Boolean =false
    var updatedProfile : Boolean = false
    var updatebackground : Boolean = false
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
        stroage = Firebase.storage
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
        //바텀시트 스크롤 가능하게 하는 함수호출
      openscrollBottomSheet()

        //뷰가 생성될 때 유저 프로필,배경사진 로드하는 함수
        loadUserInfo()

    }


    //프로필 수정 모드 해제
    private fun CancelEditMode(){
        binding.closeProfileButton.isVisible=true
        binding.userProfileChatmeButton.isVisible=true
        binding.userProfileEditButton.isVisible=true

        binding.editProfileCancel.isVisible=false
        binding.addProfilePhoto.isVisible=false
        binding.editComplete.isVisible=false
        binding.editPorfileImageButton.isVisible=false
        binding.editBackgroundButton.isVisible=false

        updatedProfile=false
        updatebackground=false
        checkprofile =false
        checkbackground =false
        loadUserInfo()
        openscrollBottomSheet()
    }

    //프로필 수정
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
        //프로필 화면 변경 버튼 리스너
        binding.editPorfileImageButton.setOnClickListener {
            checkprofile = true
            checkbackground=false
            checkPermission()
        }
        //배경화면 변경 버튼 리스너
        binding.editBackgroundButton.setOnClickListener {
            checkprofile = false
            checkbackground=true
            checkPermission()
        }
        //변경 완료버튼 리스너
        binding.editComplete.setOnClickListener {
            UpdateUserInfo()
        }
        lockscrollBottomSheet()
    }
    //바텀시트 스크롤 가능하게하는 함수
    private fun openscrollBottomSheet(){
        dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?.let { bottomSheet ->
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.isDraggable = true
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
    }
    //바텀시트 스크롤 막게하는 함수
    private fun lockscrollBottomSheet(){
        dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            ?.let { bottomSheet ->
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.isDraggable = false
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
    }

    //권한체크
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

    //권한 다이얼로그 소개하는 함수
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
    //권한 요청하는 함수
    private fun requestReadExternalStorage(){
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),100)
    }

    //이미지 로드해오는 함수
    private fun loadImage(){
        val intent = Intent(Intent.ACTION_GET_CONTENT).setType("image/*")
       loadImageLauncher.launch(intent)
    }
    //로드해온 이미지 imageView에 적용하는 함수
    private fun updateImage(resultData: ActivityResult){
        if(resultData.resultCode== AppCompatActivity.RESULT_OK&&resultData.data!=null){
            ImageUri = resultData.data?.data
            try {
                ImageUri?.let {
                    if(Build.VERSION.SDK_INT<28){
                        val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,ImageUri)
                        if(checkprofile){ binding.userProfileImage.setImageBitmap(bitmap)
                            updatedProfile = true
                            profileImageUrl=ImageUri!!}
                        else if(checkbackground){binding.userbackgroundProfile.setImageBitmap(bitmap)
                            updatebackground = true
                            backgroundImageUrl= ImageUri!!}
                    }else{
                        val source =ImageDecoder.createSource(requireActivity().contentResolver,ImageUri!!)
                        val bitmap =ImageDecoder.decodeBitmap(source)
                        if(checkprofile){binding.userProfileImage.setImageBitmap(bitmap)
                            updatedProfile = true
                            profileImageUrl=ImageUri!!}
                        else if(checkbackground){ binding.userbackgroundProfile.setImageBitmap(bitmap)
                            updatebackground = true
                            backgroundImageUrl= ImageUri!!}
                    }
                }
            }catch (e:Exception){
                Toast.makeText(requireActivity(), "사진 로드 실패", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(requireActivity(), "사진 선택 취소", Toast.LENGTH_LONG).show();
        }
    }

    //유저 프로필, 배경화면 사진 로드하는 함수
    private fun loadUserInfo(){
        //유저 프로필 화면 coil 라이브러리 로드
        binding.userProfileImage.load(userInfo.profileurl){
            placeholder(R.drawable.customcircle)
            crossfade(true)
            transformations(RoundedCornersTransformation(5.0f))
        }
        //유저 배경화면 coil 라이브러리 로드
        binding.userbackgroundProfile.load(userInfo.backgroundurl){
            placeholder(R.drawable.customcircle)
            crossfade(true)
            transformations(RoundedCornersTransformation(5.0f))
        }
    }

    //유저 프로필 변경하는 함수
    private fun UpdateUserInfo(){
        //사진이 바뀌었을 때만 데이터 변경
        val profileImageName = userInfo.userUid+"profile"
        val backgroundImageName = userInfo.userUid+"background"
        val currentuser =Firebase.auth.currentUser

            val metadata = storageMetadata{contentType="image/jpg"}
            val profileImagestorageRef = stroage.reference.child(userInfo.userUid!!).child("${profileImageName}.jpg")
            val backgroundImagestorageRef = stroage.reference.child(userInfo.userUid!!).child("${backgroundImageName}.jpg")

        if(updatedProfile&&updatebackground) {
            Log.e("프로필 수정중","둘다 수정")
            profileImagestorageRef.putFile(profileImageUrl, metadata).addOnSuccessListener {
                profileImagestorageRef.downloadUrl.addOnSuccessListener { uri ->
                    userInfo.profileurl = uri.toString()
                    backgroundImagestorageRef.putFile(backgroundImageUrl, metadata)
                        .addOnSuccessListener {
                            backgroundImagestorageRef.downloadUrl.addOnSuccessListener { uri ->
                                userInfo.backgroundurl = uri.toString()
                                val userInfoUpdate = mutableMapOf<String, Any>()
                                userInfoUpdate["profileurl"] = userInfo.profileurl!!
                                userInfoUpdate["backgroundurl"] = userInfo.backgroundurl!!
                                if (currentuser != null) {
                                    Firebase.database(Key.DB_URL).reference.child(Key.DB_USERS)
                                        .child(currentuser.uid).updateChildren(userInfoUpdate)
                                        .addOnSuccessListener {
                                            updatemyInfo()
                                            CancelEditMode()
                                        }
                                }
                            }
                        }
                }
            }
        }
        else if(updatedProfile){
            Log.e("프로필 수정중","프로필만 수정")
            profileImagestorageRef.putFile(profileImageUrl,metadata).addOnSuccessListener{
                profileImagestorageRef.downloadUrl.addOnSuccessListener { uri->
                    userInfo.profileurl = uri.toString()
                    val userInfoUpdate = mutableMapOf<String, Any>()
                    userInfoUpdate["profileurl"] = userInfo.profileurl!!
                    if(currentuser!=null){
                        Firebase.database(Key.DB_URL).reference.child(Key.DB_USERS).child(currentuser.uid).updateChildren(userInfoUpdate).addOnSuccessListener {
                            updatemyInfo()
                            CancelEditMode()
                        }
                    }
                }
            }
        }
        else if(updatebackground){
            Log.e("프로필 수정중","배경만 수정")
            backgroundImagestorageRef.putFile(backgroundImageUrl,metadata).addOnSuccessListener{
                backgroundImagestorageRef.downloadUrl.addOnSuccessListener {uri->
                    userInfo.backgroundurl=uri.toString()
                    val userInfoUpdate = mutableMapOf<String, Any>()
                    userInfoUpdate["backgroundurl"] = userInfo.backgroundurl!!
                    if(currentuser!=null){
                        Firebase.database(Key.DB_URL).reference.child(Key.DB_USERS).child(currentuser.uid).updateChildren(userInfoUpdate).addOnSuccessListener {
                            updatemyInfo()
                            CancelEditMode()
                        }
                    }
                }
            }
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