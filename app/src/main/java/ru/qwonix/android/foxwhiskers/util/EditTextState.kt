package ru.qwonix.android.foxwhiskers.util

import ru.qwonix.android.foxwhiskers.R

enum class EditTextState(
    val backgroundId: Int
) {
    IN_PROGRESS(R.drawable.rectangle_corners15_gray500_background),
    CORRECT(R.drawable.rectangle_green_stroke_corners15_gray500_background),
    INCORRECT(R.drawable.rectangle_red_stroke_corners15_gray500_background);

    fun isCorrect(): Boolean {
        return this == CORRECT
    }
}