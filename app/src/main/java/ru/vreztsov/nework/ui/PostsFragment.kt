package ru.vreztsov.nework.ui

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.vreztsov.nework.R
import ru.vreztsov.nework.adapter.PostsAdapter
import ru.vreztsov.nework.databinding.FragmentPostsBinding
import ru.vreztsov.nework.dto.Post
import ru.vreztsov.nework.util.BundleArguments.Companion.post
import ru.vreztsov.nework.util.goToLogin
import ru.vreztsov.nework.util.listener.AbstractPostOnInteractionListener
import ru.vreztsov.nework.util.setBottomNavigationViewListener
import ru.vreztsov.nework.util.setTopAppBarListener
import ru.vreztsov.nework.viewmodel.PostViewModel


class PostsFragment : Fragment() {

    private lateinit var binding: FragmentPostsBinding
    private lateinit var adapter: PostsAdapter
    private val viewModel: PostViewModel by activityViewModels()
    private val mediaPlayer = MediaPlayer()
    //TODO не забудь про создание нового поста
    //TODO про обновление страницы и пагинацию тоже подумай

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
        object : AbstractPostOnInteractionListener(this, viewModel, mediaPlayer) {

            override fun onPostCLick(post: Post) {
                findNavController().navigate(
                    R.id.action_postsFragment_to_detailedPostFragment,
                    Bundle().apply {
                        this.post = post
                    }
                )
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