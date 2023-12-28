package com.example.chattingapp.UserList

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chattingapp.databinding.ItemUserBinding

class UserAdapter:androidx.recyclerview.widget.ListAdapter<UserItem,UserAdapter.UserViewHolder>(diffutil){

    inner class  UserViewHolder(private val binding : ItemUserBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(item:UserItem){
            binding.userlistname.text=item.username

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