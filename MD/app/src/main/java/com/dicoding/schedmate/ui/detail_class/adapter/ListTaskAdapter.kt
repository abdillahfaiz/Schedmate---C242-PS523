package com.dicoding.schedmate.ui.detail_class.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.schedmate.R
import com.dicoding.schedmate.data.pref.SessionManager
import com.dicoding.schedmate.data.response.ClassDataItem
import com.dicoding.schedmate.data.response.TaskDataItem
import com.dicoding.schedmate.databinding.ClassItemBinding
import com.dicoding.schedmate.databinding.TaskItemBinding
import okhttp3.internal.concurrent.Task
import java.text.SimpleDateFormat
import java.util.Locale

class ListTaskAdapter() : ListAdapter<TaskDataItem, ListTaskAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListTaskAdapter.MyViewHolder {
        val binding = TaskItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListTaskAdapter.MyViewHolder, position: Int) {
        val taskItem = getItem(position)
        holder.bind(taskItem)
        holder.itemView.setOnClickListener {onItemClickCallback.onItemClicked(taskItem)}
    }

    class MyViewHolder(private val binding: TaskItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(taskItem: TaskDataItem) {
            val sessionManager = SessionManager(itemView.context)
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault())
            val date = inputFormat.parse(taskItem.deadline)
            val formattedDeadline = outputFormat.format(date)

            if (sessionManager.getRole() == "guru"){
                binding.ivProgress.visibility = View.INVISIBLE
            }

            when (taskItem.progress) {
                "0" -> {
                    binding.ivProgress.setImageResource(R.drawable.ic_uncompleted)
                }
                "1" -> {
                    binding.ivProgress.setImageResource(R.drawable.ic_in_progress)
                }
                "2" -> {
                    binding.ivProgress.setImageResource(R.drawable.ic_completed)
                }
                else -> {
                    binding.ivProgress.visibility = View.INVISIBLE
                }
            }

            val priority = (taskItem.priority * 3) / 100 * 100

            binding.pbScore.progress = priority.toInt()
            binding.tvPriorityScore.text = "Priority Score : " + priority.toString() + "%"

            binding.tvTitleTask.text = taskItem.title
            binding.tvDeadline.text = formattedDeadline
            binding.tvMapel.text = taskItem.mapel
        }
    }


    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TaskDataItem>() {
            override fun areItemsTheSame(oldItem: TaskDataItem, newItem: TaskDataItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: TaskDataItem, newItem: TaskDataItem): Boolean {
                return oldItem == newItem
            }

        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: TaskDataItem)
    }
}