package com.yamin.chatchat.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.yamin.chatchat.R
import com.yamin.chatchat.databinding.CustomDialogPopupBinding
import com.yamin.chatchat.databinding.FragmentSignUpBinding
import com.yamin.chatchat.viewmodels.UserViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [SignUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignUpFragment : Fragment() {

    private lateinit var mContext: Context

    // View Binding
    private var _viewBinding: FragmentSignUpBinding? = null
    private var customDialogPopupBinding: CustomDialogPopupBinding? = null
    private val viewBinding get() = _viewBinding

    // View Models
    private lateinit var userViewModel: UserViewModel

    private val cameraActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val intent = result.data
                intent?.data?.let {
                    userViewModel.setImageFilePath(it.path)
                }
                val imageBitmap = intent?.extras?.get("data") as Bitmap
                viewBinding?.apply {
                    profilePic.setImageBitmap(imageBitmap)
                    profilePic.tag = "Camera"
                }
            }
        }
    private val galleryActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val intent = result?.data
                val uri = intent?.data
                userViewModel.setImageFilePath(uri?.path)
                viewBinding?.apply {
                    profilePic.setImageURI(uri)
                    profilePic.tag = "Gallery"
                }
            }
        }

    private val cameraPermissionActivity =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(mContext, getString(R.string.camera_permission_denied), Toast.LENGTH_SHORT).show()
            }
        }

    private val galleryPermissionActivity =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGallery()
            } else {
                Toast.makeText(mContext, getString(R.string.gallery_permission_denied), Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        Log.d(TAG, "onStart")

        super.onStart()

    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach")

        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView")

        customDialogPopupBinding = CustomDialogPopupBinding.inflate(LayoutInflater.from(mContext))

        _viewBinding = FragmentSignUpBinding.inflate(inflater, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated")

        super.onViewCreated(view, savedInstanceState)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        viewBinding?.apply {
            profilePic.setOnClickListener {
                showDialogForChoose()
            }
            var pass = ""
            password.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (s.length < 8) {
                        passwordLayout.isErrorEnabled = true
                        passwordLayout.error = getString(R.string.password_length_short_error)
                    } else {
                        passwordLayout.error = null
                        passwordLayout.isErrorEnabled = false
                    }
                }

                override fun afterTextChanged(s: Editable) {
                    pass = s.toString()
                }
            })
            confirmPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable) {
                    Log.d(TAG, "Editable $s $pass")
                    if (s.toString() == pass) {
                        confirmPasswordLayout.error = null
                        confirmPasswordLayout.isErrorEnabled = false
                    } else {
                        confirmPasswordLayout.isErrorEnabled = true
                        confirmPasswordLayout.error = getString(R.string.password_not_match_error)
                    }
                }
            })
            goToLogIn.setOnClickListener {
                goToLogInFragment()
            }
            signUpButton.setOnClickListener {
                it.isEnabled = false
                if (validateDataAndSignUpUser()) {
                    Log.d(TAG, "Sign Up Successful")

                    goToLogInFragment()
                } else {
                    Log.d(TAG, "Sign Up Unsuccessful")
                    Toast.makeText(mContext, getString(R.string.sign_up_failed), Toast.LENGTH_SHORT).show()
                }
                it.isEnabled = true
            }
        }
    }

    private fun showDialogForChoose() {
        val customDialogPopup = AlertDialog.Builder(mContext).setView(customDialogPopupBinding?.root).create()
        customDialogPopupBinding?.apply {
            cameraButtonLayout.setOnClickListener {
                openCameraOrRequestPermission()
            }
            galleryButtonLayout.setOnClickListener {
                openGalleryOrRequestPermission()
            }
            cancelButton.setOnClickListener {
                customDialogPopup.dismiss()
            }
        }
        if (customDialogPopupBinding?.root?.parent != null) {
            (customDialogPopupBinding?.root?.parent as ViewGroup).removeView(customDialogPopupBinding?.root)
        }
        customDialogPopup.show()
    }

    private fun openGalleryOrRequestPermission() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            galleryPermissionActivity.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            openGallery()
        }
    }

    private fun openCameraOrRequestPermission() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            cameraPermissionActivity.launch(Manifest.permission.CAMERA)
        } else {
            openCamera()
        }
    }

    private fun goToLogInFragment() {
        Log.d(TAG, "goToLogInFragment")

        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.beginTransaction().replace(R.id.main_activity_fragment_container, LogInFragment()).commit()
    }

    private fun openCamera() {
        Log.d(TAG, "openCamera")

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraActivity.launch(intent)
    }

    private fun openGallery() {
        Log.d(TAG, "openGallery")

        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        galleryActivity.launch(intent)
    }

    private fun validateDataAndSignUpUser(): Boolean {
        Log.d(TAG, "validateDataAndSignUpUser")

        var valid = false
        viewBinding?.apply {
            val mEmail = email.text.toString()
            val mFirstName = firstName.text.toString()
            val mLastName = lastName.text.toString()
            val mMobile = mobile.text.toString()
            val mPassword = password.text.toString()
            val mConfirmPassword = confirmPassword.text.toString()

            Log.d(TAG, "Profile Pic ${profilePic.drawable} $ Email ${email.text}")

            if (profilePic.tag == "Default") {
                profilePicErrorText.visibility = View.VISIBLE
                profilePicErrorText.bringToFront()
                // Below part is for pre Kit-Kat devices 4.4
                val parent = profilePicErrorText.parent as? ViewGroup
                parent?.let {
                    it.requestLayout()
                    it.invalidate()
                }
            } else {
                profilePicErrorText.visibility = View.GONE
            }
            if (mEmail.isBlank()) {
                emailLayout.isErrorEnabled = true
                emailLayout.error = getString(R.string.field_empty_error)
            } else {
                if (Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
                    emailLayout.error = null
                    emailLayout.isErrorEnabled = false
                } else {
                    emailLayout.isErrorEnabled = true
                    emailLayout.error = getString(R.string.invalid_format_error)
                }
            }
            if (mFirstName.isBlank()) {
                firstNameLayout.isErrorEnabled = true
                firstNameLayout.error = getString(R.string.field_empty_error)
            } else {
                firstNameLayout.error = null
                firstNameLayout.isErrorEnabled = false
            }
            if (mLastName.isBlank()) {
                lastNameLayout.isErrorEnabled = true
                lastNameLayout.error = getString(R.string.field_empty_error)
            } else {
                lastNameLayout.error = null
                lastNameLayout.isErrorEnabled = false
            }
            if (mMobile.isBlank()) {
                mobileLayout.isErrorEnabled = true
                mobileLayout.error = getString(R.string.field_empty_error)
            } else {
                if (Patterns.PHONE.matcher(mMobile).matches()) {
                    mobileLayout.error = null
                    mobileLayout.isErrorEnabled = false
                } else {
                    mobileLayout.isErrorEnabled = true
                    mobileLayout.error = getString(R.string.invalid_format_error)
                }
            }
            if (mPassword.isBlank()) {
                passwordLayout.isErrorEnabled = true
                passwordLayout.error = getString(R.string.field_empty_error)
            } else {
                if (mPassword.length < 8) {
                    passwordLayout.isErrorEnabled = true
                    passwordLayout.error = getString(R.string.password_length_short_error)
                } else {
                    passwordLayout.error = null
                    passwordLayout.isErrorEnabled = false
                }
            }
            if (mConfirmPassword.isBlank()) {
                confirmPasswordLayout.isErrorEnabled = true
                confirmPasswordLayout.error = getString(R.string.field_empty_error)
            } else {
                if (mPassword != mConfirmPassword) {
                    confirmPasswordLayout.isErrorEnabled = true
                    confirmPasswordLayout.error = getString(R.string.password_not_match_error)
                } else {
                    confirmPasswordLayout.error = null
                    confirmPasswordLayout.isErrorEnabled = false
                }
            }
            if (profilePic.tag != "Default" && mEmail.isNotBlank() && mFirstName.isNotBlank() && mLastName.isNotBlank() && mMobile.isNotBlank() && mPassword.isNotBlank() && mPassword.length >= 8 && mPassword == mConfirmPassword) {
                valid = true
                userViewModel.signUp(mEmail, mPassword, mFirstName, mLastName, mMobile)
            }
        }
        Log.d(TAG, "Validated $valid")
        return valid
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        super.onDestroyView()
        _viewBinding = null
    }

    companion object {
        const val TAG = "SignUpFragment"
    }
}