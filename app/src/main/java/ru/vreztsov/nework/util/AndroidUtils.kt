package ru.vreztsov.nework.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import ru.vreztsov.nework.dto.Attachment
import ru.vreztsov.nework.dto.AttachmentType
import ru.vreztsov.nework.dto.Post

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

    fun getExtensionFromUri(uri: Uri, contentResolver: ContentResolver): String? {
        val mimeType = contentResolver.getType(uri) ?: return null
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
    }

    fun withCoordinates(
        post: Post?,
        receiver: (latitude: Double, longitude: Double) -> Unit
    ): Unit? {
        post?.coords?.let {
            val ltt = it.lat?.toDoubleOrNull()
            val lgt = it.long?.toDoubleOrNull()
            if (ltt != null && lgt != null) {
                receiver(ltt, lgt)
                return Unit
            }
        }
        return null
    }

    fun getAttachment(url: String?, type: AttachmentType?): Attachment? {
        if (url != null && type != null){
            return Attachment(url, type)
        }
        return null
    }

    fun equalsAttachment(attachment: Attachment?, url: String?, type: AttachmentType?): Boolean {
        return if (attachment == null) {
            url == null && type == null
        } else {
            attachment.url == url && attachment.type == type
        }
    }
}