package com.dicoding.schedmate.ui.home_student

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dicoding.schedmate.R
import com.dicoding.schedmate.data.pref.UserPreference
import com.dicoding.schedmate.data.pref.dataStore
import com.dicoding.schedmate.data.response.TaskDataItem
import com.dicoding.schedmate.databinding.FragmentHomeBinding
import com.dicoding.schedmate.databinding.FragmentHomeStudentBinding
import com.dicoding.schedmate.ui.detail_class.DetailClassViewModel
import com.dicoding.schedmate.ui.detail_class.DetailViewModelFactory
import com.dicoding.schedmate.ui.detail_class.adapter.ListTaskAdapter
import com.dicoding.schedmate.ui.detail_task.DetailTaskActivity
import com.dicoding.schedmate.ui.home.HomeViewModel
import com.dicoding.schedmate.ui.home.HomeViewModelFactory
import kotlin.math.log

class HomeStudentFragment : Fragment() {

    companion object {
        fun newInstance() = HomeStudentFragment()
        const val CREATE_TASK_REQUEST_CODE = 1001
    }

    private lateinit var  viewModel: HomeViewModel
    private lateinit var  viewModel2: DetailClassViewModel
    private var _binding: FragmentHomeStudentBinding? = null
    private var progressSelected: String = ""

    private val binding get() = _binding!!

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == DetailTaskActivity.RESULT_CODE) {
            val selectedValue =
                result.data?.getBooleanExtra(DetailTaskActivity.RESULT_UPDATE, false)
            if (selectedValue == true) {
                viewModel.classRes.observe(viewLifecycleOwner) { classData ->
                    classData?.let {
                        viewModel2.getListTaskData(idClass = classData.data[0].id)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.classRes.observe(viewLifecycleOwner) { classData ->
            classData?.let {
                viewModel2.getListTaskData(idClass = classData.data[0].id)
            }
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        viewModel.token.observe(viewLifecycleOwner) { token ->
            viewModel.getProfile()
            viewModel.getClassData()
        }



        viewModel.profileRes.observe(viewLifecycleOwner) { profileRes ->
            if(profileRes != null) {
                binding.tvWelcome.text = profileRes.data.username
                if (profileRes.data.photo != ""){
                    Glide.with(this)
                        .load(profileRes.data.photo)
                        .into(binding.ivProfilePicture)
                }

            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

            if (isLoading) {
                binding.tvWelcome.visibility = View.GONE
                binding.ivProfilePicture.visibility = View.GONE
                binding.tvGreeting.visibility = View.GONE
                binding.cardView.visibility = View.GONE
                binding.tvDaftarTugas.visibility = View.GONE
                binding.chipGroup.visibility = View.GONE
                binding.rvTask.visibility = View.GONE
            } else {
                binding.tvWelcome.visibility = View.VISIBLE
                binding.ivProfilePicture.visibility = View.VISIBLE
                binding.tvGreeting.visibility = View.VISIBLE
                binding.cardView.visibility = View.VISIBLE
                binding.tvDaftarTugas.visibility = View.VISIBLE
                binding.chipGroup.visibility = View.VISIBLE
                binding.rvTask.visibility = View.VISIBLE
            }
        }

        viewModel.classRes.observe(viewLifecycleOwner) { classData ->
            classData?.let {
                binding.constraintClass.visibility = View.VISIBLE
                binding.constraintNoClass.visibility = View.GONE
                binding.tvClass.text = classData.data[0].name
                binding.tvCodeClass.text = "Kode Kelas : " + classData.data[0].code
                viewModel2.getListTaskData(idClass = classData.data[0].id)

                binding.chipAll.setOnClickListener {
                    progressSelected = ""
                    viewModel2.getListTaskData(idClass = classData.data[0].id)
                }

                binding.chipNotCompleted.setOnClickListener {
                    progressSelected = "0"
                    viewModel2.getListTaskData(idClass = classData.data[0].id)
                    binding.chipNotCompleted.isChecked = true
                }

                binding.chipInProgress.setOnClickListener {
                    progressSelected = "1"
                    viewModel2.getListTaskData(idClass = classData.data[0].id)
                    binding.chipInProgress.isChecked = true
                }

                binding.chipCompleted.setOnClickListener {
                    progressSelected = "2"
                    viewModel2.getListTaskData(idClass = classData.data[0].id)
                    binding.chipCompleted.isChecked = true
                }
            }
        }

        viewModel.errorClassData.observe(viewLifecycleOwner) {error ->
            binding.constraintClass.visibility = View.GONE
            binding.constraintNoClass.visibility = View.VISIBLE
//            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        }

        val listTaskAdapter = ListTaskAdapter()


        listTaskAdapter.setOnItemClickCallback(object : ListTaskAdapter.OnItemClickCallback {
            override fun onItemClicked(data: TaskDataItem) {
                val intent = Intent(requireContext(), DetailTaskActivity::class.java)
                intent.putExtra(DetailTaskActivity.ID_CLASS, 1)
                intent.putExtra(DetailTaskActivity.ID_TASK, data.id)
//                 intent.putExtra(DetailClassActivity.CODE_CLASS, data.code)
                startActivityForResult(intent, CREATE_TASK_REQUEST_CODE)
            }
        })

        viewModel2.listTask.observe(viewLifecycleOwner) {listTask ->
            listTask?.let {
                Log.d("INI LIST TASK", listTask.data.toString())
                val filteredTask = if (progressSelected == "") {
                    listTask.data
                } else {
                    listTask.data.filter { it.progress == progressSelected }
                }


                binding.rvTask.layoutManager = LinearLayoutManager(requireContext())
                listTaskAdapter.submitList(filteredTask)
                binding.rvTask.adapter = listTaskAdapter
            }
        }



        binding.btnJoin.setOnClickListener {
            val codeClassResult = binding.etCodeClass.text?.toString()?.trim()

            if (codeClassResult.isNullOrEmpty()){
                binding.etCodeClass.error = "Kode Kelas tidak boleh kosong"
            }else {
                Log.d("JOIN", codeClassResult)
                viewModel.doJoinClass(codeClassResult)
                binding.etCodeClass.setText("")
            }
        }

        viewModel.joinClassRes.observe(viewLifecycleOwner) {joinClassRes ->
            if(joinClassRes != null) {
                viewModel.getClassData()
            }
        }

        viewModel.token.observe(viewLifecycleOwner) { token ->
            if (token != "") {
                Log.d("INI TOKEN 4", token)
                viewModel.getProfile()
                viewModel.getClassData()
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val userPreference = UserPreference.getInstance(requireContext().dataStore)
        val factory = HomeViewModelFactory(userPreference)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]

        val factory2 = DetailViewModelFactory(userPreference)
        viewModel2 = ViewModelProvider(this, factory2)[DetailClassViewModel::class.java]




        _binding = FragmentHomeStudentBinding.inflate(inflater, container, false)
        return binding.root
    }
}