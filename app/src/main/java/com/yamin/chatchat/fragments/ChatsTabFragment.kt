package com.yamin.chatchat.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.yamin.chatchat.R
import com.yamin.chatchat.adapters.ChatsTabRecyclerViewAdapter
import com.yamin.chatchat.data.models.Chats
import com.yamin.chatchat.databinding.FragmentChatsTabBinding
import com.yamin.chatchat.helpers.OnItemClickListener
import com.yamin.chatchat.utils.Response
import com.yamin.chatchat.viewmodels.ChatsViewModel


/**
 * A simple [Fragment] subclass.
 * Use the [ChatsTabFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatsTabFragment : Fragment(), OnItemClickListener {

    private var _viewBinding: FragmentChatsTabBinding? = null
    private val viewBinding get() = _viewBinding

    private val chatsViewModel: ChatsViewModel by activityViewModels()
    private lateinit var chatsList: ArrayList<Chats>
    private lateinit var currentUserId: String
    private lateinit var chatsTabRecyclerAdapter: ChatsTabRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")
        _viewBinding = FragmentChatsTabBinding.inflate(inflater, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        observeChatsList()
        chatsList = chatsViewModel.getUserChatsList()
        currentUserId = chatsViewModel.getCurrentUserId().toString()
        viewBinding?.apply {
            chatsTabRecyclerAdapter = ChatsTabRecyclerViewAdapter(chatsList, currentUserId, this@ChatsTabFragment)
            chatsTabRecyclerView.layoutManager = LinearLayoutManager(context)
            chatsTabRecyclerView.adapter = chatsTabRecyclerAdapter
        }
    }

    private fun observeChatsList() {
        Log.d(TAG, "observeChatsList")
        chatsViewModel.chatsList.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Success -> {
                    Log.d(TAG, "Response.Success ${it.data}")
                    chatsList = it.data
                    chatsTabRecyclerAdapter.updateData(chatsList)
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
        chatsViewModel.getUserChatsList()
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

    override fun onClick(userId: String) {
        val bundle = Bundle()
        bundle.putString("userId", userId)
        launchMessagesFragment(bundle)
    }

    private fun launchMessagesFragment(bundle: Bundle) {
        val messagesFragment = MessagesFragment()
        messagesFragment.arguments = bundle
        val fragmentTransaction = childFragmentManager.beginTransaction().replace(R.id.home_activity_fragment_container, messagesFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    companion object {
        const val TAG = "ChatsTabFragment"
    }
}