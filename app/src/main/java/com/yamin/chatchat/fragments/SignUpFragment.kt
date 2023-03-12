package com.yamin.chatchat.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.yamin.chatchat.R
import com.yamin.chatchat.databinding.FragmentSignUpBinding

/**
 * A simple [Fragment] subclass.
 * Use the [SignUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignUpFragment : Fragment() {
    private var _viewBinding: FragmentSignUpBinding? = null
    private val viewBinding get() = _viewBinding
    private val pickOptions =
        arrayOf(getString(R.string.camera), getString(R.string.gallery), getString(R.string.cancel))
    private val cameraActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val intent = result.data
                val imageBitmap = intent?.extras?.get("data") as Bitmap
                viewBinding?.profilePic?.setImageBitmap(imageBitmap)
            }
        }
    private val galleryActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val intent = result?.data
                val uri = intent?.data
                viewBinding?.profilePic?.setImageURI(uri)
            }
        }

    private val cameraPermissionActivity =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.camera_permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private val galleryPermissionActivity =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openGallery()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.gallery_permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = FragmentSignUpBinding.inflate(inflater, container, false)
        return viewBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding?.apply {
            profilePic.setOnClickListener {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle(getString(R.string.choose_image))
                builder.setItems(pickOptions) { dialog, item ->
                    when {
                        pickOptions[item] == getString(R.string.camera) -> {
                            if (ContextCompat.checkSelfPermission(
                                    requireContext(),
                                    Manifest.permission.CAMERA
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                cameraPermissionActivity.launch(Manifest.permission.CAMERA)
                            } else {
                                openCamera()
                            }
                        }
                        pickOptions[item] == getString(R.string.gallery) -> {
                            if (ContextCompat.checkSelfPermission(
                                    requireContext(),
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                galleryPermissionActivity.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            } else {
                                openGallery()
                            }
                        }
                        pickOptions[item] == getString(R.string.cancel) -> {
                            dialog.dismiss()
                        }
                    }
                }
            }
            var pass = ""
            password.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (s.length < 8) {
                        passwordLayout.error = getString(R.string.password_length_short_error)
                    } else {
                        passwordLayout.error = null
                    }
                }

                override fun afterTextChanged(s: Editable) {
                    pass = s.toString()
                }

            })
            confirmPassword.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable) {
                    if (s.equals(pass)) {
                        confirmPasswordLayout.error = null
                    } else {
                        confirmPasswordLayout.error = getString(R.string.password_not_match_error)
                    }
                }
            })
            signUpButton.setOnClickListener {
                if (checkForValidity()) {
                    TODO()
                }
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraActivity.launch(intent)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        galleryActivity.launch(intent)
    }

    private fun checkForValidity(): Boolean {
        var flag = false
        viewBinding?.apply {
            if (profilePic.drawable != null && email.text != null && firstName.text != null && lastName.text != null && mobile.text != null && password.text != null && confirmPassword.text != null) {
                profilePicErrorText.visibility = View.INVISIBLE
                emailLayout.error = null
                firstNameLayout.error = null
                lastNameLayout.error = null
                mobileLayout.error = null
                passwordLayout.error = null
                confirmPasswordLayout.error = null
                flag = true
            }
            if (profilePic.drawable == null) {
                profilePicErrorText.visibility = View.VISIBLE
            }
            if (email.text == null) {
                emailLayout.error = getString(R.string.field_empty_error)
            }
            if (firstName.text == null) {
                firstNameLayout.error = getString(R.string.field_empty_error)
            }
            if (lastName.text == null) {
                lastNameLayout.error = getString(R.string.field_empty_error)
            }
            if (mobile.text == null) {
                mobileLayout.error = getString(R.string.field_empty_error)
            }
            if (password.text == null) {
                passwordLayout.error = getString(R.string.field_empty_error)
            }
            if (confirmPassword.text == null) {
                confirmPasswordLayout.error = getString(R.string.field_empty_error)
            }
        }
        return flag
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    companion object {
        const val TAG = "SignUpFragment"
    }
}