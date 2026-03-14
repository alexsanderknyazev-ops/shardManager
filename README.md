# Shard Manager

Сервис для работы с шардированной БД: каталог (маппинг credit_id → shard), маршрутизация запросов по шардам, поиск кредита по всем шардам.

## Требования

- Java 17+
- PostgreSQL (несколько инстансов для шардов + один для каталога)

## Запуск

```bash
./gradlew bootRun
```

Перед запуском подними БД (порты 5433–5437 в `application.yaml`) и создай схему каталога:

```sql
CREATE TABLE credit_shard_mapping (
    credit_id BIGINT PRIMARY KEY,
    shard_name VARCHAR(50) NOT NULL
);
```

На каждом шарде — таблица `credits` (JPA-сущность Credit).

## Конфигурация

- `sharding.shards` — мапа шардов (url, username, password, hikari).
- `sharding.catalog` — БД каталога.
- `sharding.search-timeout-seconds` — таймаут поиска по шардам (по умолчанию 5).

## API

- `GET /api/credit/{id}` — получить кредит (маршрут по каталогу или поиск по шардам).
- `POST /api/credit/create` — создать кредит (тело: CreditDto), запись в каталог.
- `DELETE /api/credit/{id}` — удалить кредит и запись из каталога.
- `GET /api/shard` — список шардов.
- `GET /api/shard/credit/{creditId}` — шард, на котором лежит кредит.

## Health

- `GET /actuator/health` — состояние приложения и БД.

## Тесты

```bash
./gradlew test
```

Профиль `test`: H2 in-memory, один шард и каталог в одной БД.
