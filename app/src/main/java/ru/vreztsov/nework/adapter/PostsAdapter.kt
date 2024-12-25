package ru.vreztsov.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.vreztsov.nework.databinding.CardPostBinding
import ru.vreztsov.nework.dto.Post
import ru.vreztsov.nework.util.BindingUtils.fillCommonPostViews
import ru.vreztsov.nework.util.BindingUtils.setCommonPostListeners
import ru.vreztsov.nework.util.listener.PostOnInteractionListener


class PostsAdapter(
    private val onInteractionListener: PostOnInteractionListener
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PostViewHolder(
            CardPostBinding.inflate(layoutInflater, parent, false),
            onInteractionListener
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        getItem(position)?.let { post ->
            holder.bind(post)
        }

    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: PostOnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(post: Post) {
        with(binding){
            mension.isCheckable = true // не общее
            mension.isClickable = false // не общее
        }
        fillCommonPostViews(binding.root, post)
        postListeners(post) // не общее
    }

    private fun postListeners(post: Post) {
        setCommonPostListeners(binding.root, post, onInteractionListener)
        with(binding) {
            share.setOnClickListener {// не общее
                onInteractionListener.onShare(post)
            }
            postCard.setOnClickListener {
                onInteractionListener.onPostCLick(post)
            }
            avatar.setOnClickListener {
                onInteractionListener.onAvatarCLick(post.authorId)
            }
        }
    }

}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        if (oldItem::class != newItem::class) return false
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}