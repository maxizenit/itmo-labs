[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/CbUYJsts)
# Лабораторная работа 6: Тестирование производительности приложения Spring Music с помощью Apache JMeter

## Цели работы

- Ознакомиться с инструментом Apache JMeter для нагрузочного и функционального тестирования веб-приложений.
- Научиться создавать data-driven тесты с использованием CSV-файлов.
- Провести тестирование производительности основных REST API приложения Spring Music.
- Проанализировать результаты тестирования и сделать выводы о производительности приложения.

---

## Задание

Контроллер AlbumController предоставляет следующие REST API:
- GET	/albums	Получить список всех альбомов
- GET	/albums/{id}	Получить альбом по ID
- PUT	/albums	Добавить новый альбом
- POST	/albums	Обновить существующий альбом
- DELETE	/albums/{id}	Удалить альбом по ID

### 1. Подготовка

- Склонируйте репозиторий Spring Music и запустите приложение локально (например, на порту 8080)
- Убедитесь, что приложение работает и доступны основные API.
- Запустите Apache JMeter.
- Создайте новый тест-план.
- Добавьте Thread Group с 5 пользователями, 10 итерациями.
- Добавьте HTTP Request для запроса GET /artists.
- Добавьте Listener View Results Tree и Aggregate Report.
- Запустите тест и проанализируйте время отклика.

### 2. Расчет целевых показателей эффективности

На основе маркетинговых данных рассчитайте ключевые показатели нагрузки, которые будут использоваться для тестирования производительности.

Дано:

- В первый год планируется 1 000 000 пользователей.
- Средняя продолжительность сеанса пользователя — 3 минуты.
- Типичный пользователь посещает приложение 3 раза в месяц.
- Активность пользователей приходится на период с 00:00 до 6:00 (6 часов в сутки).

Вам необходимо:

- Рассчитать общее количество сеансов в месяц.
- Определить среднее количество пользователей в день.
- Определить среднее количество пользователей в час.
- Рассчитать количество одновременных пользователей, учитывая среднюю продолжительность сеанса.
- Оценить необходимую пропускную способность системы, если в среднем за сеанс пользователь выполняет 10 запросов к API.


### 3. Создание CSV-файлов с тестовыми данными
- album_ids.csv — список ID альбомов для GET и DELETE запросов:
- albums.csv — данные для создания и обновления альбомов:

### 4. Создание тест-плана в JMeter
#### 4.1. Настройка Thread Group
  - Количество потоков (пользователей) — рассчитанное в задаче 2.
  - Ramp-up — 30 секунд.
  - Loop Count — 5.

#### 4.2. Добавление CSV Data Set Config 
  - Для album_ids.csv с переменной id.
  - Для albums.csv с переменными id, title, artist, releaseYear, genre, trackCount, albumId.

#### 4.3. Создание HTTP Request Samplers

  - GET /albums — получить список всех альбомов.
  - GET /albums/{id} — получить альбом по ID (/albums/${id}).
  - PUT /albums — добавить новый альбом.
  ```
  Метод: PUT
  Заголовок: Content-Type: application/json
  Тело запроса (Body Data):
  
  {
  "id": "${id}",
  "title": "${title}",
  "artist": "${artist}",
  "releaseYear": "${releaseYear}",
  "genre": "${genre}",
  "trackCount": ${trackCount},
  "albumId": "${albumId}"
  }
  ```

  - POST /albums — обновить альбом.
  ```
  Метод: POST
  Заголовок: Content-Type: application/json
  Тело запроса (Body Data):
  
  {
  "id": "${id}",
  "title": "${title}",
  "artist": "${artist}",
  "releaseYear": "${releaseYear}",
  "genre": "${genre}",
  "trackCount": ${trackCount},
  "albumId": "${albumId}"
  }
  ```
  - DELETE /albums/{id} — удалить альбом по ID (/albums/${id}).

#### 4.4. Добавление Assertions
  - Проверка кода ответа (200 или 204 для DELETE).
  - Проверка отсутствия ошибок в теле ответа.

#### 4.5. Добавление Timers 
  - Uniform Random Timer с задержкой 1000–3000 мс для имитации пауз.

### 5. Запуск теста и анализ результатов

- Запустите тест.
- Соберите статистику в Aggregate Report и View Results Tree.
- Проанализируйте:
  - Среднее время отклика.
  - Процент ошибок.
  - Пропускную способность (requests per second).
- Сравните результаты с рассчитанными целевыми показателями.

### 6. Что надо будет показывать преподавателю
- Расчеты целевых показателей (задача 1).
- Тест-план в JMeter.
- Результаты тестирования и их анализ.
- Выводы