package com.example.chattingapp.UserList

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ListAdapter
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.chattingapp.R
import com.example.chattingapp.databinding.ItemUserBinding

class UserAdapter(private val friendview:friendProfileView):androidx.recyclerview.widget.ListAdapter<UserItem,UserAdapter.UserViewHolder>(diffutil){

    inner class  UserViewHolder(private val binding : ItemUserBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(item:UserItem){
            binding.clickfriendView.setOnClickListener{
                friendview.clickfriendProfile(item)
            }
            binding.userlistProfileImageView.load(item.profileurl){
                placeholder(R.drawable.customcircle)
                crossfade(true)
                transformations(RoundedCornersTransformation(5.0f))
            }
            binding.userlistname.text=item.username
            binding.userliststatusmessage.apply {
                if(!item.statusMessage.isNullOrEmpty()){
                    isVisible = true
                    text = item.statusMessage
                }else{
                    isVisible = false
                }
            }
        }
    }

    companion object{
        val diffutil = object : DiffUtil.ItemCallback<UserItem>(){
            override fun areItemsTheSame(oldItem: UserItem, newItem: UserItem): Boolean {
                return oldItem.userUid == newItem.userUid
            }

            override fun areContentsTheSame(oldItem: UserItem, newItem: UserItem): Boolean {
               return oldItem==newItem
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(ItemUserBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
}