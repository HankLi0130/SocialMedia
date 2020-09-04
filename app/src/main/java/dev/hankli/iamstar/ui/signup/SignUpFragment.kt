package dev.hankli.iamstar.ui.signup

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.baoyz.actionsheet.ActionSheet
import dev.hankli.iamstar.R
import dev.hankli.iamstar.databinding.FragmentSignUpBinding
import dev.hankli.iamstar.ui.MainActivity
import dev.hankli.iamstar.utils.Consts.PERMISSION_CODE
import dev.hankli.iamstar.utils.Consts.PICK_FROM_CAMERA
import dev.hankli.iamstar.utils.Consts.REQUEST_CAMERA
import dev.hankli.iamstar.utils.Consts.REQUEST_SD_CARD
import dev.hankli.iamstar.utils.Consts.PICK_FROM_GALLERY as PICK_FROM_GALLERY1


class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private val signUpViewModel by viewModels<SignUpViewModel>()
    private lateinit var hostActivity: MainActivity
    private lateinit var imageUri: Uri
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hostActivity = activity as MainActivity
        binding = FragmentSignUpBinding.inflate(inflater, container, false).apply {
            viewModel = signUpViewModel
            lifecycleOwner = this@SignUpFragment
        }
        setupListeners()
        setupObservers()
        return binding.root
    }

    private fun setupObservers() {
        signUpViewModel.loading.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                hostActivity.showProgress(it)
            } else {
                hostActivity.hideProgress()
            }
        })
        signUpViewModel.alert.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                hostActivity.showDialog(it)
                signUpViewModel.doneShowingAlert()
            }
        })
        signUpViewModel.shouldNavigateToHome.observe(viewLifecycleOwner, Observer {
            if (it) {
                startActivity(Intent(context, MainActivity::class.java))
                hostActivity.finish()
                signUpViewModel.doneNavigatingToHomeFragment()
            }
        })
    }

    private fun setupListeners() {
        binding.etFullName.doAfterTextChanged { fullName ->
            signUpViewModel.fullName = fullName.toString()
        }
        binding.etPhone.doAfterTextChanged { phone ->
            signUpViewModel.phoneNumber = phone.toString()
        }
        binding.etEmail.doAfterTextChanged { email -> signUpViewModel.email = email.toString() }
        binding.etPassword.doAfterTextChanged { password ->
            signUpViewModel.password = password.toString()
        }
        binding.selectPhoto.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkPermissions()
            } else {
                showActionSett()
            }
        }
    }

    private fun showActionSett() {
        ActionSheet.createBuilder(context, childFragmentManager)
            .setCancelButtonTitle(R.string.cancel).setCancelableOnTouchOutside(true)
            .setOtherButtonTitles(
                getString(R.string.open_camera),
                getString(R.string.pick_from_gallery)
            ).setListener(object : ActionSheet.ActionSheetListener {
                override fun onOtherButtonClick(actionSheet: ActionSheet?, index: Int) {
                    if (index == 0) {
                        takePhoto()
                    } else {
                        pickFromGallery()
                    }
                }

                override fun onDismiss(actionSheet: ActionSheet?, isCancel: Boolean) {

                }
            }).show()
    }

    private fun pickFromGallery() {
        if (!(activity as MainActivity).checkPermissions(REQUEST_SD_CARD)) {
            return
        }
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_FROM_GALLERY1)
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_CODE
            )
        } else {
            showActionSett()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE) {
            if (permissions[0] == Manifest.permission.READ_EXTERNAL_STORAGE) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromGallery()
                }
            }
        }
    }

    private fun takePhoto() {

        if (!(activity as MainActivity).checkPermissions(REQUEST_CAMERA)) {
            return
        }
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your camera")
        imageUri = activity?.contentResolver!!.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
        )!!
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, PICK_FROM_CAMERA)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_FROM_CAMERA && resultCode == AppCompatActivity.RESULT_OK) {
            var bitmap: Bitmap? = null
            try {
                bitmap =
                    MediaStore.Images.Media.getBitmap(
                        activity?.contentResolver,
                        signUpViewModel.imageUri.value
                    )
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val path =
                MediaStore.Images.Media.insertImage(
                    activity?.contentResolver,
                    bitmap,
                    "Title",
                    null
                )

            signUpViewModel.setImageUri(Uri.parse(path))

        } else if (requestCode == PICK_FROM_GALLERY1 && resultCode == AppCompatActivity.RESULT_OK) {
            signUpViewModel.setImageUri(data?.data!!)
        }
    }
}