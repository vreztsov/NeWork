package ru.vreztsov.nework.util.listener

import android.widget.ImageButton
import android.widget.VideoView
import ru.vreztsov.nework.dto.Post

interface PostOnInteractionListener {
    fun onLike(post: Post)
    fun onEdit(post: Post)
    fun onRemove(post: Post)
    fun onShare(post: Post)
    fun onPostCLick(post: Post)
    fun onPlayVideo(post: Post, videoView: VideoView, button: ImageButton)
    fun onPlayAudio(post: Post, button: ImageButton)
    

}