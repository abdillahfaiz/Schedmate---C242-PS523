package com.dicoding.schedmate.ui.detail_task.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.schedmate.data.response.TaskDataItem
import com.dicoding.schedmate.data.response.UsersWithProgress2Item
import com.dicoding.schedmate.databinding.TaskItemBinding
import com.dicoding.schedmate.databinding.UserCompletedItemBinding
import java.text.SimpleDateFormat
import java.util.Locale

class UserCompletedAdapter : ListAdapter<UsersWithProgress2Item, UserCompletedAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserCompletedAdapter.MyViewHolder {
        val binding = UserCompletedItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserCompletedAdapter.MyViewHolder, position: Int) {
        val userItem = getItem(position)
        holder.bind(userItem)
        holder.binding.btnDetail.setOnClickListener {onItemClickCallback.onItemClicked(userItem)}
//        holder.itemView.setOnClickListener {onItemClickCallback.onItemClicked(userItem)}
    }

    class MyViewHolder( val binding: UserCompletedItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(userItem: UsersWithProgress2Item) {
            binding.tvUsername.text = userItem.userName
            binding.tvEmail.text = userItem.email
            if (userItem.userPhoto != ""){
                Glide.with(itemView.context)
                    .load(userItem.userPhoto)
                    .into(binding.circleImageView)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UsersWithProgress2Item>() {
            override fun areItemsTheSame(oldItem: UsersWithProgress2Item, newItem: UsersWithProgress2Item): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: UsersWithProgress2Item, newItem: UsersWithProgress2Item): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: UsersWithProgress2Item)
    }
}