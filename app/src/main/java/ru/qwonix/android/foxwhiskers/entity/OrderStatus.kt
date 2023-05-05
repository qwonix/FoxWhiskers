package ru.qwonix.android.foxwhiskers.entity

import ru.qwonix.android.foxwhiskers.R

enum class OrderStatus(val title: String, val drawableId: Int) {
    AWAITS_PAYMENT(
        "Ожидает оплаты",
        R.drawable.rectangle_corners15_gray500_background
    ),
    CREATED(
        "Создан",
        R.drawable.rectangle_corners15_gray100_background
    ),
    IN_PROGRESS(
        "Готовится",
        R.drawable.rectangle_corners15_yellow300_background
    ),
    READY_FOR_PICKUP(
        "Готов к получению",
        R.drawable.rectangle_corners15_white_background
    ),
    CANCELLED(
        "Отменен",
        R.drawable.rectangle_corners15_gray500_background
    ),
    RECEIVED(
        "Получен",
        R.drawable.rectangle_corners15_gray500_background
    )

}