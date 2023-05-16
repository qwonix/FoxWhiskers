package ru.qwonix.android.foxwhiskers.entity

import ru.qwonix.android.foxwhiskers.R

enum class OrderStatus(val title: String, val colorId: Int) {
    AWAITS_PAYMENT(
        "Ожидает оплаты",
        R.color.red
    ),
    CREATED(
        "Создан",
        R.color.gray_300
    ),
    IN_PROGRESS(
        "Готовится",
        R.color.gray_500
    ),
    READY_FOR_PICKUP(
        "Готов к получению",
        R.color.green
    ),
    CANCELLED(
        "Отменен",
        R.color.red
    ),
    RECEIVED(
        "Получен",
        R.color.yellow_300
    )

}