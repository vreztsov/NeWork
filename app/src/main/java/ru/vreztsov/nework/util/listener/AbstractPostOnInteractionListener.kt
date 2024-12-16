package ru.vreztsov.nework.util.listener

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.view.View
import android.widget.ImageButton
import android.widget.MediaController
import android.widget.VideoView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import ru.vreztsov.nework.R
import ru.vreztsov.nework.dto.AttachmentType
import ru.vreztsov.nework.dto.Post
import ru.vreztsov.nework.util.goToLogin
import ru.vreztsov.nework.viewmodel.PostViewModel

abstract class AbstractPostOnInteractionListener(
    private val fragment: Fragment,
    private val viewModel: PostViewModel,
    private val mediaPlayer: MediaPlayer
): PostsOnInteractionListener {
    override fun onLike(post: Post) {
        if (!viewModel.isAuthorized) {
            goToLogin(fragment)
            return
        }
        viewModel.likeById(post.id)
    }

    override fun onEdit(post: Post) {
        viewModel.edit(post)
        //TODO реализовать редактирование
    }

    override fun onRemove(post: Post) {
        viewModel.removeById(post.id)
        //TODO реализовать удаление
    }

    override fun onShare(post: Post) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, post.content)
            type = "text/plain"
        }

        val shareIntent =
            Intent.createChooser(intent, fragment.getString(R.string.chooser_share_post))
        fragment.startActivity(shareIntent)
    }

    override fun onPlayVideo(post: Post, videoView: VideoView, button: ImageButton) {
        if (post.attachment?.type == AttachmentType.VIDEO) {
            videoView.apply {
                isVisible = true
                setVideoURI(Uri.parse(post.attachment.url))
                setMediaController(MediaController(fragment.requireContext()))
                setOnPreparedListener {
                    layoutParams?.height =
                        (resources.displayMetrics.widthPixels *
                                (it.videoHeight.toDouble() / it.videoWidth)).toInt()
                    start()
                    button.visibility = View.GONE
                }

                setOnCompletionListener {
                    layoutParams?.let {
                        it.width = resources.displayMetrics.widthPixels
                        it.height = (it.width * 0.5625).toInt()
                    }
                    stopPlayback()
                    button.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onPlayAudio(post: Post, button: ImageButton) {
        post.attachment
            ?.takeIf { it.type == AttachmentType.AUDIO }
            ?.let {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                    button.setImageResource(R.drawable.ic_play_circle)
                } else {
                    mediaPlayer.reset()
                    mediaPlayer.setDataSource(it.url)
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                    button.setImageResource(R.drawable.ic_pause_circle)
                }
            }
    }

    override fun onPostCLick(post: Post) {

    }
}