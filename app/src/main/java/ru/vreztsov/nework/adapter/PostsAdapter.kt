package ru.vreztsov.nework.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.VideoView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import ru.vreztsov.nework.R
import ru.vreztsov.nework.databinding.CardPostBinding
import ru.vreztsov.nework.dto.AttachmentType
import ru.vreztsov.nework.dto.Post
import ru.vreztsov.nework.util.DataViewTransform

interface PostsOnInteractionListener {
    fun onLike(post: Post)
    fun onEdit(post: Post)
    fun onRemove(post: Post)
    fun onShare(post: Post)
    fun onPostCLick(post: Post)
    fun onPlayVideo(post: Post, videoView: VideoView, button: ImageButton)
    fun onPlayAudio(post: Post, button: ImageButton)
    // fun onAttachClick()

}


class PostsAdapter(
    private val onInteractionListener: PostsOnInteractionListener
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
    private val onInteractionListener: PostsOnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {


    fun bind(post: Post) {
        with(binding) {
            author.text = post.author
            published.text = DataViewTransform.dataToTextView(post.published)
            content.text = post.content
            like.isChecked = post.likedByMe
            mension.isCheckable = true
            mension.isClickable = false
            mension.isChecked = post.mentionedMe
            mension.text = "${
                with(post.mentionIds) {
                    if (isNullOrEmpty()) "" else size
                }
            }"
            links.isVisible = DataViewTransform.checkLinkFormat(post.link)
            post.link?.let {
                links.text = it
            }
            like.text = "${
                with(post.likeOwnerIds) {
                    if (isNullOrEmpty()) "" else size
                }
            }"
            avatarInitial.text = post.author.take(1)
            Glide.with(root.context)
                .load(post.authorAvatar)
                .timeout(2_000)
                .circleCrop()
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        avatarFrame.background = AppCompatResources.getDrawable(
                            root.context,
                            R.drawable.shape_avatar_filled
                        )
                        avatarInitial.visibility = View.VISIBLE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        avatarFrame.background = AppCompatResources.getDrawable(
                            root.context,
                            R.drawable.shape_avatar_transparent
                        )
                        avatarInitial.visibility = View.GONE
                        return false
                    }
                })
                .into(avatar)
            menu.isVisible = post.ownedByMe
            //TODO attachment
            post.attachment?.run {
                attachmentContent.visibility = View.VISIBLE
                if (type == AttachmentType.IMAGE) {
                    Glide.with(root.context)
                        .load(url)
                        .error(R.drawable.ic_broken_image_24)
                        .apply(
                            RequestOptions.overrideOf(
                                root.resources.displayMetrics.widthPixels
                            )
                        )
                        .timeout(2_000)
                        .into(imageAttachment)
                }
                imageAttachment.isVisible = (type == AttachmentType.IMAGE)
                videoAttachment.isVisible = (type == AttachmentType.VIDEO)
                playButtonPost.isVisible = (type == AttachmentType.VIDEO)
                playButtonPostAudio.isVisible = (type == AttachmentType.AUDIO)
                descriptionAttachment.isVisible = (type == AttachmentType.AUDIO)
            } ?: {
                attachmentContent.visibility = View.GONE
            }

            postListeners(post)
        }
    }

    private fun postListeners(post: Post) {
        with(binding) {
            like.setOnClickListener {
                like.isChecked = !like.isChecked
                onInteractionListener.onLike(post)
            }
            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }
            playButtonPost.setOnClickListener {
                onInteractionListener.onPlayVideo(post, binding.videoAttachment, binding.playButtonPost)
            }
            playButtonPostAudio.setOnClickListener {
                onInteractionListener.onPlayAudio(post, binding.playButtonPostAudio)
            }
            //TODO attachment
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