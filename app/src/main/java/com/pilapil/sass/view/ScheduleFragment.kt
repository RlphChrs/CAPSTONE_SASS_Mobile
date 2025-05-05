package com.pilapil.sass.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pilapil.sass.R
import com.pilapil.sass.adapter.ScheduleAdapter
import com.pilapil.sass.api.ApiService
import com.pilapil.sass.databinding.FragmentScheduleBinding
import com.pilapil.sass.repository.BookingRepository
import com.pilapil.sass.utils.SessionManager
import com.pilapil.sass.viewmodel.BookingViewModel
import com.pilapil.sass.viewmodel.BookingViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ScheduleFragment : Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: BookingViewModel
    private lateinit var adapter: ScheduleAdapter
    private lateinit var sessionManager: SessionManager

    private val allSlots = listOf(
        "08:00", "09:00", "10:00", "11:00",
        "13:00", "14:00", "15:00", "16:00"
    )

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

        val selectedDate = arguments?.getString("SELECTED_DATE") ?: return
        val token = sessionManager.getAuthToken()
        val schoolId = sessionManager.getSchoolId()

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Adapter with click callback
        adapter = ScheduleAdapter { selectedTime ->
            val bookingFragment = AppointmentBookingFragment()
            val bundle = Bundle().apply {
                putString("SELECTED_DATE", selectedDate)
                putString("SELECTED_TIME", selectedTime)
            }
            bookingFragment.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, bookingFragment)
                .addToBackStack(null)
                .commit()
        }

        binding.recyclerView.adapter = adapter

        if (!token.isNullOrEmpty() && !schoolId.isNullOrEmpty()) {
            CoroutineScope(Dispatchers.Main).launch {
                val response = repository.getBookedAppointments(token, selectedDate)
                if (response.isSuccessful) {
                    val bookedTimes = response.body()?.bookings?.map { it.fromTime } ?: emptyList()
                    val availableTimes = allSlots.filter { it !in bookedTimes }
                    adapter.submitList(availableTimes)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
