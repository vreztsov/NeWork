package ru.vreztsov.nework.ui

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.MediaController
import android.widget.VideoView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.vreztsov.nework.R
import ru.vreztsov.nework.adapter.PostsAdapter
import ru.vreztsov.nework.adapter.PostsOnInteractionListener
import ru.vreztsov.nework.databinding.FragmentPostsBinding
import ru.vreztsov.nework.dto.AttachmentType
import ru.vreztsov.nework.dto.Post
import ru.vreztsov.nework.util.ARG_POST_ID
import ru.vreztsov.nework.util.goToLogin
import ru.vreztsov.nework.util.setBottomNavigationViewListener
import ru.vreztsov.nework.util.setTopAppBarListener
import ru.vreztsov.nework.viewmodel.PostViewModel


class PostsFragment : Fragment() {

    private lateinit var binding: FragmentPostsBinding
    private lateinit var adapter: PostsAdapter
    private val viewModel: PostViewModel by activityViewModels()
    private val mediaPlayer = MediaPlayer()
    //TODO приделай меню для правки своих постов
    //TODO не забудь про создание нового поста
//    TODO про обновление страницы и пагинацию тоже подумай

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPostsBinding.inflate(inflater, container, false)
        binding.bottomNavigation.selectedItemId = R.id.item_posts
        adapter = createAdapter()
        binding.postsList.adapter = adapter
        subscribe()
        setListeners()
        return binding.root
    }

    private fun createAdapter(): PostsAdapter = PostsAdapter(
        object : PostsOnInteractionListener {
            override fun onLike(post: Post) {
                if (!viewModel.isAuthorized) {
                    goToLogin(this@PostsFragment)
                    return
                }
                viewModel.likeById(post.id)
            }

            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            override fun onPostCLick(post: Post) {
                findNavController().navigate(
                    R.id.action_postsFragment_to_detailedPostFragment,
                    Bundle().apply {
                        putLong(ARG_POST_ID, post.id)
                    }
                )
            }

            override fun onPlayVideo(post: Post, videoView: VideoView, button: ImageButton) {
                if (post.attachment?.type == AttachmentType.VIDEO) {
                    videoView.apply {
                        isVisible = true
                        setVideoURI(Uri.parse(post.attachment.url))
                        setMediaController(MediaController(requireContext()))
                        setOnPreparedListener {
                            layoutParams?.height =
                                (resources.displayMetrics.widthPixels *
                                        (it.videoHeight.toDouble() / it.videoWidth)).toInt()
                            start()
                            button.visibility = View.GONE
//                            if (!isPlaying) {
//                                layoutParams?.height =
//                                    (resources.displayMetrics.widthPixels *
//                                            (it.videoHeight.toDouble() / it.videoWidth)).toInt()
//                                start()
//                                CoroutineScope(EmptyCoroutineContext).launch {
//                                    playButton.isChecked = true
//                                    delay(3_000)
//                                    playButton.visibility = View.INVISIBLE
//                                }
//                            } else {
//                                pause()
//                                playButton.isChecked = false
//                            }
                        }
//                        setOnClickListener {
//                            if (isPlaying) CoroutineScope(EmptyCoroutineContext).launch {
//                                playButton.visibility = View.VISIBLE
//                                delay(3_000)
//                                playButton.visibility = View.INVISIBLE
//                            }
//                        }
                        setOnCompletionListener {
                            layoutParams?.let {
                                it.width = resources.displayMetrics.widthPixels
                                it.height = (it.width * 0.5625).toInt()
                            }
                            stopPlayback()
                            button.visibility = View.VISIBLE
                        }
                    }
                }
            }

            override fun onPlayAudio(post: Post, button: ImageButton) {
                post.attachment
                    ?.takeIf { it.type == AttachmentType.AUDIO }
                    ?.let {
                        if (mediaPlayer.isPlaying) {
                            mediaPlayer.pause()
                            button.setImageResource(R.drawable.ic_play_circle)
                        } else {
                            mediaPlayer.reset()
                            mediaPlayer.setDataSource(it.url)
                            mediaPlayer.prepare()
                            mediaPlayer.start()
                            button.setImageResource(R.drawable.ic_pause_circle)
                        }
                    }
            }
        }
    )

    private fun subscribe() {
        viewModel.data.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            // TODO пока пусто
        }
    }

    private fun setListeners() {
        binding.topAppBar.setTopAppBarListener(this, viewModel.isAuthorized)
        binding.bottomNavigation.setBottomNavigationViewListener(this)
//        binding.bottomNavigation.selectedItemId = R.id.item_events
        binding.addNewPost.setOnClickListener {
            if (viewModel.isAuthorized) {
                //TODO переход к созданию нового поста
            } else {
                goToLogin(this)
            }
        }
    }
}