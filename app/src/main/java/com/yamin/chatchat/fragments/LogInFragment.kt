package com.yamin.chatchat.fragments

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yamin.chatchat.R
import com.yamin.chatchat.databinding.FragmentLogInBinding

/**
 * A simple [Fragment] subclass.
 * Use the [LogInFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LogInFragment : Fragment() {

    private var _viewBinding: FragmentLogInBinding? = null
    private val viewBinding get() = _viewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onViewCreated")

        _viewBinding = FragmentLogInBinding.inflate(inflater, container, false)
        return  viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        viewBinding?.apply {

            goToSignUp.setOnClickListener {
                goToSignUpFragment()
            }

            logInButton.setOnClickListener {
                if(checkForLogInValidity()) {
                    Log.d(TAG, "Log In Successful")
                } else {
                    Log.d(TAG, "Log In Unsuccessful")

                }
            }
        }
    }

    private fun checkForLogInValidity(): Boolean {
        Log.d(TAG, "checkForLogInValidity")

        val valid = false
        viewBinding?.apply {
            val mEmailOrMobile = emailOrMobile.text.toString()
            val mPassword = logInPassword.text.toString()
            if(mEmailOrMobile.isBlank()) {
                emailOrMobileLayout.isErrorEnabled = true
                emailOrMobileLayout.error = getString(R.string.field_empty_error)
            } else {
                if (Patterns.EMAIL_ADDRESS.matcher(mEmailOrMobile).matches()) {

                } else if (Patterns.PHONE.matcher(mEmailOrMobile).matches()) {

                } else {
                    emailOrMobileLayout.isErrorEnabled = true
                    emailOrMobileLayout.error = getString(R.string.invalid_format_error)
                }
            }
            if(mPassword.isBlank()) {
                logInPasswordLayout.isErrorEnabled = true
                logInPasswordLayout.error = getString(R.string.field_empty_error)
            } else {

            }
        }

        return  valid
    }

    private fun goToSignUpFragment() {
        Log.d(TAG, "goToSignUpFragment")

        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.main_activity_fragment_container, SignUpFragment()).commit()
    }

    override fun onDestroyView() {
        Log.d(TAG, "onViewCreated")

        super.onDestroyView()
        _viewBinding = null
    }

    companion object {
        const val TAG = "LogInFragment"
    }
}