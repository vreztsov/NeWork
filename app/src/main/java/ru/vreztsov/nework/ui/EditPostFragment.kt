package ru.vreztsov.nework.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
import ru.vreztsov.nework.R
import ru.vreztsov.nework.databinding.FragmentEditPostBinding
import ru.vreztsov.nework.dto.Attachment
import ru.vreztsov.nework.dto.AttachmentType
import ru.vreztsov.nework.util.AndroidUtils
import ru.vreztsov.nework.util.AndroidUtils.getExtensionFromUri
import ru.vreztsov.nework.util.BundleArguments.Companion.editType
import ru.vreztsov.nework.util.BundleArguments.Companion.post
import ru.vreztsov.nework.util.BundleArguments.Companion.userWallType
import ru.vreztsov.nework.util.EditType
import ru.vreztsov.nework.util.UserWallType
import ru.vreztsov.nework.viewmodel.PostViewModel
import ru.vreztsov.nework.viewmodel.UserViewModel
import java.io.File
import java.io.FileOutputStream

class EditPostFragment : Fragment() {

    private lateinit var binding: FragmentEditPostBinding
    private val viewModel: PostViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private var type: AttachmentType? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditPostBinding.inflate(inflater, container, false)
        with(binding) {
            val type: EditType? = arguments?.editType
            topAppBar.title = type?.title
            photoContainer.visibility = View.GONE
            when (type) {
                EditType.NEW_POST -> {

                }

                EditType.EDIT_POST -> {
                    val post = arguments?.post ?: return@with
                    editPost.setText(post.content)
                    editLink.setText(post.link)
//                    post.attachment?.let {
//                        photoContainer.isVisible = true
//                        Glide.with(photo)
//                            .load(it.url)
//                            .placeholder(
//                                when(it.type){
//                                    AttachmentType.AUDIO -> {
//                                        R.drawable.ic_baseline_audio_file_500
//                                    }
//                                    AttachmentType.VIDEO -> {
//                                        R.drawable.ic_baseline_video_library_500
//                                    }
//                                    else -> {
//                                        R.drawable.not_image_500
//                                    }
//                                }
//                            )
//                            .timeout(2_000)
//                            .into(photo)
//                    }
                }

                else -> return@with
            }
            subscribe()
            setListeners()
        }

        return binding.root
    }

    @SuppressLint("IntentReset")
    private fun setListeners() {
        with(binding) {
            topAppBar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            topAppBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.save -> {
                        viewModel.changeContent(
                            editPost.text.toString(),
                            editLink.text.toString()
                        )
                        viewModel.save()
                        AndroidUtils.hideKeyboard(requireView())
                        true
                    }

                    else -> false
                }
            }
            removePhoto.setOnClickListener {
                viewModel.changeMedia(null, null, null)
            }
            val pickPhotoLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    when (it.resultCode) {
                        ImagePicker.RESULT_ERROR -> {
                            Snackbar.make(
                                binding.root,
                                ImagePicker.getError(it.data),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }

                        Activity.RESULT_OK -> {
                            val uri: Uri? = it.data?.data
                            viewModel.changeMedia(uri, uri?.toFile(), AttachmentType.IMAGE)
                        }
                    }
                }
//            val pickMediaLauncher =
//                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//                    when (it.resultCode) {
//                        Activity.RESULT_OK -> {
//                            val uri: Uri? = it.data?.data
//                            val contentResolver = context?.contentResolver
//                            val inputStream =
//                                uri?.let { it1 -> contentResolver?.openInputStream(it1) }
//                            val audioBytes = inputStream?.readBytes()
//                            if (uri != null && contentResolver != null) {
//                                val extension = getExtensionFromUri(uri, contentResolver)
//                                val file =
//                                    File(context?.getExternalFilesDir(null), "input.$extension")
//                                FileOutputStream(file).use { outputStream ->
//                                    outputStream.write(audioBytes)
//                                    outputStream.flush()
//                                }
//                                viewModel.changeMedia(uri, file, type!!)
//                            }
//                        }
//
//                        else -> {
//                            Snackbar.make(
//                                binding.root,
//                                getString(R.string.error_upload),
//                                Snackbar.LENGTH_LONG
//                            ).show()
//                        }
//                    }
//                }
            val mediaLauncher =
                registerForActivityResult(ActivityResultContracts.GetContent()) {
                    it?.let { uri ->
                        context?.contentResolver?.let { contentResolver ->
                            val inputStream = contentResolver.openInputStream(uri)
                            val bytes = inputStream?.readBytes()
                            val extension = getExtensionFromUri(uri, contentResolver)
                            val file =
                                File(context?.getExternalFilesDir(null), "input.$extension")
                            FileOutputStream(file).use { outputStream ->
                                outputStream.write(bytes)
                                outputStream.flush()
                            }
                            type?.let { t ->
                                viewModel.changeMedia(uri, file, t)
                            }
                            inputStream?.close()
                        }
                    }
                }

            editPostBottomAppBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.attach_photo -> {
                        type = AttachmentType.IMAGE
                        ImagePicker.with(this@EditPostFragment)
                            .crop()
                            .compress(2048)
                            .provider(ImageProvider.BOTH)
                            .galleryMimeTypes(
                                arrayOf(
                                    "image/png",
                                    "image/jpeg",
                                )
                            ).createIntent(pickPhotoLauncher::launch)
                        true
                    }

                    R.id.attach -> {
                        attachmentSelector.isVisible = !attachmentSelector.isVisible
                        true
                    }

                    R.id.tag_users -> {
                        findNavController().navigate(R.id.action_editPostFragment_to_userWallFragment,
                            Bundle().apply
                            { userWallType = UserWallType.USERS_CHOOSING})
                        true
                    }

                    R.id.location -> {
                        findNavController().navigate(R.id.action_editPostFragment_to_mapFragment)
                        true
                    }

                    else -> false
                }
            }
            audioSelector.setOnClickListener {
                type = AttachmentType.AUDIO
                mediaLauncher.launch("audio/*")
            }
            videoSelector.setOnClickListener {
                type = AttachmentType.VIDEO
                mediaLauncher.launch("video/*")
            }

        }
    }

    private fun subscribe() {
        viewModel.postCreated.observe(viewLifecycleOwner) {
            viewModel.loadData()
            findNavController().navigateUp()
        }
        viewModel.media.observe(viewLifecycleOwner) {
            if (it.uri == null || it.attachmentType == null) {
                binding.photoContainer.isVisible = false
                return@observe
            } else {
                binding.photoContainer.isVisible = true
                Glide.with(binding.photo)
                    .load(it.uri)
                    .placeholder(
                        when (it.attachmentType) {
                            AttachmentType.AUDIO -> {
                                R.drawable.ic_baseline_audio_file_500
                            }

                            AttachmentType.VIDEO -> {
                                R.drawable.ic_baseline_video_library_500
                            }

                            else -> {
                                R.drawable.not_image_500
                            }
                        }
                    )
                    .timeout(2_000)
                    .into(binding.photo)
            }

        }
        userViewModel.selectedUsersList.observe(viewLifecycleOwner) {
            viewModel.changeMentioned(it)
        }
    }
}