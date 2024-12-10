package ru.vreztsov.nework.util

import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.vreztsov.nework.R
import ru.vreztsov.nework.ui.EventsFragment
import ru.vreztsov.nework.ui.PostsFragment
import ru.vreztsov.nework.ui.UsersFragment

fun goToLogin(startFragment: Fragment) {
    val actionFromTo = when {
        (startFragment is PostsFragment) -> R.id.action_postsFragment_to_loginFragment
        else -> null
    }
    actionFromTo?.let {
        startFragment.findNavController().navigate(it)
    }
}

fun goToProfile(startFragment: Fragment) {
    val actionFromTo = when {
        (startFragment is PostsFragment) -> R.id.action_postsFragment_to_profileFragment
        (startFragment is EventsFragment) -> R.id.action_eventsFragment_to_profileFragment
        (startFragment is UsersFragment) -> R.id.action_usersFragment_to_profileFragment
        else -> null
    }
    actionFromTo?.let {
        startFragment.findNavController().navigate(it)
    }
}

fun MaterialToolbar.setTopAppBarListener(
    fragment: Fragment,
    isAuthorized: Boolean
) = setOnMenuItemClickListener {
    when (it.itemId) {
        R.id.viewProfile -> {
            if (isAuthorized) {
                goToProfile(fragment)
            } else {
                goToLogin(fragment)
            }
            true
        }

        else -> false
    }
}

fun BottomNavigationView.setBottomNavigationViewListener(
    fragment: Fragment
) = setOnItemSelectedListener {
    when (it.itemId) {
        R.id.item_posts -> {
            when (fragment) {
                is PostsFragment -> true
                is EventsFragment -> {
                    findNavController().navigate(R.id.action_eventsFragment_to_postsFragment)
                    true
                }

                is UsersFragment -> {
                    findNavController().navigate(R.id.action_usersFragment_to_postsFragment)
                    true
                }

                else -> false
            }
        }

        R.id.item_events -> {
            when (fragment) {
                is PostsFragment -> {
                    findNavController().navigate(R.id.action_postsFragment_to_eventsFragment)
                    true
                }

                is EventsFragment -> true
                is UsersFragment -> {
                    findNavController().navigate(R.id.action_usersFragment_to_eventsFragment)
                    true
                }

                else -> false
            }
        }

        R.id.item_users -> {
            when (fragment) {
                is PostsFragment -> {
                    findNavController().navigate(R.id.action_postsFragment_to_usersFragment)
                    true
                }

                is EventsFragment -> {
                    findNavController().navigate(R.id.action_eventsFragment_to_usersFragment)
                    true
                }

                is UsersFragment -> true
                else -> false
            }
        }

        else -> false
    }
}