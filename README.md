# Booking bot
Телеграм бот для бронирования чего угодно: переговорки, инвентарь, слоты на запись к специалисту и тд.

Java 17, Spring boot, PostgreSQL

Реализованы две роли:

- Пользователь – может забронировать что-то, посмотреть свои брони и отменить.
 
- Администратор – функционал пользователя, плюс возможность добавлять, редактировать и удалять объекты для бронирования.

Команды бота разделены по классам, реализующим общий интерфейс, так что можно легко добавлять новые пользовательские сценарии, не изменяя имеющиеся классы.

#### Регистрация нового пользователя. Сохраняется в базу данных вместе с Telegram user id.

<img src="https://github.com/EtoMisha/Booking_bot/blob/main/screenshots/1.jpg" height="600" /><br>

#### Бронирование

<img src="https://github.com/EtoMisha/Booking_bot/blob/main/screenshots/2.jpg" height="600" /> <img src="https://github.com/EtoMisha/Booking_bot/blob/main/screenshots/3.jpg" height="600" /><br>


#### Добавление объекта в каталог и редактирование прав пользователя (доступно только администратору)

<img src="https://github.com/EtoMisha/Booking_bot/blob/main/screenshots/4.jpg" height="600" /> <img src="https://github.com/EtoMisha/Booking_bot/blob/main/screenshots/5.jpg" height="600" /> <img src="https://github.com/EtoMisha/Booking_bot/blob/main/screenshots/6.jpg" height="600" /> 

