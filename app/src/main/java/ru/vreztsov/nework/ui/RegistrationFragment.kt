package ru.vreztsov.nework.ui

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import ru.vreztsov.nework.R
import ru.vreztsov.nework.auth.RegisterInfo
import ru.vreztsov.nework.databinding.FragmentRegistrationBinding
import ru.vreztsov.nework.util.AndroidUtils
import ru.vreztsov.nework.viewmodel.RegistrationViewModel
import java.io.File

@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    val viewModel by activityViewModels<RegistrationViewModel>()
    private lateinit var binding: FragmentRegistrationBinding
    private val pickImageLauncher = registerPickImageLauncher()
    private var imageFile: File? = null


    private val maxAvatarSize = 2048

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(layoutInflater, container, false)
        resetErrors()
        binding.registerPhotoImageView.setImageResource(R.drawable.ic_upload_photo)
        imageFile = null
        binding.registerButton.isEnabled = true
        subscribe()
        setListeners()
        return binding.root
    }

    private fun resetErrors() {
//        binding.photoInfoText.setTextColor(
//            ContextCompat.getColor(requireContext(), R.color.black)
//        )
//        val s1 = getString(R.string.photo_format_requirements)
//        val s2 = getString(R.string.photo_size_requirements)
//        val s = "$s1\n$s2"
//        binding.photoInfoText.text = s
        binding.photoInfoText.visibility = View.GONE
        viewModel.resetErrors()
    }

    private fun subscribe() {
        subscribeErrorMessages(
            hashMapOf(
                Pair(viewModel.loginErrorMessage, binding.layoutRegisterLoginTextField),
                Pair(viewModel.nameErrorMessage, binding.layoutRegisterNameTextField),
                Pair(viewModel.passwordErrorMessage, binding.layoutRegisterPasswordTextField),
                Pair(viewModel.password2ErrorMessage, binding.layoutRegisterPasswordTextField2)
            )
        )
        viewModel.registerSuccessEvent.observe(viewLifecycleOwner) {
            AndroidUtils.hideKeyboard(requireView())
            val navController = findNavController()
            val navUp = navController.navigateUp()
//            if (!navUp) {
//                // TODO переход в свой профиль
//            }
            Log.i(
                "RegisterFragment was leaved",
                if (navUp) "navigating up" else "navigating to profile"
            )
        }
        viewModel.registerError.observe(viewLifecycleOwner) { errText ->
            if (errText == null) return@observe
            showToast(errText)
            binding.registerButton.isEnabled = true
        }


    }

    private fun changePhoto(uri: Uri) {
        imageFile = uri.toFile()
//        with(binding) {
//            uri?.let {
        Glide.with(requireContext())
            .load(uri)
            .circleCrop()
            .into(binding.registerPhotoImageView)
//            } ?: registerPhotoImageView.setImageResource(R.drawable.ic_upload_photo)
//        }
    }

    private fun subscribeErrorMessages(
        map: Map<LiveData<Int?>, TextInputLayout>
    ) {
        map.keys.forEach { liveData ->
            liveData.observe(viewLifecycleOwner) { resId ->
                Log.d("RegisterFragment", "Register info: ${viewModel.registerInfo}")
                map.getValue(liveData).error = resId?.let {
                    getString(it)
                }
            }
        }
    }

    private fun setListeners() {
        binding.registerButton.setOnClickListener {
            AndroidUtils.hideKeyboard(requireView())
            resetErrors()
            viewModel.resetRegisterInfo(
                RegisterInfo(
                    username = binding.registerNameTextField.text.toString(),
                    login = binding.registerLoginTextField.text.toString(),
                    password = binding.registerPasswordTextField.text.toString(),
                    password2 = binding.registerPasswordTextField2.text.toString(),
                    avatar = imageFile
                )
            )
            if (viewModel.completed()) {
                binding.registerButton.isEnabled = false
                viewModel.doRegister()
            }
        }

        binding.registerPhotoImageView.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(maxAvatarSize)
                .provider(ImageProvider.GALLERY)
                .galleryMimeTypes(arrayOf("image/jpeg", "image/jpg", "image/png"))
                .createIntent(pickImageLauncher::launch)
        }
    }


    private fun registerPickImageLauncher() =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                ImagePicker.RESULT_ERROR -> {
                    binding.photoInfoText.text = ImagePicker.getError(it.data)
                }

                Activity.RESULT_OK -> {
                    it.data?.data?.let { uri -> changePhoto(uri) }
                }

            }
        }
//        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
////            try {
//                uri?.let {
//                    Glide.with(this)
//                        .load(uri)
//                        //TODO добавить проверку на формат, который ещё как-то найти нужно. В URI его нет.
//                        // Проверка на размеры тоже нужна. В Glide это видимо не предусмотрено
//                        .timeout(2_000)
//                        .circleCrop()
//                        .error(R.drawable.ic_upload_photo)
//                        .into(binding.registerPhotoImageView)
//                    imageUri = uri
//                }
////                    ?: Toast.makeText(requireContext(), "No Image Selected", Toast.LENGTH_SHORT)
////                .show()
////            } catch (e: ImageFormatException) {
////                binding.photoInfoText.setTextColor(
////                    ContextCompat.getColor(requireContext(), R.color.red_500)
////                )
////                binding.photoInfoText.setText(e.messageResId)
////            }
//        }

    private fun showToast(textInformation: String) {
        val warnToast = Toast.makeText(
            activity,
            textInformation,
            Toast.LENGTH_SHORT
        )
        warnToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        warnToast.show()
    }
}