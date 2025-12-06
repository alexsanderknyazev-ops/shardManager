-- Этот скрипт выполнится при первом запуске каждого контейнера
-- Создаем таблицы для шардирования

-- Таблица клиентов
CREATE TABLE IF NOT EXISTS clients (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    passport_number VARCHAR(20) UNIQUE NOT NULL,
    phone VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица кредитов
CREATE TABLE IF NOT EXISTS credits (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL REFERENCES clients(id),
    contract_number VARCHAR(50) UNIQUE NOT NULL,
    amount DECIMAL(15,2) NOT NULL CHECK (amount > 0),
    interest_rate DECIMAL(5,2) NOT NULL,
    term_months INTEGER NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Индексы для производительности
CREATE INDEX IF NOT EXISTS idx_clients_passport ON clients(passport_number);
CREATE INDEX IF NOT EXISTS idx_clients_phone ON clients(phone);
CREATE INDEX IF NOT EXISTS idx_credits_client_id ON credits(client_id);
CREATE INDEX IF NOT EXISTS idx_credits_contract ON credits(contract_number);
CREATE INDEX IF NOT EXISTS idx_credits_status ON credits(status);

-- Функция для логирования
CREATE OR REPLACE FUNCTION log_table_changes()
RETURNS TRIGGER AS $$
BEGIN
    RAISE NOTICE 'Table % changed: % operation', TG_TABLE_NAME, TG_OP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Триггер для логирования изменений (опционально)
-- CREATE TRIGGER log_clients_changes
-- AFTER INSERT OR UPDATE OR DELETE ON clients
-- FOR EACH ROW EXECUTE FUNCTION log_table_changes();