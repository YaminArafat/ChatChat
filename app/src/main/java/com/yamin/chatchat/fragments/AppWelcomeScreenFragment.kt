package com.yamin.chatchat.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yamin.chatchat.databinding.FragmentAppWelcomeScreenBinding


/**
 * A simple [Fragment] subclass.
 * Use the [AppWelcomeScreenFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AppWelcomeScreenFragment : Fragment() {
    private var _viewBinding: FragmentAppWelcomeScreenBinding? = null
    private val viewBinding get() = _viewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = FragmentAppWelcomeScreenBinding.inflate(inflater, container, false)
        return viewBinding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
    companion object {
        const val TAG = "AppWelcomeScreenFragment"
    }
}