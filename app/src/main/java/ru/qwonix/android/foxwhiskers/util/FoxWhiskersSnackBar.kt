package ru.qwonix.android.foxwhiskers.util

import android.view.View
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import ru.qwonix.android.foxwhiskers.R

class FoxWhiskersSnackBar {
    companion object {
        fun make(
            view: View, text: CharSequence, @BaseTransientBottomBar.Duration duration: Int
        ): Snackbar {
            return Snackbar.make(view, text, duration)
                .setTextColor(view.resources.getColor(R.color.gray_900))
                .setBackgroundTint(view.resources.getColor(R.color.yellow_300))
        }
    }
}