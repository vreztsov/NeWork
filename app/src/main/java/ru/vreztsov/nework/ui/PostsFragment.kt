package ru.vreztsov.nework.ui

import android.content.Intent
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.vreztsov.nework.R
import ru.vreztsov.nework.adapter.PostsAdapter
import ru.vreztsov.nework.adapter.PostsOnInteractionListener
import ru.vreztsov.nework.databinding.FragmentPostsBinding
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
        }
    )

    private fun subscribe() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.data.collectLatest {
                    adapter.submitList(it)
                }
            }
        }
        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            //TODO пока пусто
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