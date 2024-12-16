package ru.vreztsov.nework.util

import android.os.Bundle
import com.google.gson.Gson
import ru.vreztsov.nework.dto.Post

const val POST_AS_JSON = "post_as_json"
const val USER_ID = "user_id"
const val USER_ID_LIST = "user_id_list"

class BundleArguments {

    companion object {
        private val gson: Gson = Gson()
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