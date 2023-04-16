package com.yamin.chatchat.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.yamin.chatchat.adapters.ChatsRecyclerViewAdapter
import com.yamin.chatchat.databinding.FragmentChatsBinding
import com.yamin.chatchat.viewmodels.UserViewModel


/**
 * A simple [Fragment] subclass.
 * Use the [ChatsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatsFragment : Fragment() {

    private var _viewBinding: FragmentChatsBinding? = null
    private val viewBinding get() = _viewBinding

    // View Models
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getUserChatsList()
    }

    private fun getUserChatsList() {
        val userId = userViewModel.getCurrentUserId()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")
        _viewBinding = FragmentChatsBinding.inflate(inflater, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreateView")
        super.onViewCreated(view, savedInstanceState)
        viewBinding?.apply {
            val adapter = ChatsRecyclerViewAdapter()
            chatsRecyclerView.layoutManager = LinearLayoutManager(context)
            chatsRecyclerView.adapter = adapter
        }
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        super.onDestroyView()
        _viewBinding = null
    }

    companion object {
        const val TAG = "ChatsFragment"
    }
}