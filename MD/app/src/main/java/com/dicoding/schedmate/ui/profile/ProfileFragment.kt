package com.dicoding.schedmate.ui.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.dicoding.schedmate.R
import com.dicoding.schedmate.data.pref.SessionManager
import com.dicoding.schedmate.data.pref.UserPreference
import com.dicoding.schedmate.data.pref.dataStore
import com.dicoding.schedmate.databinding.FragmentHomeBinding
import com.dicoding.schedmate.databinding.FragmentProfileBinding
import com.dicoding.schedmate.ui.UpdateProfileActivity
import com.dicoding.schedmate.ui.home.HomeViewModel
import com.dicoding.schedmate.ui.home.HomeViewModelFactory
import com.dicoding.schedmate.ui.login.LoginActivity

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private lateinit var viewModel: ProfileViewModel

    private val binding get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sesssionManager = SessionManager(requireContext())

        viewModel.token.observe(viewLifecycleOwner) { token ->
            if (token != null) {
                viewModel.getProfile()
            }
        }

        viewModel.profileRes.observe(viewLifecycleOwner) { profileRes ->
            profileRes?.let {
                val greetingName = getString(R.string.hallo_1_s, it.data.username ?: "Guest")
                binding.tvName.text = greetingName

                binding.tvEmail.text = profileRes.data.email

                if (profileRes.data.photo != "") {
                    Glide.with(requireContext())
                        .load(profileRes.data.photo)
                        .into(binding.ivPhotoProfile)
                }else {
                    Glide.with(requireContext())
                        .load("https://i.pinimg.com/originals/f1/0f/f7/f10ff70a7155e5ab666bcdd1b45b726d.jpg")
                        .into(binding.ivPhotoProfile)
                }
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {isLoading ->
            if (isLoading) {
                binding.progressBar4.visibility = View.VISIBLE
                binding.linearLayout.visibility = View.INVISIBLE
            }else {
                binding.progressBar4.visibility = View.GONE
                binding.linearLayout.visibility = View.VISIBLE
            }
        }

        binding.btnLogout.setOnClickListener {
            sesssionManager.updateIsLogin(false)
            sesssionManager.updateRole("")
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        binding.btnUpdate.setOnClickListener {
            val intent = Intent(requireContext(), UpdateProfileActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val userPreference = UserPreference.getInstance(requireContext().dataStore)
        val factory = ProfileViewModelFactory(userPreference)
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]


        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}