package com.dicoding.schedmate.ui.leaderboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.dicoding.schedmate.R
import com.dicoding.schedmate.data.pref.SessionManager
import com.dicoding.schedmate.data.pref.UserPreference
import com.dicoding.schedmate.data.pref.dataStore
import com.dicoding.schedmate.databinding.FragmentLeaderboardBinding
import com.dicoding.schedmate.ui.home.HomeViewModel
import com.dicoding.schedmate.ui.home.HomeViewModelFactory
import com.dicoding.schedmate.ui.leaderboard.adapter.LeaderboardAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class LeaderboardFragment : Fragment() {

    private lateinit var viewModel: LeaderboardViewModel
    private lateinit var viewModel2: HomeViewModel
    private var _binding: FragmentLeaderboardBinding? = null
    private lateinit var sessionManager: SessionManager

    companion object {
        const val CLASS_ID = "class_id"
    }

    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val classId = arguments?.getInt(CLASS_ID)

        sessionManager = SessionManager(requireContext())

        viewModel2.token.observe(viewLifecycleOwner) { token ->
            if (token != "") {
                viewModel2.getClassData()
            }
        }

        viewModel2.classRes.observe(viewLifecycleOwner) { classRes ->
            classRes?.let {
                sessionManager.getRole()?.let {
                    if (it == "murid") {
                        viewModel.getLeaderboard(classRes.data[0].id)
                    }
                }
            }
        }

        viewModel.token.observe(viewLifecycleOwner) { token ->
            if (token != "") {
                sessionManager.getRole()?.let {
                    if (it == "guru") {
                        viewModel.getLeaderboard(classId!!)
                    }
                }
            }
        }

        val leaderboardAdapter = LeaderboardAdapter()

        viewModel.leaderboard.observe(viewLifecycleOwner) { leaderboard ->
            leaderboard?.let {
                binding.rvLeaderboard.layoutManager = LinearLayoutManager(requireContext())
                leaderboardAdapter.submitList(leaderboard.data)
                binding.rvLeaderboard.adapter = leaderboardAdapter

                if (leaderboard.data[0].photo != "") {
                    Glide.with(requireContext())
                        .load(leaderboard.data[0].photo)
                        .into(binding.cvWinner)
                }

                binding.tvNameWinner.text = leaderboard.data[0].username
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val userPreference = UserPreference.getInstance(requireContext().dataStore)
        val factory = LeaderboardViewModelFactory(userPreference)
        viewModel = ViewModelProvider(this, factory)[LeaderboardViewModel::class.java]

        val factory2 = HomeViewModelFactory(userPreference)
        viewModel2 = ViewModelProvider(this, factory2)[HomeViewModel::class.java]

        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)


        return binding.root
    }

}