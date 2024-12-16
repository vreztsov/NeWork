package ru.vreztsov.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.vreztsov.nework.databinding.FrameMiniAvatarBinding
import ru.vreztsov.nework.dto.User
import ru.vreztsov.nework.util.AndroidUtils
import ru.vreztsov.nework.util.AndroidUtils.MAX_AVATARS_LIST_SIZE

interface AvatarsOnInteractionListener {
    fun onAvatarClick()
    fun onPlusClick(userList: List<User>)
}

class AvatarAdapter(
    private val onInteractionListener: AvatarsOnInteractionListener
) : ListAdapter<User, UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return UserViewHolder(
            FrameMiniAvatarBinding.inflate(layoutInflater, parent, false),
            onInteractionListener,
            currentList
        )

    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        getItem(position)?.let { user -> holder.bind(user, position) }
    }

    fun submitList(idlist: List<Long>?, list: List<User>?) {
        submitList(idlist
            .takeIf { !it.isNullOrEmpty() }
            ?.mapNotNull { id ->
                list?.find { it.id == id }
            }
        )
    }
}

class UserViewHolder(
    private val binding: FrameMiniAvatarBinding,
    private val onInteractionListener: AvatarsOnInteractionListener,
    private val adapterList: List<User>
) : ViewHolder(binding.root) {
    fun bind(user: User, position: Int) {
        when (position) {
            in 0 until MAX_AVATARS_LIST_SIZE -> {
                AndroidUtils.initAvatar(binding, user.name, user.avatar)
                binding.avatar.setOnClickListener {
                    onInteractionListener.onAvatarClick()
                }
            }

            MAX_AVATARS_LIST_SIZE -> {
                AndroidUtils.initAvatar(binding, "+", null)
                binding.avatar.setOnClickListener {
                    onInteractionListener.onPlusClick(adapterList)
                }
            }
            else -> return
        }

    }
}

class UserDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        if (oldItem::class != newItem::class) return false
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}