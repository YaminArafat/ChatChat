package com.yamin.chatchat.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yamin.chatchat.R

/**
 * A simple [Fragment] subclass.
 * Use the [ActiveFriendsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ActiveFriendsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_active_friends, container, false)
    }

    companion object {

    }
}