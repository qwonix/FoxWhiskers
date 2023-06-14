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
        val mapInitPosition = Point(59.939096, 30.315871)

        // simple email regex
        val EMAIL_REGEX = Regex("^[A-z0-9._%+-]+@[A-z0-9.-]+\\.[A-z]{2,}\$")
        val FIRSTNAME_REGEX = Regex("^[А-яA-z]+([ -][А-яA-z]+)*\$")
        val LASTNAME_REGEX = Regex("^[А-яA-z]+([-'][А-яA-z]+)*\$")


        fun isValidFirstName(firstName: String): Boolean {
            return FIRSTNAME_REGEX.matches(firstName)
        }

        fun isValidLastName(lastName: String): Boolean {
            return LASTNAME_REGEX.matches(lastName)
        }

        fun isValidEmail(email: String): Boolean {
            return EMAIL_REGEX.matches(email)
        }
    }
}


fun EditText.onSearch(callback: () -> Unit) {
    onImeAction(EditorInfo.IME_ACTION_SEARCH, callback)
}

fun EditText.onSend(callback: () -> Unit) {
    onImeAction(EditorInfo.IME_ACTION_SEND, callback)
}


fun EditText.onImeAction(imeAction: Int, callback: () -> Unit) {
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