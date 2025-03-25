package com.pilapil.sass.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.viewModels
import com.pilapil.sass.R
import com.pilapil.sass.adapter.MessageAdapter
import com.pilapil.sass.api.ApiService
import com.pilapil.sass.api.PythonApiService
import com.pilapil.sass.model.ChatMessage
import com.pilapil.sass.repository.StudentAuthRepository
import com.pilapil.sass.utils.ChatManager
import com.pilapil.sass.utils.SessionManager
import com.pilapil.sass.viewModel.StudentAuthViewModel
import com.pilapil.sass.viewModel.ViewModelFactory

class ChatFragment : Fragment() {

    private lateinit var chatAdapter: MessageAdapter
    private val chatMessages = mutableListOf<ChatMessage>()

    private val studentAuthViewModel: StudentAuthViewModel by viewModels {
        ViewModelFactory(StudentAuthRepository(ApiService.create()))
    }

    private lateinit var chatManager: ChatManager
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_chat, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager(requireContext())
        val studentId = sessionManager.getStudentId()
        val schoolId = sessionManager.getSchoolId()

        chatManager = ChatManager(
            requireContext(),
            viewLifecycleOwner.lifecycleScope,
            studentAuthViewModel,
            PythonApiService.create()
        )

        val inputMessage = view.findViewById<EditText>(R.id.et_message)
        val sendButton = view.findViewById<ImageButton>(R.id.btn_send_message)
        val chatRecyclerView = view.findViewById<RecyclerView>(R.id.chatRecyclerView)

        chatAdapter = MessageAdapter(chatMessages)
        chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        chatRecyclerView.adapter = chatAdapter

        sendButton.setOnClickListener {
            val message = inputMessage.text.toString()
            if (message.isNotBlank()) {
                chatManager.sendMessageToChatbot(
                    schoolId ?: return@setOnClickListener,
                    studentId ?: return@setOnClickListener,
                    message,
                    { addMessageToChat(it) },
                    { error -> Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show() }
                )
                inputMessage.text.clear()
            }
        }
    }

    private fun addMessageToChat(chatMessage: ChatMessage) {
        chatMessages.add(chatMessage)
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
        view?.findViewById<RecyclerView>(R.id.chatRecyclerView)
            ?.scrollToPosition(chatMessages.size - 1)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        println("On Destroy Triggered")
        chatManager.saveBufferedMessages()
    }
}

