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
import androidx.fragment.app.activityViewModels
import com.yamin.chatchat.R
import com.yamin.chatchat.activites.HomeActivity
import com.yamin.chatchat.databinding.FragmentLogInBinding
import com.yamin.chatchat.utils.DIM_VIEW
import com.yamin.chatchat.utils.NORMAL_VIEW
import com.yamin.chatchat.utils.Response
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
    private val userViewModel: UserViewModel by activityViewModels()

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
        Log.d(TAG, "onCreateView")

        _viewBinding = FragmentLogInBinding.inflate(inflater, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")

        observeLogInStatus()

        viewBinding?.apply {
            goToSignUp.setOnClickListener {
                goToSignUpFragment()
            }

            logInButton.setOnClickListener {
                Log.d(TAG, "logInButton clicked")
                launchHomeActivity("Yamin")
                /*showLogInProgress(true)
                validateDataAndLogin()*/
            }
        }
    }

    private fun observeLogInStatus() {
        Log.d(TAG, "observeLogInStatus")

        userViewModel.logInSuccessful.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Success -> {
                    Log.d(TAG, "Log In Successful ${response.data}")
                    showLogInProgress(false)
                    launchHomeActivity(response.data.second)
                }
                is Response.Error -> {
                    Log.d(TAG, "Log In Unsuccessful ${response.errorMessage}")
                    showLogInProgress(false)
                    Toast.makeText(
                        mContext,
                        getString(R.string.log_in_failed) + " " + response.errorMessage + getString(R.string.try_again),
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                    Log.d(TAG, "Log In progress $response")
                }
            }
        }
    }

    private fun showLogInProgress(progress: Boolean) {
        Log.d(TAG, "showLogInProgress")
        viewBinding?.apply {
            if (progress) {
                logInProgressBar.visibility = View.VISIBLE
                logInProgressBar.bringToFront()
                logInCredentialsLayout.alpha = DIM_VIEW
                emailOrMobile.isEnabled = false
                logInPassword.isEnabled = false
                goToSignUp.isEnabled = false
                logInButton.isClickable = false
            } else {
                logInProgressBar.visibility = View.GONE
                logInCredentialsLayout.alpha = NORMAL_VIEW
                emailOrMobile.isEnabled = true
                logInPassword.isEnabled = true
                goToSignUp.isEnabled = true
                logInButton.isClickable = true
            }
        }
    }

    private fun launchHomeActivity(userId: String?) {
        Log.d(TAG, "launchHomeActivity")
        if (userId != null) {
            val intent = Intent(mContext, HomeActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
            requireActivity().finish()
        } else {
            Toast.makeText(mContext, getString(R.string.log_in_failed) + " " + getString(R.string.try_again), Toast.LENGTH_LONG).show()
        }
    }

    private fun validateDataAndLogin() {
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
            } else {
                showLogInProgress(false)
            }
        }
    }

    private fun goToSignUpFragment() {
        Log.d(TAG, "goToSignUpFragment")

        val fragmentTransaction = parentFragmentManager.beginTransaction().replace(R.id.main_activity_fragment_container, SignUpFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")

        super.onDestroyView()
        userViewModel.resetLogInStatus()
        _viewBinding = null
    }

    companion object {
        const val TAG = "LogInFragment"
    }
}