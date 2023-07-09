package com.yamin.chatchat.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yamin.chatchat.R
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
        Log.d(TAG, "onCreate")

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")

        _viewBinding = FragmentAppWelcomeScreenBinding.inflate(inflater, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        viewBinding?.logInButton?.setOnClickListener {
            launchLogInFragment()
        }
        viewBinding?.signUpButton?.setOnClickListener {
            launchSignUpFragment()
        }
    }

    private fun launchSignUpFragment() {
        Log.d(TAG, "launchSignUpFragment")

        val fragmentTransaction = parentFragmentManager.beginTransaction().replace(R.id.main_activity_fragment_container, SignUpFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun launchLogInFragment() {
        Log.d(TAG, "launchLogInFragment")

        val fragmentTransaction = parentFragmentManager.beginTransaction().replace(R.id.main_activity_fragment_container, LogInFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }


    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
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
        const val TAG = "AppWelcomeScreenFragment"
    }
}