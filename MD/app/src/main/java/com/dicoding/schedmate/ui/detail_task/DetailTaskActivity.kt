package com.dicoding.schedmate.ui.detail_task

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.schedmate.ui.image_task_upload.ImageTaskUploadActivity
import com.dicoding.schedmate.R
import com.dicoding.schedmate.data.pref.SessionManager
import com.dicoding.schedmate.data.pref.UserPreference
import com.dicoding.schedmate.data.pref.dataStore
import com.dicoding.schedmate.data.response.UsersWithProgress2Item
import com.dicoding.schedmate.databinding.ActivityDetailTaskBinding
import com.dicoding.schedmate.ui.DetailUserTaskActivity
import com.dicoding.schedmate.ui.detail_task.adapter.UserCompletedAdapter
import com.dicoding.schedmate.ui.home.HomeViewModel
import com.dicoding.schedmate.ui.home.HomeViewModelFactory
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class DetailTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailTaskBinding
    private lateinit var viewModel: DetailTaskViewModel
    private lateinit var viewModel2: HomeViewModel
    private lateinit var sessionManager: SessionManager
    private var idTask : Int? = null
    private var idClass : Int? = null


    companion object {
        const val ID_TASK = "id_task"
        const val ID_CLASS = "id_class"
        const val RESULT_CODE = 110
        const val RESULT_UPDATE = "result_update"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailTaskBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        supportActionBar?.hide()

        sessionManager = SessionManager(this)

        val userPreference = UserPreference.getInstance(dataStore)
        val factory = DetailTaskViewModelFactory(userPreference)
        viewModel = ViewModelProvider(this, factory)[DetailTaskViewModel::class.java]

        val factory2 = HomeViewModelFactory(userPreference)
        viewModel2 = ViewModelProvider(this, factory2)[HomeViewModel::class.java]


         idTask = intent.getIntExtra(ID_TASK, -1)
         idClass = intent.getIntExtra(ID_CLASS, -1)

        if (sessionManager.getRole() == "guru") {
            binding.cvProgress.visibility = View.GONE
            binding.btnUpdateTask.visibility = View.INVISIBLE
            binding.btnDeleteTask.visibility = View.VISIBLE
//            binding.btnUpdateTeacherTask.visibility = View.VISIBLE
        }

        binding.btnDeleteTask.setOnClickListener {
            viewModel.deleteTask(idTask!!, idClass!!)
        }

        viewModel.updateTaskRes.observe(this) { updateTaskRes ->
            if (updateTaskRes != null) {
                Toast.makeText(this, updateTaskRes.message, Toast.LENGTH_SHORT).show()
                sessionManager.getRole()?.let { viewModel.getDetailTask(idClass!!, idTask!!, it) }
            }
        }


        viewModel.deleteTask.observe(this) {deleteTask ->
            if (deleteTask != null){
                Toast.makeText(this, deleteTask.message, Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        viewModel.token.observe(this) { token ->

            if (token != "") {
                sessionManager.getRole()?.let { viewModel.getDetailTask(idClass!!, idTask!!, it) }
            }
        }


        binding.ivBackButton.setOnClickListener {
            finish()
        }

        viewModel.isLoading.observe(this) {isLoading ->
            binding.progressBar3.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.tvTitleTask.visibility = if (isLoading) View.GONE else View.VISIBLE
            binding.tvDesc.visibility = if (isLoading) View.GONE else View.VISIBLE
            binding.tvDeadline.visibility = if (isLoading) View.GONE else View.VISIBLE
            binding.tvMapel.visibility = if (isLoading) View.GONE else View.VISIBLE
            binding.completed.visibility = if (isLoading) View.GONE else View.VISIBLE
            binding.imageView3.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        val userCompletedAdapter = UserCompletedAdapter()

        userCompletedAdapter.setOnItemClickCallback(object : UserCompletedAdapter.OnItemClickCallback {
            override fun onItemClicked(data: UsersWithProgress2Item) {
                val intent = Intent(this@DetailTaskActivity, DetailUserTaskActivity::class.java)
                intent.putExtra(DetailUserTaskActivity.IMAGE, data.uploadFile)
                intent.putExtra(DetailUserTaskActivity.EMAIL, data.email)
                intent.putExtra(DetailUserTaskActivity.USERNAME, data.userName)
                intent.putExtra(DetailUserTaskActivity.PHOTO_PROFILE, data.userPhoto)
                startActivity(intent)
            }
        })





        viewModel.detailTaskRes.observe(this) { detailTaskRes ->
            if (detailTaskRes != null) {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault())
                val date = inputFormat.parse(detailTaskRes.data.deadline)
                val formattedDeadline = outputFormat.format(date)

                binding.tvTitleTask.text = detailTaskRes.data.title
                binding.tvDesc.text = detailTaskRes.data.description
                binding.tvDeadline.text = formattedDeadline
                binding.tvMapel.text = detailTaskRes.data.mapel

                binding.ivProgress.setImageResource(
                    when (detailTaskRes.data.progress) {
                        "0" -> R.drawable.ic_uncompleted
                        "1" -> R.drawable.ic_in_progress
                        "2" -> R.drawable.ic_completed
                        else -> R.drawable.ic_image
                    }
                )

                binding.tvProgress.setText(
                    when (detailTaskRes.data.progress) {
                        "0" -> "Belum Dikerjakan"
                        "1" -> "Sedang Dikerjakan"
                        "2" -> "Selesai"
                        else -> "null"
                    }
                )

                binding.btnUpdateTask.setText(
                    when (detailTaskRes.data.progress) {
                        "0" -> "Mulai Kerjakan"
                        "1" -> "Selesaikan"
                        "2" -> "Task Selesai"
                        else -> "null"
                    }
                )


                binding.btnUpdateTask.setOnClickListener {
                    when(detailTaskRes.data.progress){
                        "0" -> {
                            val current = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                LocalDateTime.now()
                            } else {
                                TODO("VERSION.SDK_INT < O")
                            }
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                            val formatted = current.format(formatter)
                            viewModel.updateTask(startTime =  formatted, taskId = idTask!!, progressStatus = "1")
                        }
                        "1" -> {
                            val intent = Intent(this@DetailTaskActivity, ImageTaskUploadActivity::class.java)
                            intent.putExtra(ImageTaskUploadActivity.ID_TASK, idTask)
                            startActivity(intent)
                        }
                        "2" ->{
//                            val intent = Intent(this@DetailTaskActivity, ImageTaskUploadActivity::class.java)
//                            intent.putExtra(ImageTaskUploadActivity.ID_TASK, idTask)
//                            startActivity(intent)
                        }
                    }
                }

                binding.rvUserCompleted.layoutManager = LinearLayoutManager(this)
                userCompletedAdapter.submitList(detailTaskRes.data.usersWithProgress2)
                binding.rvUserCompleted.adapter = userCompletedAdapter
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("DetailTaskActivity", "onResume: TRUE")
        sessionManager.getRole()?.let { idClass?.let { it1 -> idTask?.let { it2 ->
            viewModel.getDetailTask(it1,
                it2, it)
        } } }
    }
}