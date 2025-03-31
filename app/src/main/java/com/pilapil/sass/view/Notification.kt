package com.pilapil.sass.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pilapil.sass.R
import com.pilapil.sass.adapter.NotificationAdapter
import com.pilapil.sass.model.NotificationResponse
import com.pilapil.sass.utils.SessionManager
import com.pilapil.sass.viewModel.NotificationViewModel

class Notification : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationAdapter
    private lateinit var viewModel: NotificationViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_notification, container, false)

        recyclerView = view.findViewById(R.id.notifications_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = NotificationAdapter(mutableListOf()) { notification ->
            showNotificationDialog(notification)
        }

        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(this)[NotificationViewModel::class.java]
        viewModel.notifications.observe(viewLifecycleOwner) { notifications ->
            adapter.updateData(notifications)
        }

        viewModel.fetchNotifications(requireContext())

        return view
    }

    private fun showNotificationDialog(notification: NotificationResponse) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle(notification.subject)
            .setMessage(
                "From: ${notification.from}\n\n" +
                        "Message:\n${notification.message}"
            )
            .setPositiveButton("Close", null)
            .show()
    }
}
