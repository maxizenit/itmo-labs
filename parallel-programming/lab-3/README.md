# Распределенная обработка текстовых данных с использованием JMS

## Цель задания:
Реализовать систему для распределенной обработки текстовых данных с использованием Java Message Service (JMS)

## Шаги выполнения:

### Подготовка данных:
Загрузите или создайте набор текстовых данных. Это могут быть, например, книги, статьи или большой корпус текста. Разделите данные на секции для распределения между узлами.

### Разработка приложения:
Общая задача: Необходимо решить следующие задачи для обработки текстовых данных:
 * Подсчет количества слов в каждой секции текста.
 * Поиск наиболее часто встречающихся слов.
 * Анализ тональности текста (положительная, отрицательная, нейтральная) (алгоритм определения выберите сами)
 * Замена всех имен в тексте на другое, по вашему выбору
 * Сортировка предложений по длине

Реализуйте приложение, которое обрабатывает текстовые данные параллельно на нескольких узлах.\
Используйте JMS: Реализуйте отправку и прием сообщений для передачи данных между узлами. Каждый узел будет обрабатывать свою секцию текста и отправлять результаты для агрегации.

## Эксперименты и анализ результатов:
Оцените масштабируемость приложения. Используйте различные объемы данных и количество узлов для определения, насколько эффективно приложение масштабируется.

# Запуск программ

## Сборка

Сборка программ осуществляется при помощи команды `./gradlew bootJar`.

## Запуск программ напрямую

Для работы диспетчера и воркеров необходим работающий брокер ActiveMQ.
После этого можно запустить один экземпляр `dispatcher` и произвольное количество экземпляров `worker` - они сами зарегистрируют себя.

## Запуск программ через Docker Compose

В файле `.env` необходимо установить количество воркеров в переменной `WORKER_SCALE`. Затем необходимо вызвать команду `docker compose up`.

## Использование программы

Управление диспетчером осуществляется через HTTP-запросы. Их 3:
1. `POST /tasks` - создаёт задачу на обработку текста. Параметры запроса - `file` (обрабатываемый текстовый файл) и `nameReplacement` - строка, на которую заменяются все имена в файле.
2. `GET /tasks/{id}/result` - возвращает результат задачи с заданным `id`.
3. `GET /tasks/elapsedTime` - возвращает `Map<Integer, Long`, где ключ - `id` задачи, значение - затраченное на выполнение задачи время (в миллисекундах).

Для выполнения запросов через Postman можно импортировать коллекцию `postman_collection.json` в корневой папке проекта.