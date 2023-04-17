package ru.qwonix.android.foxwhiskers.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

class Utils {

    companion object {
        val DECIMAL_FORMAT = DecimalFormat("###,###.## ₽",
            DecimalFormatSymbols().apply {
                decimalSeparator = '.'
                groupingSeparator = ' '
            })
    }
}