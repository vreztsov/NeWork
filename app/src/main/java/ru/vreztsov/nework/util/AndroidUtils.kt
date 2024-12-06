package ru.vreztsov.nework.util

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.FragmentActivity

object AndroidUtils {
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
}