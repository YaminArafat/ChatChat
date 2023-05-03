package com.yamin.chatchat.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yamin.chatchat.R
import com.yamin.chatchat.databinding.FragmentFriendsBinding

/**
 * A simple [Fragment] subclass.
 * Use the [FriendsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FriendsFragment : Fragment() {

    private var _viewBinding: FragmentFriendsBinding? = null
    private val viewBinding get() = _viewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")
        _viewBinding = FragmentFriendsBinding.inflate(inflater, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        switchToMyFriendsFragment()
        viewBinding?.apply {
            var currentFriendPageState = friends.text.toString()
            friends.setOnClickListener {
                if (currentFriendPageState == it.context.getString(R.string.add_friends)) {
                    friends.text = it.context.getString(R.string.my_friends)
                    currentFriendPageState = friends.text.toString()
                    switchToAddFriendsFragment()
                } else {
                    friends.text = it.context.getString(R.string.add_friends)
                    currentFriendPageState = friends.text.toString()
                    switchToMyFriendsFragment()
                }
            }
            var currentRequestPageState = ""
            requests.setOnClickListener {
                when (currentRequestPageState) {
                    it.context.getString(R.string.friend_requests) -> {
                        requests.text = it.context.getString(R.string.sent_requests)
                        currentRequestPageState = requests.text.toString()
                        switchToMyRequestsFragment()
                    }
                    it.context.getString(R.string.sent_requests) -> {
                        requests.text = it.context.getString(R.string.friend_requests)
                        currentRequestPageState = requests.text.toString()
                        switchToSentRequestsFragment()
                    }
                    else -> {
                        requests.text = it.context.getString(R.string.sent_requests)
                        currentRequestPageState = requests.text.toString()
                        switchToMyRequestsFragment()
                    }
                }
            }
        }
    }

    private fun switchToSentRequestsFragment() {
        val fragmentTransaction = childFragmentManager.beginTransaction().replace(R.id.friends_fragment_fragment_container, SentRequestsFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun switchToMyRequestsFragment() {
        val fragmentTransaction = childFragmentManager.beginTransaction().replace(R.id.friends_fragment_fragment_container, MyRequestsFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun switchToMyFriendsFragment() {
        val fragmentTransaction = childFragmentManager.beginTransaction().replace(R.id.friends_fragment_fragment_container, MyFriendsFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun switchToAddFriendsFragment() {
        val fragmentTransaction = childFragmentManager.beginTransaction().replace(R.id.friends_fragment_fragment_container, AddFriendsFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        super.onDestroyView()
        _viewBinding = null
    }

    companion object {
        const val TAG = "FriendsFragment"
    }
}