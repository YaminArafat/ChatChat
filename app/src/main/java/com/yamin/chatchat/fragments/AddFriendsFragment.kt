package com.yamin.chatchat.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.yamin.chatchat.adapters.AddFriendsRecyclerViewAdapter
import com.yamin.chatchat.data.models.Friend
import com.yamin.chatchat.databinding.FragmentAddFriendsBinding
import com.yamin.chatchat.helpers.OnItemClickListener
import com.yamin.chatchat.utils.Response
import com.yamin.chatchat.viewmodels.FriendsViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [AddFriendsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddFriendsFragment : Fragment(), OnItemClickListener {
    private var _viewBinding: FragmentAddFriendsBinding? = null
    private val viewBinding get() = _viewBinding

    private val friendsViewModel: FriendsViewModel by activityViewModels()

    private lateinit var addFriendsRecyclerViewAdapter: AddFriendsRecyclerViewAdapter
    private lateinit var availableUserList: ArrayList<Friend>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")

        _viewBinding = FragmentAddFriendsBinding.inflate(inflater, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        observeAvailableUserList()
        observeSendRequestStatus()
        availableUserList = friendsViewModel.getAvailableUserList()
        Log.d(TAG, "userList $availableUserList")
        viewBinding?.apply {
            addFriendsRecyclerViewAdapter = AddFriendsRecyclerViewAdapter(availableUserList, this@AddFriendsFragment)
            addFriendsRecyclerView.layoutManager = LinearLayoutManager(context)
            addFriendsRecyclerView.adapter = addFriendsRecyclerViewAdapter
        }
    }

    private fun observeSendRequestStatus() {
        Log.d(TAG, "observeSendRequestStatus")
        friendsViewModel.sendRequestStatus.observe(viewLifecycleOwner) {
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

    private fun observeAvailableUserList() {
        Log.d(TAG, "observeAvailableUserList")
        friendsViewModel.availableUserList.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Success -> {
                    Log.d(TAG, "Response.Success ${it.data}")
                    availableUserList = it.data
                    addFriendsRecyclerViewAdapter.updateData(availableUserList)
                }
                is Response.Error -> {
                    ///TODO
                }
                else -> {

                }
            }
        }
    }

    override fun onClick(userId: String) {
        friendsViewModel.sendFriendRequest(userId)
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        super.onDestroyView()
        _viewBinding = null
    }

    companion object {
        const val TAG = "AddFriendsFragment"
    }
}