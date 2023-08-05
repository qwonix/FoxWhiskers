# Fox Whiskers (Усы Лисы)

Мобильное приложение, разработанное в рамках дипломной работы, защищённой на оценку отлично. Приложение позволяет изучить ассортимент пиццерии, добавить товары в корзину, 
создать аккаунт, оформить заказ, а также выбрать пункт выдачи на карте.

![Preview gif](docs/preview.gif "Пример работы")

## Технологии
* [Yandex MapKit](https://yandex.ru/dev/maps/mapkit)
* [Dagger Hilt](https://dagger.dev/hilt/)
* [Retrofit](https://square.github.io/retrofit/)
* [Picasso](https://square.github.io/picasso/)
* [ZXing](https://github.com/journeyapps/zxing-android-embedded)
* [Tinkoff Decoro](https://github.com/tinkoff-mobile-tech/decoro)

## Требования
Android версии 5 (Lollipop) и более. 50МБ свободного места в памяти устройства.

## Загрузка приложения

Последнюю версию приложения можно скачать на [странице релизов](https://github.com/qwonix/fox-whiskers/releases).

## Источники

Идея и дизайн приложения основаны на реально существующей петербургской пиццерии [«Усы Лисы»](https://vk.com/usilisi_sennaya).

## Использование

Для работы приложения необходимо запустить
сервер [Fox Whiskers API](https://github.com/qwonix/fox-whiskers-api)
и указать его адрес в `apikey.properties`. А также указать
ключ [Yandex MapKit](https://developer.tech.yandex.ru/services/)
