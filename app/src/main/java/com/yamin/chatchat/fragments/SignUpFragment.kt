package com.yamin.chatchat.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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
import androidx.fragment.app.activityViewModels
import com.yamin.chatchat.R
import com.yamin.chatchat.databinding.CustomDialogPopupBinding
import com.yamin.chatchat.databinding.FragmentSignUpBinding
import com.yamin.chatchat.utils.DIM_VIEW
import com.yamin.chatchat.utils.NORMAL_VIEW
import com.yamin.chatchat.utils.Response
import com.yamin.chatchat.viewmodels.UserViewModel
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

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
    private val userViewModel: UserViewModel by activityViewModels()

    private val cameraActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val intent = result.data
                val imageBitmap = intent?.extras?.get("data") as Bitmap
                imageBitmap.let { bitmap ->
                    viewBinding?.apply {
                        profilePic.setImageDrawable(null)
                        profilePicErrorText.visibility = View.INVISIBLE
                        profilePic.setImageBitmap(bitmap)
                        profilePic.tag = "Camera"
                    }
                    userViewModel.setImageData(bitmapToByteArray(bitmap))
                }
            }
        }
    private val galleryActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val intent = result?.data
                val uri = intent?.data
                val imageBitmap = uriToBitmap(mContext, uri)
                imageBitmap?.let { bitmap ->
                    viewBinding?.apply {
                        profilePicErrorText.visibility = View.INVISIBLE
                        profilePic.setImageBitmap(bitmap)
                        profilePic.tag = "Gallery"
                    }
                    userViewModel.setImageData(bitmapToByteArray(bitmap))
                }
                /*val imageFilePath = uri?.path
                Log.d(TAG, "uri $uri imageFilePath $imageFilePath")
                val correctedImageFilePath = if (imageFilePath?.startsWith("/raw/") == true) {
                    imageFilePath.replace("/raw/", "")
                } else {
                    imageFilePath
                }
                Log.d(TAG, "correctedImageFilePath $correctedImageFilePath")
                userViewModel.setImageData(correctedImageFilePath)
                viewBinding?.apply {
                    profilePic.setImageURI(uri)
                    profilePic.tag = "Gallery"
                }*/
            }
        }

    private val cameraPermissionActivity =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d(TAG, "Camera permission granted")
                openCamera()
            } else {
                Log.d(TAG, "Camera permission not granted")
                Toast.makeText(mContext, getString(R.string.camera_permission_denied), Toast.LENGTH_SHORT).show()
            }
        }

    private val galleryPermissionActivity =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Log.d(TAG, "Gallery permission granted")
                openGallery()
            } else {
                Log.d(TAG, "Gallery permission granted")
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

    override fun onResume() {
        Log.d(TAG, "onResume")
        super.onResume()
    }

    override fun onPause() {
        Log.d(TAG, "onPause")
        super.onPause()
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
        observeSignUpStatus()

        viewBinding?.apply {
            profilePic.setOnClickListener {
                showDialogForChoose()
            }
            var pass = ""
            password.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (s.isNotEmpty() && s.length < 8) {
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
                    // Log.d(TAG, "Editable $s $pass")
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
                Log.d(TAG, "signUpButton clicked")
                showSignUpInProgress(true)
                validateDataAndSignUpUser()
            }
        }
    }

    private fun uriToBitmap(context: Context, uri: Uri?): Bitmap? {
        return try {
            uri?.let {
                val inputStream = context.contentResolver.openInputStream(uri)
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: FileNotFoundException) {
            Log.d(TAG, "FileNotFoundException ${e.message}")
            e.printStackTrace()
            Toast.makeText(mContext, e.message.toString(), Toast.LENGTH_SHORT).show()
            null
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        Log.d(TAG, "imageBitmap $bitmap outputStream $outputStream")
        return outputStream.toByteArray()
    }

    private fun showDialogForChoose() {
        Log.d(TAG, "showDialogForChoose")
        val customDialogPopup = AlertDialog.Builder(mContext).setView(customDialogPopupBinding?.root).create()
        customDialogPopupBinding?.apply {
            cameraButtonLayout.setOnClickListener {
                openCameraOrRequestPermission()
                customDialogPopup.dismiss()
            }
            galleryButtonLayout.setOnClickListener {
                openGalleryOrRequestPermission()
                customDialogPopup.dismiss()
            }
            cancelButton.setOnClickListener {
                customDialogPopup.dismiss()
            }
        }
        (customDialogPopupBinding?.root?.parent as? ViewGroup)?.removeView(customDialogPopupBinding?.root)
        customDialogPopup.show()
    }

    private fun openCameraOrRequestPermission() {
        Log.d(TAG, "checkCameraPermission")
        if (isCameraPermissionGranted()) {
            openCamera()
        } else {
            cameraPermissionActivity.launch(Manifest.permission.CAMERA)
        }
    }

    private fun isCameraPermissionGranted(): Boolean {
        Log.d(TAG, "isCameraPermissionGranted")
        return ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun openGalleryOrRequestPermission() {
        Log.d(TAG, "openGalleryOrRequestPermission")
        if (isGalleryPermissionGranted()) {
            openGallery()
        } else {
            galleryPermissionActivity.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun isGalleryPermissionGranted(): Boolean {
        Log.d(TAG, "isGalleryPermissionGranted")
        return ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
    }

    private fun goToLogInFragment() {
        Log.d(TAG, "goToLogInFragment")

        val fragmentTransaction = parentFragmentManager.beginTransaction().replace(R.id.main_activity_fragment_container, LogInFragment())
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
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

    private fun validateDataAndSignUpUser() {
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
                //passwordLayout.errorIconDrawable = null
            } else {
                if (mPassword.length < 8) {
                    passwordLayout.isErrorEnabled = true
                    passwordLayout.error = getString(R.string.password_length_short_error)
                  //  passwordLayout.errorIconDrawable = null
                } else {
                    passwordLayout.error = null
                    passwordLayout.isErrorEnabled = false
                }
            }
            if (mConfirmPassword.isBlank()) {
                confirmPasswordLayout.isErrorEnabled = true
                confirmPasswordLayout.error = getString(R.string.field_empty_error)
                //confirmPasswordLayout.errorIconDrawable = null
            } else {
                if (mPassword != mConfirmPassword) {
                    confirmPasswordLayout.isErrorEnabled = true
                    confirmPasswordLayout.error = getString(R.string.password_not_match_error)
                   // confirmPasswordLayout.errorIconDrawable = null
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
        if (!valid)
            showSignUpInProgress(false)
    }

    private fun observeSignUpStatus() {
        Log.d(TAG, "observeSignUpStatus")
        userViewModel.signUpSuccessful.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Response.Success -> {
                    Log.d(TAG, "Sign Up Successful ${response.data}")
                    showSignUpInProgress(false)
                    goToLogInFragment()
                }
                is Response.Error -> {
                    Log.d(TAG, "Sign Up Unsuccessful ${response.errorMessage}")
                    showSignUpInProgress(false)
                    Toast.makeText(mContext, getString(R.string.sign_up_failed) + " " + response.errorMessage + " " + getString(R.string.try_again), Toast.LENGTH_LONG).show()
                }
                else -> {
                    Log.d(TAG, "Sign Up in progress $response")
                }
            }
        }
    }

    private fun showSignUpInProgress(progress: Boolean) {
        Log.d(TAG, "showSignUpInProgress")
        viewBinding?.apply {
            if (progress) {
                signUpProgressBar.visibility = View.VISIBLE
                signUpProgressBar.bringToFront()
                signUpCredentialsLayout.alpha = DIM_VIEW
                profilePic.isEnabled = false
                email.isEnabled = false
                firstName.isEnabled = false
                lastName.isEnabled = false
                mobile.isEnabled = false
                password.isEnabled = false
                confirmPassword.isEnabled = false
                signUpButton.isClickable = false
                goToLogIn.isEnabled = false
            } else {
                signUpProgressBar.visibility = View.INVISIBLE
                signUpCredentialsLayout.alpha = NORMAL_VIEW
                profilePic.isEnabled = true
                email.isEnabled = true
                firstName.isEnabled = true
                lastName.isEnabled = true
                mobile.isEnabled = true
                password.isEnabled = true
                confirmPassword.isEnabled = true
                goToLogIn.isEnabled = true
                signUpButton.isClickable = true
             }
        }
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView")
        super.onDestroyView()
        userViewModel.resetSignUpStatus()
        _viewBinding = null
    }

    override fun onStop() {
        Log.d(TAG, "onStop")
        super.onStop()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        super.onDestroy()
    }

    companion object {
        const val TAG = "SignUpFragment"
    }
}