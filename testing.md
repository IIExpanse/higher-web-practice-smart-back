# Развертывание и тестирование

Примечание: эндпоинты генерации берут sql/json из последнего сохраненного сообщения нейросети, вручную их вытаскивать не нужно.

При этом режим "свободного чата" не предусмотрен.<br>
Ожидается, что в каждом ответе нейросеть будет стараться генерировать валидный markdown с json сниппетом с sql-запросом или конфигом апи согласно системному промпту и релевантному запросу пользователя.

## Порядок развертывания и тестирования

0. В локальном окружении должны быть установлены Java 11+, Maven, docker и docker compose.

1. Выполнить из корня проекта ```./scripts/deploy.sh ``` для сборки сервиса и запуска проекта из docker-compose.

2. Попросить модель создать тестовую таблицу с пользователями.

На эндпоинт http://localhost:8080/api/chat отправить POST запрос 
```json
{
  "message": "Generate an SQL query to create table 'users' with fields login, e-mail, password_hash. All fields are non-null"
}
```
Первый запрос может занять несколько минут, если модель не инициализировалась до этого.<br>
Придет ответ нейросети (в `message`) и chatId. ChatId нужно сохранить, чтобы в дальнейшем использовать контекст нашего чата.

3. На эндпоинт http://localhost:8080/api/feature отправить POST запрос
```json
{
  "name": "myFeature"
}
```
и сохранить полученный featureId

4. Воспользоваться sql, который в п.2 сгенерировала нам модель.

На эндпоинт http://localhost:8080/api/generate/ddl отправить POST запрос

```json
{
  "featureId": "<featureId>",
  "chatId": "<chatId>"
}
```
В результате создастся таблица.<br>
Здесь и далее угловые кавычки `<>` означают, что нужно взять сохраненное ранее значение.

5. Попросить нейросеть сгенерировать запрос на наполнение таблицы тестовыми пользователями.

На эндпоинт http://localhost:8080/api/chat отправить POST запрос
```json
{
  "message": "Generate one SQL query to insert 10 test users into previously created table",
  "chatId": "<chatId>"
}
```
Сохранить email одного из пользователей, он понадобится в конце.

6. Воспользоваться sql, который сгенерировала нам модель.

На эндпоинт http://localhost:8080/api/generate/ddl отправить POST запрос

```json
{
  "featureId": "<featureId>",
  "chatId": "<chatId>"
}
```
В результате в таблице появятся данные.

7. Попросить модель сгенерировать конфиг апи под ранее созданную таблицу.

На эндпоинт http://localhost:8080/api/chat отправить POST запрос
```json
{
  "message": "Generate api config to get all fields except password_hash from users from previously created table using email as query parameter",
  "chatId": "<chatId>"
}
```

8. Воспользоваться конфигом для создания апи.

На эндпоинт http://localhost:8080/api/generate/api отправить POST запрос

```json
{
  "featureId": "<featureId>",
  "chatId": "<chatId>"
}
```
В ответе вернется apiId, его нужно сохранить.

9. Попробовать вызвать новый эндпоинт по пути и методу из ответа. Вернется ответ, что эндпоинт не привязан к какому-либо запросу.

10. Попросить модель сгенерировать sql для нашего эндпоинта.

На эндпоинт http://localhost:8080/api/chat отправить POST запрос
```json
{
  "message": "Generate sql query to select all fields except password_hash from users from previously created table using email for filter",
  "chatId": "<chatId>"
}
```

11. Подключить sql для ранее созданного эндпоинта.

На эндпоинт http://localhost:8080/api/generate/dml отправить POST запрос

```json
{
  "apiId": "<apiId>",
  "chatId": "<chatId>"
}
```

12. Вызвать новый эндпоинт еще раз, передав в качестве параметра запроса один из email ранее добавленных нами пользователей.
Должна вернуться мапа с полями этого пользователя.