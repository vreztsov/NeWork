package ru.vreztsov.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.vreztsov.nework.databinding.FrameMiniAvatarBinding
import ru.vreztsov.nework.dto.User
import ru.vreztsov.nework.util.AndroidUtils.MAX_AVATARS_LIST_SIZE
import ru.vreztsov.nework.util.BindingUtils.initAvatar

interface AvatarOnInteractionListener {
    fun onAvatarClick()
    fun onPlusClick(userList: List<User>)
}

class AvatarAdapter(
    private val onInteractionListener: AvatarOnInteractionListener
) : ListAdapter<User, AvatarViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return AvatarViewHolder(
            FrameMiniAvatarBinding.inflate(layoutInflater, parent, false),
            onInteractionListener,
            currentList
        )

    }

    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {
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

class AvatarViewHolder(
    private val binding: FrameMiniAvatarBinding,
    private val onInteractionListener: AvatarOnInteractionListener,
    private val adapterList: List<User>
) : ViewHolder(binding.root) {
    fun bind(user: User, position: Int) {
        when (position) {
            in 0 until MAX_AVATARS_LIST_SIZE -> {
                initAvatar(binding, user.name, user.avatar)
                binding.avatar.setOnClickListener {
                    onInteractionListener.onAvatarClick()
                }
            }

            MAX_AVATARS_LIST_SIZE -> {
                initAvatar(binding, "+", null)
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