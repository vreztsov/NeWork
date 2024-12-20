package ru.vreztsov.nework.util

import android.os.Bundle
import com.google.gson.Gson
import ru.vreztsov.nework.dto.Post

const val EDIT_TITLE = "edit_title"
const val POST_AS_JSON = "post_as_json"
const val USER_ID = "user_id"
const val USER_ID_LIST = "user_id_list"
const val USERS_TITLE = "users_title"

class BundleArguments {

    companion object {
        private val gson: Gson = Gson()
        var Bundle.editType: EditType?
            set(value) = putString(EDIT_TITLE, value.toString())
            get() = getString(EDIT_TITLE)?.let {
                try {
                    EditType.valueOf(it)
                } catch (e: Exception) {
                    null
                }
            }

        var Bundle.userWallType: UserWallType?
            set(value) = putString(USERS_TITLE, value.toString())
            get() = getString(USERS_TITLE)?.let {
                try {
                    UserWallType.valueOf(it)
                } catch (e: Exception) {
                    null
                }
            }
        var Bundle.post: Post?
            set(value) = putString(POST_AS_JSON, gson.toJson(value))
            get() = gson.fromJson(getString(POST_AS_JSON), Post::class.java)

        var Bundle.userId: Long
            set(value) = putLong(USER_ID, value)
            get() = getLong(USER_ID)

        var Bundle.userIdList: List<Long>?
            set(value) = putLongArray(USER_ID_LIST, value?.toLongArray())
            get() = getLongArray(USER_ID_LIST)?.toList()

    }
}

enum class EditType(val title: String){
    NEW_POST("New Post"),
    EDIT_POST("Edit Post"),
    NEW_EVENT("New Event"),
    EDIT_EVENT("Edit Event")
}

enum class UserWallType(val title: String){
    LIKERS_LIST("Likers"),
    MENTIONED_LIST("Mentioned"),
    USERS_CHOOSING("Choose users")
}
