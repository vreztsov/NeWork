package ru.vreztsov.nework.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.vreztsov.nework.R
import ru.vreztsov.nework.databinding.FrameMiniAvatarBinding
import ru.vreztsov.nework.dto.AttachmentType
import ru.vreztsov.nework.dto.Post
import ru.vreztsov.nework.util.listener.PostsOnInteractionListener

object AndroidUtils {

    const val MAX_AVATARS_LIST_SIZE = 5
    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun showToast(activity: FragmentActivity?, textInformation: String) {
        val warnToast = Toast.makeText(
            activity,
            textInformation,
            Toast.LENGTH_SHORT
        )
        warnToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
        warnToast.show()
    }

    fun fillCommonPostViews(view: ViewGroup, post: Post) {
        with(view) {
            findViewById<TextView>(R.id.author).text = post.author
            findViewById<TextView>(R.id.published).text =
                DataViewTransform.dataToTextView(post.published)
            findViewById<TextView>(R.id.content).text = post.content
            val like = findViewById<MaterialButton>(R.id.like)
            like.isChecked = post.likedByMe
            like.text = "${
                with(post.likeOwnerIds) {
                    if (isNullOrEmpty()) "" else size
                }
            }"
            val mension = findViewById<MaterialButton>(R.id.mension)
            mension.isChecked = post.mentionedMe
            mension.text = "${
                with(post.mentionIds) {
                    if (isNullOrEmpty()) "" else size
                }
            }"
            val links = findViewById<TextView>(R.id.links)
            links.isVisible = DataViewTransform.checkLinkFormat(post.link)
            post.link?.let {
                links.text = it
            }
            val avatarFrame = findViewById<FrameLayout>(R.id.avatar_frame)
            initAvatar(FrameMiniAvatarBinding.bind(avatarFrame), post.author, post.authorAvatar)
            findViewById<MaterialButton>(R.id.menu).isVisible = post.ownedByMe
            val attachmentContent = findViewById<ConstraintLayout>(R.id.attachment_content)
            val imageAttachment = findViewById<ImageView>(R.id.image_attachment)
            if (post.attachment != null) with(post.attachment) {
                attachmentContent.visibility = View.VISIBLE
                if (type == AttachmentType.IMAGE) {
                    Glide.with(imageAttachment)
                        .load(url)
                        .error(R.drawable.ic_broken_image_24)
                        .apply(
                            RequestOptions.overrideOf(
                                context.resources.displayMetrics.widthPixels
                            )
                        )
                        .timeout(2_000)
                        .into(imageAttachment)
                }
                imageAttachment.isVisible = (type == AttachmentType.IMAGE)
                findViewById<VideoView>(R.id.video_attachment).isVisible =
                    (type == AttachmentType.VIDEO)
                findViewById<FloatingActionButton>(R.id.play_button_post).isVisible =
                    (type == AttachmentType.VIDEO)
                findViewById<FloatingActionButton>(R.id.play_button_post_audio).isVisible =
                    (type == AttachmentType.AUDIO)
                findViewById<TextView>(R.id.description_attachment).isVisible =
                    (type == AttachmentType.AUDIO)
            } else {
                attachmentContent.visibility = View.GONE
            }
        }
    }

    fun initAvatar(binding: FrameMiniAvatarBinding, name: String, avatarUri: String?){
        val avatarInitial = binding.avatarInitial
        val avatar = binding.avatar
        val avatarFrame = binding.root
        avatarInitial.text = name.take(1)
        Glide.with(avatar)
            .load(avatarUri)
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
                        binding.root.context,
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
                        binding.root.context,
                        R.drawable.shape_avatar_transparent
                    )
                    avatarInitial.visibility = View.GONE
                    return false
                }
            })
            .into(avatar)
    }

    fun setCommonPostListeners(
        view: View,
        post: Post,
        listener: PostsOnInteractionListener
    ) {
        with(view) {
            val like = findViewById<MaterialButton>(R.id.like)
            like.setOnClickListener {
                like.isChecked = !like.isChecked
                listener.onLike(post)
            }
            val videoAttachment = findViewById<VideoView>(R.id.video_attachment)
            val playButtonPost = findViewById<FloatingActionButton>(R.id.play_button_post)
            playButtonPost.setOnClickListener {
                listener.onPlayVideo(
                    post,
                    videoAttachment,
                    playButtonPost
                )
            }
            val playButtonPostAudio =
                findViewById<FloatingActionButton>(R.id.play_button_post_audio)
            playButtonPostAudio.setOnClickListener {
                listener.onPlayAudio(post, playButtonPostAudio)
            }
        }
    }
}