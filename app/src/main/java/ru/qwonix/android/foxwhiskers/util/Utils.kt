package ru.qwonix.android.foxwhiskers.util

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