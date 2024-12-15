package ru.vreztsov.nework.util

import android.os.Bundle

const val POST_AS_JSON = "post_as_json"

class BundleArguments {


    companion object {
        var Bundle.postAsJson: String?
            set(value) = putString(POST_AS_JSON, value)
            get() = getString(POST_AS_JSON)

    }
}