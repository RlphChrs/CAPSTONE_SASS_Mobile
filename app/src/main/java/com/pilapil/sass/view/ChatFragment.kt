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

    private var hasHiddenHeader = false
    private var headerContainer: LinearLayout? = null
    private lateinit var chatRecyclerView: RecyclerView

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
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView)
        headerContainer = view.findViewById(R.id.header_container)

        chatAdapter = MessageAdapter(chatMessages)
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true
        chatRecyclerView.layoutManager = layoutManager
        chatRecyclerView.adapter = chatAdapter

        //  Detect scroll to top to show header again
        chatRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
                if (firstVisibleItem == 0 && hasHiddenHeader && headerContainer?.visibility == View.GONE) {
                    showHeader()
                }
            }
        })

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
        // 👇 Remove header when first message is added
        if (chatMessages.isEmpty() && headerContainer?.visibility != View.GONE) {
            headerContainer?.visibility = View.GONE
        }

        chatMessages.add(chatMessage)
        chatAdapter.notifyItemInserted(chatMessages.size - 1)
        chatRecyclerView.scrollToPosition(chatMessages.size - 1)
    }

    private fun showHeader() {
        headerContainer?.post {
            headerContainer?.visibility = View.VISIBLE
            headerContainer?.translationY = -headerContainer!!.height.toFloat()
            headerContainer?.alpha = 0f
            headerContainer?.animate()
                ?.translationY(0f)
                ?.alpha(1f)
                ?.setDuration(300)
                ?.withEndAction { hasHiddenHeader = false }
                ?.start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        println("On Destroy Triggered")
        chatManager.saveBufferedMessages()
    }
}
