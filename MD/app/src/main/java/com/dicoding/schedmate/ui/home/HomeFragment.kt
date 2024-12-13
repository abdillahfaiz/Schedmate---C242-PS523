package com.dicoding.schedmate.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dicoding.schedmate.R
import com.dicoding.schedmate.data.pref.SessionManager
import com.dicoding.schedmate.data.pref.UserPreference
import com.dicoding.schedmate.data.pref.dataStore
import com.dicoding.schedmate.data.response.ClassDataItem
import com.dicoding.schedmate.databinding.FragmentHomeBinding
import com.dicoding.schedmate.ui.detail_class.DetailClassActivity
import com.dicoding.schedmate.ui.home.adapter.ListClassAdapter
import com.dicoding.schedmate.ui.login.LoginViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var viewModel: HomeViewModel

    private val binding get() = _binding!!
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        viewModel.token.observe(viewLifecycleOwner) { token ->
           viewModel.getProfile()
           viewModel.getClassData()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {error ->
            binding.tvNotFound.visibility = if (error != "") View.VISIBLE else View.GONE
            binding.ivNotFound.visibility = if (error != "") View.VISIBLE else View.GONE
        }

        viewModel.token.observe(viewLifecycleOwner) { token ->
            if (token != "") {
                Log.d("INI TOKEN 4", token)
                viewModel.getProfile()
                viewModel.getClassData()
            }
        }

        viewModel.joinClassRes.observe(viewLifecycleOwner) {joinClassRes ->
            if(joinClassRes != null) {
                viewModel.getClassData()
            }
        }

        val listClassAdapter = ListClassAdapter()

        listClassAdapter.setOnItemClickCallback(object : ListClassAdapter.OnItemClickCallback {
            override fun onItemClicked(data: ClassDataItem) {
                val intent = Intent(requireContext(), DetailClassActivity::class.java)
                intent.putExtra(DetailClassActivity.ID_CLASS, data.id)
                intent.putExtra(DetailClassActivity.NAME_CLASS, data.name)
                intent.putExtra(DetailClassActivity.CODE_CLASS, data.code)
//                 intent.putExtra(DetailClassActivity.CODE_CLASS, data.code)
                startActivity(intent)
            }
        })

        viewModel.classRes.observe(viewLifecycleOwner) { classData ->
            classData?.let {
                binding.rvClass.layoutManager = GridLayoutManager(requireContext(), 2)
                listClassAdapter.submitList(classData.data)
                binding.rvClass.adapter = listClassAdapter
            }
        }

        viewModel.profileRes.observe(viewLifecycleOwner) { profileRes ->
            profileRes?.let {
                val greetingName = getString(R.string.hallo_1_s, it.data.username ?: "Guest")
                binding.tvWelcome.text = greetingName

                if (profileRes.data.photo != "") {
                    Glide.with(requireContext())
                        .load(profileRes.data.photo)
                        .into(binding.ivProfilePicture)
                }else {
                    Glide.with(requireContext())
                        .load("https://i.pinimg.com/originals/f1/0f/f7/f10ff70a7155e5ab666bcdd1b45b726d.jpg")
                        .into(binding.ivProfilePicture)
                }
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
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val userPreference = UserPreference.getInstance(requireContext().dataStore)
        val factory = HomeViewModelFactory(userPreference)
        viewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]


        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}