package ru.vreztsov.nework.util

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.vreztsov.nework.R
import ru.vreztsov.nework.dto.Post
import ru.vreztsov.nework.ui.DetailedEventFragment
import ru.vreztsov.nework.ui.DetailedPostFragment
import ru.vreztsov.nework.ui.EventsFragment
import ru.vreztsov.nework.ui.PostsFragment
import ru.vreztsov.nework.ui.UserListFragment
import ru.vreztsov.nework.ui.UsersFragment
import ru.vreztsov.nework.util.BundleArguments.Companion.editType
import ru.vreztsov.nework.util.BundleArguments.Companion.post
import ru.vreztsov.nework.util.BundleArguments.Companion.userId

fun goToLogin(startFragment: Fragment) {
    val actionFromTo = when {
        (startFragment is PostsFragment) -> R.id.action_postsFragment_to_loginFragment
        else -> null
    }
    actionFromTo?.let {
        startFragment.findNavController().navigate(it)
    }
}

fun goToPostEditing(startFragment: Fragment, post: Post) {
    val bundle = Bundle().apply {
        this.editType = EditType.EDIT_POST
        this.post = post
    }
    val actionFromTo = when (startFragment) {
        is PostsFragment -> R.id.action_postsFragment_to_editPostFragment
        is DetailedPostFragment -> R.id.action_detailedPostFragment_to_editPostFragment
        else -> null
    }
    actionFromTo?.let {
        startFragment.findNavController().navigate(it, bundle)
    }
}

fun goToUser(startFragment: Fragment, userId: Long) {
    val actionFromTo = when (startFragment) {
        is PostsFragment -> R.id.action_postsFragment_to_userWallFragment
        is EventsFragment -> R.id.action_eventsFragment_to_userWallFragment
        is UsersFragment -> R.id.action_usersFragment_to_userWallFragment
        is DetailedPostFragment -> R.id.action_detailedPostFragment_to_userWallFragment
        is DetailedEventFragment -> R.id.action_detailedEventFragment_to_userWallFragment
        is UserListFragment -> R.id.action_userListFragment_to_userWallFragment
        else -> null
    }
    actionFromTo?.let {
        startFragment.findNavController().navigate(it, Bundle().apply { this.userId = userId })
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