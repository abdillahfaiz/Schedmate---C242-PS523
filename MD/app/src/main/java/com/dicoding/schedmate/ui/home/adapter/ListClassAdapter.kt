package com.dicoding.schedmate.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.schedmate.data.response.ClassDataItem
import com.dicoding.schedmate.databinding.ClassItemBinding

class ListClassAdapter : ListAdapter<ClassDataItem, ListClassAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListClassAdapter.MyViewHolder {
        val binding = ClassItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListClassAdapter.MyViewHolder, position: Int) {
        val classItem = getItem(position)
        holder.bind(classItem)
        holder.itemView.setOnClickListener {onItemClickCallback.onItemClicked(classItem)}
    }

    class MyViewHolder(private val binding: ClassItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(classItem : ClassDataItem) {
            binding.tvClassName.text = classItem.name
            binding.tvCodeClass.text = "Kode Kelas : ${classItem.code}"
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ClassDataItem>() {
            override fun areItemsTheSame(oldItem: ClassDataItem, newItem: ClassDataItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ClassDataItem, newItem: ClassDataItem): Boolean {
                return oldItem == newItem
            }
        }
    }


    interface OnItemClickCallback {
        fun onItemClicked(data: ClassDataItem)
    }


}