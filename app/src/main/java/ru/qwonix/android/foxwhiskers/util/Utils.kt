package ru.qwonix.android.foxwhiskers.util

import android.content.Context
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.yandex.mapkit.geometry.Point
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class Utils {

    companion object {
        val DECIMAL_FORMAT = DecimalFormat("###,###.## ₽",
            DecimalFormatSymbols().apply {
                decimalSeparator = '.'
                groupingSeparator = ' '
            })

        // Saint-Petersburg
        val primaryCityPoint = Point(59.939096, 30.315871)
    }
}

fun EditText.onSearch(callback: () -> Unit) {
    onImeAction(callback, EditorInfo.IME_ACTION_SEARCH)
}

fun EditText.onSend(callback: () -> Unit) {
    onImeAction(callback, EditorInfo.IME_ACTION_SEND)
}


fun EditText.onImeAction(callback: () -> Unit, imeAction: Int) {
    setOnEditorActionListener { _, actionId, _ ->
        // hide keyboard
        post {
            val imm =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(windowToken, 0)
        }
        if (actionId == imeAction) {
            callback.invoke()
            return@setOnEditorActionListener true
        }
        false
    }
}

// https://stackoverflow.com/a/71587766
fun View.focusAndShowKeyboard() {
    /**
     * This is to be called when the window already has focus.
     */
    fun View.showTheKeyboardNow() {
        if (isFocused) {
            post {
                // We still post the call, just in case we are being notified of the windows focus
                // but InputMethodManager didn't get properly setup yet.
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }

    requestFocus()
    if (hasWindowFocus()) {
        // No need to wait for the window to get focus.
        showTheKeyboardNow()
    } else {
        // We need to wait until the window gets focus.
        viewTreeObserver.addOnWindowFocusChangeListener(
            object : ViewTreeObserver.OnWindowFocusChangeListener {
                override fun onWindowFocusChanged(hasFocus: Boolean) {
                    // This notification will arrive just before the InputMethodManager gets set up.
                    if (hasFocus) {
                        this@focusAndShowKeyboard.showTheKeyboardNow()
                        // It’s very important to remove this listener once we are done.
                        viewTreeObserver.removeOnWindowFocusChangeListener(this)
                    }
                }
            })
    }
}