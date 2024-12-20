package ru.vreztsov.nework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.vreztsov.nework.R
import ru.vreztsov.nework.adapter.UsersAdapter
import ru.vreztsov.nework.databinding.FragmentUserWallBinding
import ru.vreztsov.nework.dto.User
import ru.vreztsov.nework.util.BundleArguments.Companion.userId
import ru.vreztsov.nework.util.BundleArguments.Companion.userIdList
import ru.vreztsov.nework.util.BundleArguments.Companion.userWallType
import ru.vreztsov.nework.util.UserWallType.LIKERS_LIST
import ru.vreztsov.nework.util.UserWallType.MENTIONED_LIST
import ru.vreztsov.nework.util.UserWallType.USERS_CHOOSING
import ru.vreztsov.nework.util.listener.UserOnInteractionListener
import ru.vreztsov.nework.viewmodel.UserViewModel

class UserWallFragment : Fragment() {
    private lateinit var binding: FragmentUserWallBinding
    private lateinit var adapter: UsersAdapter
    private val userMap: MutableMap<Long, Boolean> = hashMapOf()
    val viewModel: UserViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserWallBinding.inflate(inflater, container, false)
        arguments?.userWallType?.let { userWallType ->
            when (userWallType) {
                USERS_CHOOSING -> {
                    arguments?.userIdList?.let {
                        it.forEach { id -> userMap[id] = true }
                    }
                    val listener = object : UserOnInteractionListener {
                        override fun onSelect(user: User, isChecked: Boolean) {
                            userMap[user.id] = isChecked
                        }
                    }
                    adapter = UsersAdapter(true, arguments?.userIdList, listener)
                    binding.wall.adapter = adapter
                    lifecycleScope.launch {
                        viewModel.dataUsersList.collectLatest {
                            adapter.submitList(it)
                        }
                    }
                    binding.topAppBar.addMenuProvider(object : MenuProvider {
                        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                            menuInflater.inflate(R.menu.editor_top_app_bar, menu)
                        }

                        override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                            when (menuItem.itemId) {
                                R.id.save -> {
                                    viewModel.saveSelected(userMap
                                        .filter { entry -> entry.value }
                                        .keys
                                        .toList())
                                    findNavController().navigateUp()
                                    true
                                }

                                else -> false
                            }
                    })
                }

                LIKERS_LIST, MENTIONED_LIST -> {
                    val listener = object : UserOnInteractionListener {
                        override fun onClick(user: User) {
                            findNavController().navigate(R.id.action_userWallFragment_to_userFragment,
                                Bundle().apply {
                                    userId = user.id
                                })
                        }
                    }
                    adapter = UsersAdapter(onInteractionListener = listener)
                    binding.wall.adapter = adapter
                    arguments?.userIdList?.let { list ->
                        lifecycleScope.launch {
                            viewModel.dataUsersList.collectLatest {
                                adapter.submitList(
                                    it.filter {user ->
                                        list.contains(user.id)
                                    }.sortedBy {
                                        user -> list.indexOf(user.id)
                                    }
//                                    list.mapNotNull { id ->
//                                        it.find { user ->
//                                            user.id == id
//                                        }
//                                    }
                                )
                            }
                        }
                    }
                }
            }
            with(binding) {
                topAppBar.title = userWallType.title
                topAppBar.setNavigationOnClickListener {
                    findNavController().navigateUp()
                }
            }
        }

        return binding.root
    }
}