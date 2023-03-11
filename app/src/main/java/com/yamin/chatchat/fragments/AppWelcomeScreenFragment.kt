package com.yamin.chatchat.fragments

import android.os.Bundle
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
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = FragmentAppWelcomeScreenBinding.inflate(inflater, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding?.logInButton?.setOnClickListener {
            launchLogInFragment()
        }
        viewBinding?.signUpButton?.setOnClickListener {
            launchSignUpFragment()
        }
    }

    private fun launchSignUpFragment() {
        val signUpFragment = SignUpFragment()
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction().replace(R.id.main_activity_fragment_container, signUpFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun launchLogInFragment() {
        val logInFragment = LogInFragment()
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction().replace(R.id.main_activity_fragment_container, logInFragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
    companion object {
        const val TAG = "AppWelcomeScreenFragment"
    }
}