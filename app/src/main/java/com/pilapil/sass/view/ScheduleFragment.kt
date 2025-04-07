package com.pilapil.sass.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pilapil.sass.adapter.ScheduleAdapter
import com.pilapil.sass.api.ApiService
import com.pilapil.sass.databinding.FragmentScheduleBinding
import com.pilapil.sass.repository.BookingRepository
import com.pilapil.sass.utils.SessionManager
import com.pilapil.sass.viewmodel.BookingViewModel
import com.pilapil.sass.viewmodel.BookingViewModelFactory

class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: BookingViewModel
    private lateinit var adapter: ScheduleAdapter
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sessionManager = SessionManager(requireContext())
        val repository = BookingRepository(ApiService.create())
        val factory = BookingViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[BookingViewModel::class.java]

        adapter = ScheduleAdapter()

        val selectedDate = arguments?.getString("SELECTED_DATE") ?: return
        val token = sessionManager.getAuthToken()
        val schoolId = sessionManager.getSchoolId()

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        if (!token.isNullOrEmpty() && !schoolId.isNullOrEmpty()) {
            viewModel.getAvailableSlots(token, schoolId, selectedDate)
        }

        viewModel.availableSlots.observe(viewLifecycleOwner) { response ->
            if (response.isSuccessful) {
                val slots = response.body()?.available ?: emptyList()
                adapter.submitList(slots)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
