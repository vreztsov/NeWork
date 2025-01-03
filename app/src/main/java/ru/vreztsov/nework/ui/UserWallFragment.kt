package ru.vreztsov.nework.ui

import android.graphics.drawable.Drawable
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
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.vreztsov.nework.adapter.JobsAdapter
import ru.vreztsov.nework.adapter.PostsAdapter
import ru.vreztsov.nework.databinding.FragmentUserWallBinding
import ru.vreztsov.nework.dto.Post
import ru.vreztsov.nework.dto.User
import ru.vreztsov.nework.util.BundleArguments.Companion.userId
import ru.vreztsov.nework.util.listener.AbstractPostOnInteractionListener
import ru.vreztsov.nework.viewmodel.JobViewModel
import ru.vreztsov.nework.viewmodel.PostViewModel
import ru.vreztsov.nework.viewmodel.UserViewModel

class UserWallFragment : Fragment() {
    private lateinit var binding: FragmentUserWallBinding
    private lateinit var postsAdapter: PostsAdapter
    private lateinit var jobsAdapter: JobsAdapter
    private lateinit var user: User
    private val mediaPlayer = MediaPlayer()
    private val postViewModel: PostViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val jobViewModel: JobViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserWallBinding.inflate(inflater, container, false)
        postsAdapter = createPostsAdapter()
//        jobsAdapter = createJobsAdapter()
        binding.postsList.adapter = postsAdapter
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                userViewModel.dataUsersList.collectLatest { userList ->
                    user = userList.find {
                        it.id == arguments?.userId
                    } ?: return@collectLatest
                    with(binding) {
                        topAppBar.title = user.name
                        tabs.selectTab(tabs.getTabAt(0))
                        Glide.with(avatar)
                            .load(user.avatar)
                            .apply(
                                RequestOptions.overrideOf(
                                    avatar.context.resources.displayMetrics.widthPixels
                                )
                            ).listener(object : RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    avatar.visibility = View.GONE
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable,
                                    model: Any,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    avatar.visibility = View.VISIBLE
                                    return false
                                }
                            })
                            .timeout(2_000)
                            .into(avatar)
                    }
                    postViewModel.loadUserWall(user.id)
                }
            }
        }

        subscribe()
        setListeners()
        return binding.root
    }


    private fun subscribe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                postViewModel.data.collectLatest { postList ->
                    postsAdapter.submitList(postList.filter { post ->
                        post.authorId == user.id
                    })
                }
            }
        }
    }

    private fun createPostsAdapter() =
        PostsAdapter(object : AbstractPostOnInteractionListener(
            this, postViewModel, mediaPlayer
        ) {
            override fun onEdit(post: Post) {
            }

            override fun onShare(post: Post) {
            }

            override fun onLike(post: Post) {
                postViewModel.onWallLikeById(post.authorId, post.id)
            }

            override fun onRemove(post: Post) {
            }

            override fun onAvatarCLick(userId: Long) {
            }
        })

    private fun createJobsAdapter() = JobsAdapter() //TODO listener

    private fun setListeners() {
        with(binding) {
            topAppBar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
//            tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//                override fun onTabSelected(tab: TabLayout.Tab?) {
//                    when (tab?.position) {
//                        0 -> {
////                            userRecyclerView.adapter = postsAdapter
//                            postViewModel.loadUserWall(user.id)
//                        }
//
//                        1 -> {
////                            userRecyclerView.adapter = jobsAdapter
//                            jobViewModel.loadUserJobs(user.id)
//                        }
//                    }
//                }
//
//                override fun onTabUnselected(tab: TabLayout.Tab?) {
//                }
//
//                override fun onTabReselected(tab: TabLayout.Tab?) {
//                    // todo onSelected или что???
//                }
//            })
        }
    }
}