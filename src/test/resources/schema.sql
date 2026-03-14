CREATE TABLE IF NOT EXISTS credit_shard_mapping (
    credit_id BIGINT PRIMARY KEY,
    shard_name VARCHAR(50) NOT NULL
);
