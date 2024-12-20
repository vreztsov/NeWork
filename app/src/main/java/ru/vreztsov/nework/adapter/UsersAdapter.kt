package ru.vreztsov.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.vreztsov.nework.databinding.CardUserBinding
import ru.vreztsov.nework.databinding.FrameMiniAvatarBinding
import ru.vreztsov.nework.dto.User
import ru.vreztsov.nework.util.BindingUtils.initAvatar
import ru.vreztsov.nework.util.listener.UserOnInteractionListener


class UsersAdapter(
    private val isSelectable: Boolean = false,
    private val selectedUserIdList: List<Long>? = null,
    private val onInteractionListener: UserOnInteractionListener
) : ListAdapter<User, UserViewHolder>(UserDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return UserViewHolder(
            CardUserBinding.inflate(layoutInflater, parent, false),
            isSelectable,
            selectedUserIdList,
            onInteractionListener
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        getItem(position)?.let { user ->
            holder.bind(user)
        }

    }
}

class UserViewHolder(
    private val binding: CardUserBinding,
    private val isSelectable: Boolean,
    private val selectedUserIdList: List<Long>? = null,
    private val onInteractionListener: UserOnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User) {
        with(binding){
            userSelected.isVisible = isSelectable
            if (isSelectable) selectedUserIdList?.let {list ->
                userSelected.isChecked = list.contains(user.id)
            }
            name.text = user.name
            login.text = user.login
            initAvatar(FrameMiniAvatarBinding.bind(avatarFrame), user.name, user.avatar)
        }
        userListeners(user)
    }

    private fun userListeners(user: User) {
        with(binding){
            root.setOnClickListener {
                onInteractionListener.onClick(user)
            }
            userSelected.setOnCheckedChangeListener { _, isChecked ->
                onInteractionListener.onSelect(user, isChecked)
            }
        }
    }

}
