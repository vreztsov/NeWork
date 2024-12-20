package ru.vreztsov.nework.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.vreztsov.nework.R
import ru.vreztsov.nework.adapter.UsersAdapter
import ru.vreztsov.nework.databinding.FragmentUsersBinding
import ru.vreztsov.nework.util.listener.UserOnInteractionListener
import ru.vreztsov.nework.util.setBottomNavigationViewListener
import ru.vreztsov.nework.util.setTopAppBarListener
import ru.vreztsov.nework.viewmodel.UserViewModel

class UsersFragment : Fragment() {
    private lateinit var binding: FragmentUsersBinding
    private val viewModel: UserViewModel by activityViewModels()
    private lateinit var adapter: UsersAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUsersBinding.inflate(inflater, container, false)
        binding.bottomNavigation.selectedItemId = R.id.item_users
        adapter = createAdapter()
        binding.usersList.adapter = adapter
        subscribe()
        setListeners()
        return binding.root
    }

    private fun createAdapter() = UsersAdapter(
        onInteractionListener = object : UserOnInteractionListener {}
    )

    private fun subscribe() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.dataUsersList.collectLatest {
                    adapter.submitList(it)
                }
            }
        }
    }

    private fun setListeners() {
        binding.topAppBar.setTopAppBarListener(this, viewModel.isAuthorized)
        binding.bottomNavigation.setBottomNavigationViewListener(this)
    }
}