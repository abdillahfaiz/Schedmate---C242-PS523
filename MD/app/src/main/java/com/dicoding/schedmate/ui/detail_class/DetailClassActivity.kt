package com.dicoding.schedmate.ui.detail_class

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.schedmate.R
import com.dicoding.schedmate.data.pref.UserPreference
import com.dicoding.schedmate.data.pref.dataStore
import com.dicoding.schedmate.data.response.ClassDataItem
import com.dicoding.schedmate.data.response.TaskDataItem
import com.dicoding.schedmate.databinding.ActivityDetailClassBinding
import com.dicoding.schedmate.ui.create_task.CreateTaskActivity
import com.dicoding.schedmate.ui.detail_class.adapter.ListTaskAdapter
import com.dicoding.schedmate.ui.detail_task.DetailTaskActivity
import com.dicoding.schedmate.ui.detail_task.DetailTaskViewModel
import com.dicoding.schedmate.ui.home.adapter.ListClassAdapter
import com.dicoding.schedmate.ui.leaderboard.LeaderboardFragment

class DetailClassActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDetailClassBinding
    private lateinit var viewModel: DetailClassViewModel

    companion object {
        const val ID_CLASS = "id_class"
        const val NAME_CLASS = "name_class"
        const val CODE_CLASS = "code_class"
    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == DetailTaskActivity.RESULT_CODE) {
            val selectedValue =
                result.data?.getBooleanExtra(DetailTaskActivity.RESULT_UPDATE, false)
            if (selectedValue == true) {
                val idClass = intent.getIntExtra(ID_CLASS, -1)
                viewModel.getListTaskData(idClass)
            }
        }

        if (result.resultCode == CreateTaskActivity.RESULT_CODE) {
            val selectedValue =
                result.data?.getBooleanExtra(CreateTaskActivity.RESULT_UPDATE, false)
            if (selectedValue == true) {
                val idClass = intent.getIntExtra(ID_CLASS, -1)
                viewModel.getListTaskData(idClass)
            }
        }}

    override fun onResume() {
        super.onResume()
        val idClass = intent.getIntExtra(ID_CLASS, -1)
        viewModel.getListTaskData(idClass)
        binding.btnCreateNewTask.visibility = View.VISIBLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailClassBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val idClass = intent.getIntExtra(ID_CLASS, -1)
        val nameClass = intent.getStringExtra(NAME_CLASS)
        val codeClass = intent.getStringExtra(CODE_CLASS)

        binding.tvClassName.text = getString(R.string.class_name, nameClass.toString())
        binding.tvClassCode.text = getString(R.string.class_code, codeClass.toString())

        val userPreference = UserPreference.getInstance(dataStore)
        val factory = DetailViewModelFactory(userPreference)
        viewModel = ViewModelProvider(this, factory)[DetailClassViewModel::class.java]

        viewModel.token.observe(this) {token ->
            viewModel.getListTaskData(idClass = idClass)
        }

        binding.btnCreateNewTask.setOnClickListener {
            val intent = Intent(this@DetailClassActivity, CreateTaskActivity::class.java)
            intent.putExtra(CreateTaskActivity.ID_CLASS, idClass)
            startActivity(intent)
        }

        binding.ivBackButton.setOnClickListener {
            finish()
        }

        viewModel.isLoading.observe(this) {isLoading ->
            binding.progressBar2.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        binding.ivRank.setOnClickListener {
            binding.btnCreateNewTask.visibility = View.INVISIBLE
            val bundle = Bundle()
            val leaderboardFragment = LeaderboardFragment()
            bundle.putInt(LeaderboardFragment.CLASS_ID, idClass)
            leaderboardFragment.arguments = bundle

            supportFragmentManager.beginTransaction()
                .replace(R.id.detail_class, leaderboardFragment)
                .addToBackStack(null)
                .commit()
        }

//        viewModel.errorMessage.observe(this) {error ->
//            binding.tvNotFound.visibility = if (error != null) View.VISIBLE else View.GONE
//            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
//        }

        val listTaskAdapter = ListTaskAdapter()


        listTaskAdapter.setOnItemClickCallback(object : ListTaskAdapter.OnItemClickCallback {
            override fun onItemClicked(data: TaskDataItem) {
                val intent = Intent(this@DetailClassActivity, DetailTaskActivity::class.java)
                intent.putExtra(DetailTaskActivity.ID_CLASS, idClass)
                intent.putExtra(DetailTaskActivity.ID_TASK, data.id)
//                 intent.putExtra(DetailClassActivity.CODE_CLASS, data.code)
                startActivity(intent)
            }
        })

        viewModel.listTask.observe(this) {listTask ->
            listTask?.let {
                binding.rvTaskList.layoutManager = LinearLayoutManager(this)
                listTaskAdapter.submitList(listTask.data)
                binding.rvTaskList.adapter = listTaskAdapter
            }
        }
    }

}