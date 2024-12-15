package ru.vreztsov.nework.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.google.gson.Gson
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.vreztsov.nework.R
import ru.vreztsov.nework.adapter.AvatarAdapter
import ru.vreztsov.nework.adapter.AvatarsOnInteractionListener
import ru.vreztsov.nework.databinding.FragmentDetailedPostBinding
import ru.vreztsov.nework.dto.Post
import ru.vreztsov.nework.util.AndroidUtils.fillCommonPostViews
import ru.vreztsov.nework.util.AndroidUtils.setCommonPostListeners
import ru.vreztsov.nework.util.BundleArguments.Companion.postAsJson
import ru.vreztsov.nework.util.listener.AbstractPostOnInteractionListener
import ru.vreztsov.nework.viewmodel.PostViewModel
import ru.vreztsov.nework.viewmodel.UserViewModel

class DetailedPostFragment : Fragment() {

    private lateinit var binding: FragmentDetailedPostBinding
    private val viewModel: PostViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val mediaPlayer = MediaPlayer()
    private lateinit var likeAdapter: AvatarAdapter
    private lateinit var mensionAdapter: AvatarAdapter
    private var post: Post? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailedPostBinding.inflate(inflater, container, false)
        post = arguments?.postAsJson?.let {
            Gson().fromJson(it, Post::class.java)
        }?.also {
            bind(binding.root, it)
        }
        return binding.root
    }

    private fun bind(view: ViewGroup, post: Post) {
        with(binding) {
            mension.isCheckable = true
            mension.isClickable = false
            fillCommonPostViews(view, post)
            val onInteractionListener = object : AvatarsOnInteractionListener {
                override fun onAvatarClick() {

                }

                override fun onPlusClick() {

                }
            }
            likeAdapter = AvatarAdapter(onInteractionListener)
            mensionAdapter = AvatarAdapter(onInteractionListener)
            likersList.adapter = likeAdapter
            mensionedList.adapter = mensionAdapter
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.CREATED) {
                    userViewModel.dataUsersList.collectLatest { usersList ->
                        likeAdapter.submitList(post.likeOwnerIds, usersList)
                        mensionAdapter.submitList(post.mentionIds, usersList)
                    }
                }
            }
        }
        postListeners(view, post)
    }

    private fun postListeners(view: View, post: Post) {
        val listener = object : AbstractPostOnInteractionListener(this, viewModel, mediaPlayer) {

        }
        setCommonPostListeners(view, post, listener)
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.share -> {
                    listener.onShare(post)
                    true
                }

                else -> false
            }
        }
        binding.topAppBar.setNavigationOnClickListener {
            view.findNavController().navigateUp()
        }
    }


}