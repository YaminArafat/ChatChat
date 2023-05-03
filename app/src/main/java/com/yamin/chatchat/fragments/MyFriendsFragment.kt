package com.yamin.chatchat.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.yamin.chatchat.adapters.MyFriendsRecyclerViewAdapter
import com.yamin.chatchat.data.models.Friend
import com.yamin.chatchat.databinding.FragmentMyFriendsBinding
import com.yamin.chatchat.helpers.OnItemClickListener
import com.yamin.chatchat.utils.Response
import com.yamin.chatchat.viewmodels.FriendsViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [MyFriendsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyFriendsFragment : Fragment(), OnItemClickListener {

    private var _viewBinding: FragmentMyFriendsBinding? = null
    private val viewBinding get() = _viewBinding

    private val friendsViewModel: FriendsViewModel by activityViewModels()

    private lateinit var myFriendsRecyclerViewAdapter: MyFriendsRecyclerViewAdapter
    private lateinit var myFriendsList: ArrayList<Friend>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")
        _viewBinding = FragmentMyFriendsBinding.inflate(inflater, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        observeMyFriendList()
        myFriendsList = friendsViewModel.getMyFriendsList()
        viewBinding?.apply {
            myFriendsRecyclerViewAdapter = MyFriendsRecyclerViewAdapter(myFriendsList, this@MyFriendsFragment)
            myFriendsRecyclerView.layoutManager = LinearLayoutManager(context)
            myFriendsRecyclerView.adapter = myFriendsRecyclerViewAdapter
        }
    }

    private fun observeMyFriendList() {
        Log.d(TAG, "onViewCreated")
        friendsViewModel.myFriendList.observe(viewLifecycleOwner) {
            when (it) {
                is Response.Success -> {
                    Log.d(TAG, "Response.Success ${it.data}")
                    myFriendsList = it.data
                    myFriendsRecyclerViewAdapter.updateData(myFriendsList)
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
        const val TAG = "MyFriendsFragment"
    }

    override fun onClick(userId: String) {
        friendsViewModel.startChat(userId)
    }
}