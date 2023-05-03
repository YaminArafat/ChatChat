package com.yamin.chatchat.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.yamin.chatchat.adapters.MyRequestsRecyclerViewAdapter
import com.yamin.chatchat.data.models.Friend
import com.yamin.chatchat.databinding.FragmentMyRequestsBinding
import com.yamin.chatchat.helpers.OnItemClickListener
import com.yamin.chatchat.utils.Response
import com.yamin.chatchat.viewmodels.FriendsViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [MyRequestsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyRequestsFragment : Fragment(), OnItemClickListener {

    private var _viewBinding: FragmentMyRequestsBinding? = null
    private val viewBinding get() = _viewBinding

    private val friendsViewModel: FriendsViewModel by activityViewModels()

    private lateinit var myRequestsRecyclerViewAdapter: MyRequestsRecyclerViewAdapter
    private lateinit var myRequestsList: ArrayList<Friend>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")

        _viewBinding = FragmentMyRequestsBinding.inflate(inflater, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        observeMyRequestsList()
        observeAcceptRequestStatus()
        myRequestsList = friendsViewModel.getMyRequestsList()
        viewBinding?.apply {
            myRequestsRecyclerViewAdapter = MyRequestsRecyclerViewAdapter(myRequestsList, this@MyRequestsFragment)
            myRequestsRecyclerView.layoutManager = LinearLayoutManager(context)
            myRequestsRecyclerView.adapter = myRequestsRecyclerViewAdapter
        }
    }

    private fun observeAcceptRequestStatus() {
        Log.d(TAG, "observeAcceptRequestStatus")
        friendsViewModel.acceptRequestStatus.observe(viewLifecycleOwner) {
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
            getMyRequestsList()
            getMyFriendsList()
        }
    }

    private fun observeMyRequestsList() {
        Log.d(TAG, "observeMyRequestsList")
        friendsViewModel.myRequestsList.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Success -> {
                    Log.d(TAG, "Response.Success ${it.data}")
                    myRequestsList = it.data
                    myRequestsRecyclerViewAdapter.updateData(myRequestsList)
                }
                is Response.Error -> {
                    ///TODO
                }
                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        super.onDestroyView()
        _viewBinding = null
    }

    companion object {
        const val TAG = "MyRequestsFragment"
    }

    override fun onClick(userId: String) {
        friendsViewModel.acceptFriendRequest(userId)
    }
}