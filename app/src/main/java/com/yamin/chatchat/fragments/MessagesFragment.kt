package com.yamin.chatchat.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.yamin.chatchat.adapters.MessagesRecyclerViewAdapter
import com.yamin.chatchat.data.models.Message
import com.yamin.chatchat.databinding.FragmentMessagesBinding
import com.yamin.chatchat.utils.Response
import com.yamin.chatchat.viewmodels.ChatsViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [MessagesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MessagesFragment : Fragment() {

    private var _viewBinding: FragmentMessagesBinding? = null
    private val viewBinding get() = _viewBinding

    private val chatsViewModel: ChatsViewModel by activityViewModels()

    private var userId: String? = null
    private lateinit var fullConversationData: ArrayList<Message>
    private lateinit var messagesRecyclerViewAdapter: MessagesRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")
        observeGetFullConversationStatus()
        userId = arguments?.getString("userId")
        userId?.let {
            fullConversationData = chatsViewModel.getConversationHistory(it)
            chatsViewModel.registerLiveMessageObserver(it)
        }
        _viewBinding = FragmentMessagesBinding.inflate(inflater, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        observeLiveConversation()
        userId?.let { receiverId ->
            Log.d(TAG, "userId : $userId fullConversationData : $fullConversationData")
            if (fullConversationData.isNotEmpty()) {
                launchRecyclerViewAdapter()
            }
            viewBinding?.apply {
                enterMessage.setOnClickListener {
                    scrollToBottom()
                }
                sendButton.setOnClickListener {
                    val message = enterMessage.text.toString()
                    if (message.isNotBlank()) {
                        chatsViewModel.sendMessageToReceiver(message, receiverId, System.currentTimeMillis())
                        enterMessage.setText("")
                    }
                }
            }
        }
    }

    private fun scrollToBottom() {
        var lastPosition = 0
        if (::messagesRecyclerViewAdapter.isInitialized) {
            lastPosition = messagesRecyclerViewAdapter.itemCount - 1
        }
        if (lastPosition < 0) {
            lastPosition = 0
        }
        viewBinding?.messagesRecyclerView?.scrollToPosition(lastPosition)
    }

    private fun observeGetFullConversationStatus() {
        Log.d(TAG, "observeGetFullConversationStatus")
        chatsViewModel.getFullConversationStatus.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Success -> {
                    Log.d(TAG, "observeGetFullConversationStatus Response.Success ${it.data}")
                    fullConversationData = it.data.second
                    launchRecyclerViewAdapter()
                }
                is Response.Error -> {
                    ///TODO
                }
                else -> {}
            }
        }
    }

    private fun launchRecyclerViewAdapter() {
        Log.d(TAG, "launchRecyclerViewAdapter")
        viewBinding?.apply {
            messagesRecyclerViewAdapter = MessagesRecyclerViewAdapter(fullConversationData, userId!!)
            messagesRecyclerView.layoutManager = LinearLayoutManager(context)
            messagesRecyclerView.adapter = messagesRecyclerViewAdapter
            scrollToBottom()
        }
    }

    private fun observeLiveConversation() {
        Log.d(TAG, "observeLiveConversation")

        chatsViewModel.getLiveMessage().observe(viewLifecycleOwner) {
            when (it) {
                is Response.Success -> {
                    Log.d(TAG, "Response.Success ${it.data}")
                    if (::messagesRecyclerViewAdapter.isInitialized) {
                        messagesRecyclerViewAdapter.updateConversation(it.data)
                        scrollToBottom()
                    }
                }
                is Response.Error -> {
                    ///TODO
                }
                else -> {}
            }
        }
    }

    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
//        userId?.let {
//            chatsViewModel.getConversationHistory(it)
//        }
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        super.onPause()
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        super.onDestroyView()
        _viewBinding = null
    }

    companion object {
        const val TAG = "MessagesFragment"
    }
}