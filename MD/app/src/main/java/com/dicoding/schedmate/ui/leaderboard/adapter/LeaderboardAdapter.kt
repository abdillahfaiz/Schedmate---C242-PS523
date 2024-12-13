package com.dicoding.schedmate.ui.leaderboard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.schedmate.data.response.LeaderboardItem
import com.dicoding.schedmate.data.response.UsersWithProgress2Item
import com.dicoding.schedmate.databinding.LeaderboardItemBinding
import com.dicoding.schedmate.databinding.UserCompletedItemBinding

class LeaderboardAdapter : ListAdapter<LeaderboardItem, LeaderboardAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LeaderboardAdapter.MyViewHolder {
        val binding = LeaderboardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LeaderboardAdapter.MyViewHolder, position: Int) {
        val userItem = getItem(position)
        holder.bind(userItem)
        holder.binding.tvIndexRank.text = "#${position + 1}"
//        holder.binding.btnDetail.setOnClickListener {onItemClickCallback.onItemClicked(userItem)}
    }

    class MyViewHolder( val binding: LeaderboardItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(userItem: LeaderboardItem) {
            binding.tvUsername.text = userItem.username

            if (userItem.photo != "") {
                Glide.with(itemView.context)
                    .load(userItem.photo)
                    .into(binding.cvPhotoProfile)
            }
//            binding.tvUsername.text = userItem.userName
//            binding.tvEmail.text = userItem.email
//            if (userItem.userPhoto != ""){
//                Glide.with(itemView.context)
//                    .load(userItem.userPhoto)
//                    .into(binding.circleImageView)
//            }
        }
    }

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<LeaderboardItem>() {
            override fun areItemsTheSame(oldItem: LeaderboardItem, newItem: LeaderboardItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: LeaderboardItem, newItem: LeaderboardItem): Boolean {
                return oldItem == newItem
            }
        }

    }

    interface OnItemClickCallback {
        fun onItemClicked(data: UsersWithProgress2Item)
    }
}