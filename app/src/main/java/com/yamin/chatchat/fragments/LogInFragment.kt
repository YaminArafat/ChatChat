package com.yamin.chatchat.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.yamin.chatchat.R
import com.yamin.chatchat.activites.HomeActivity
import com.yamin.chatchat.databinding.FragmentLogInBinding
import com.yamin.chatchat.viewmodels.UserViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [LogInFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LogInFragment : Fragment() {

    private lateinit var mContext: Context

    // View Binding
    private var _viewBinding: FragmentLogInBinding? = null
    private val viewBinding get() = _viewBinding

    // View Models
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onViewCreated")

        _viewBinding = FragmentLogInBinding.inflate(inflater, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        viewBinding?.apply {


            goToSignUp.setOnClickListener {
                goToSignUpFragment()
            }

            logInButton.setOnClickListener {
                if (validateDataAndLogin()) {
                    Log.d(TAG, "Log In Successful")
                    val loggedInUserId = userViewModel.getCurrentUserId()
                    if (loggedInUserId != null) {
                        launchHomeActivity(loggedInUserId)
                    }
                } else {
                    Log.d(TAG, "Log In Unsuccessful")
                    Toast.makeText(mContext, getString(R.string.log_in_failed), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun launchHomeActivity(userId: String) {
        val intent = Intent(mContext, HomeActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun validateDataAndLogin(): Boolean {
        Log.d(TAG, "validateDataAndLogin")

        var valid = false
        viewBinding?.apply {
            val mEmailOrMobile = emailOrMobile.text.toString()
            val mPassword = logInPassword.text.toString()
            if (mEmailOrMobile.isBlank()) {
                emailOrMobileLayout.isErrorEnabled = true
                emailOrMobileLayout.error = getString(R.string.field_empty_error)
            } else {
                if (Patterns.EMAIL_ADDRESS.matcher(mEmailOrMobile).matches()) {
                    emailOrMobileLayout.error = null
                    emailOrMobileLayout.isErrorEnabled = false
                    valid = true
                } else if (Patterns.PHONE.matcher(mEmailOrMobile).matches()) {
                    emailOrMobileLayout.error = null
                    emailOrMobileLayout.isErrorEnabled = false
                    valid = true
                } else {
                    emailOrMobileLayout.isErrorEnabled = true
                    emailOrMobileLayout.error = getString(R.string.invalid_format_error)
                }
            }
            if (mPassword.isBlank()) {
                logInPasswordLayout.isErrorEnabled = true
                logInPasswordLayout.error = getString(R.string.field_empty_error)
                valid = false
            } else {
                logInPasswordLayout.error = null
                logInPasswordLayout.isErrorEnabled = false
            }
            if (valid) {
                userViewModel.logIn(mEmailOrMobile, mPassword)
            }
        }

        return valid
    }

    private fun goToSignUpFragment() {
        Log.d(TAG, "goToSignUpFragment")

        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction().replace(R.id.main_activity_fragment_container, SignUpFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
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