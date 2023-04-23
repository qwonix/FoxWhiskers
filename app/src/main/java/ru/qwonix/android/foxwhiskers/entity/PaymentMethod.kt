package ru.qwonix.android.foxwhiskers.entity

import ru.qwonix.android.foxwhiskers.R

enum class PaymentMethod(val title: String, val drawableId: Int) {
    INAPP_ONLINE_CARD("Картой в приложении", R.drawable.inapp_online_card_payment),
    OFFLINE_CARD("Картой на кассе", R.drawable.offline_card_payment),
    CASH("Наличными", R.drawable.cash_payment),
    GOOGLE_PAY("", R.drawable.ic_google_pay_payment)

}