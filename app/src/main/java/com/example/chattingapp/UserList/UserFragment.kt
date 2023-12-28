package com.example.chattingapp.UserList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chattingapp.databinding.FragmentUserlistBinding

class UserFragment:Fragment() {
    private lateinit var binding : FragmentUserlistBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserlistBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("test","Test")
        val userlistAdapter = UserAdapter()
        binding.userlistRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter =userlistAdapter
        }

        userlistAdapter.submitList(mutableListOf<UserItem>().apply {
            add(UserItem("11","22"))
        })
    }

}