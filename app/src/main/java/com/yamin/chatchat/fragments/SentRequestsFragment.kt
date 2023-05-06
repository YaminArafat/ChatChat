package com.yamin.chatchat.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.yamin.chatchat.adapters.SentRequestsRecyclerViewAdapter
import com.yamin.chatchat.data.models.Friend
import com.yamin.chatchat.databinding.FragmentSentRequestsBinding
import com.yamin.chatchat.helpers.OnItemClickListener
import com.yamin.chatchat.utils.Response
import com.yamin.chatchat.viewmodels.FriendsViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [SentRequestsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SentRequestsFragment : Fragment(), OnItemClickListener {

    private var _viewBinding: FragmentSentRequestsBinding? = null
    private val viewBinding get() = _viewBinding

    private val friendsViewModel: FriendsViewModel by activityViewModels()

    private lateinit var sentRequestsRecyclerViewAdapter: SentRequestsRecyclerViewAdapter
    private lateinit var sentRequestsList: ArrayList<Friend>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")
        _viewBinding = FragmentSentRequestsBinding.inflate(inflater, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        observeSentRequestList()
        observeCancelRequestStatus()
        sentRequestsList = friendsViewModel.getSentRequestsList()
        viewBinding?.apply {
            sentRequestsRecyclerViewAdapter = SentRequestsRecyclerViewAdapter(sentRequestsList, this@SentRequestsFragment)
            sentRequestsRecyclerView.layoutManager = LinearLayoutManager(context)
            sentRequestsRecyclerView.adapter = sentRequestsRecyclerViewAdapter
        }
    }

    private fun observeCancelRequestStatus() {
        Log.d(TAG, "observeCancelRequestStatus")
        friendsViewModel.cancelRequestStatus.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Success -> {
                    if (it.data) {
                        getUpdatedListData()
                    }
                }
                is Response.Error -> {
                    ///TODO
                }
                else -> {}
            }
        }
    }

    private fun getUpdatedListData() {
        Log.d(TAG, "getUpdatedListData")
        friendsViewModel.apply {
            getSentRequestsList()
            getAvailableUserList()
        }
    }

    private fun observeSentRequestList() {
        Log.d(TAG, "observeSentRequestList")
        friendsViewModel.sentRequestsList.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Success -> {
                    Log.d(TAG, "Response.Success ${it.data}")
                    sentRequestsList = it.data
                    sentRequestsRecyclerViewAdapter.updateData(sentRequestsList)
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
        friendsViewModel.getSentRequestsList()
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        super.onDestroyView()
        _viewBinding = null
    }

    companion object {
        const val TAG = "SentRequestsFragment"
    }

    override fun onClick(userId: String) {
        friendsViewModel.cancelFriendRequest(userId)
    }
}