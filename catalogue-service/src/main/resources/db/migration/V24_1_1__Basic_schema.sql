-- Создаем схему для каталога, если она еще не существует
CREATE SCHEMA IF NOT EXISTS catalogue;

-- Создаем таблицу продуктов в схеме каталога
CREATE TABLE IF NOT EXISTS catalogue.t_product (
                                                   id        SERIAL PRIMARY KEY,
                                                   c_title   VARCHAR(50) NOT NULL CHECK (length(trim(c_title)) >= 3),
    c_details VARCHAR(1000)
    );

-- Создаем схему для рейтингов, если она еще не существует
CREATE SCHEMA IF NOT EXISTS rating;

-- Создаем таблицу для агрегированных рейтингов и счетчика избранного
CREATE TABLE IF NOT EXISTS rating.product_rating (
                                                     product_id      INT PRIMARY KEY,          -- ID продукта, внешний ключ к catalogue.t_product.id (можно добавить CONSTRAINT)
                                                     rating_average  INT DEFAULT 0 NOT NULL,   -- Средний рейтинг (лучше с дефолтом 0)
                                                     rating_count    INT DEFAULT 0 NOT NULL,   -- Количество оценок (лучше с дефолтом 0)
                                                     favourite_count INT DEFAULT 0 NOT NULL    -- Количество добавлений в избранное (НОВАЯ КОЛОНКА)
);

-- Создаем таблицу для хранения каждой отдельной оценки
CREATE TABLE IF NOT EXISTS rating.product_rating_record (
                                                            id          SERIAL PRIMARY KEY,
                                                            product_id  INT NOT NULL,                -- ID продукта (можно добавить CONSTRAINT FK к product_rating.product_id или t_product.id)
                                                            rating      INT NOT NULL                 -- Оценка (например, от 1 до 5, можно добавить CHECK)
    -- user_id     VARCHAR(255)               -- Опционально: ID пользователя, поставившего оценку
    -- created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- Опционально: время создания оценки
);

-- Опционально: Добавление внешних ключей для целостности данных
-- ALTER TABLE rating.product_rating
-- ADD CONSTRAINT fk_product_rating_product FOREIGN KEY (product_id) REFERENCES catalogue.t_product(id) ON DELETE CASCADE;

-- ALTER TABLE rating.product_rating_record
-- ADD CONSTRAINT fk_product_rating_record_product FOREIGN KEY (product_id) REFERENCES catalogue.t_product(id) ON DELETE CASCADE;

-- Опционально: Индексы для ускорения запросов
-- CREATE INDEX idx_product_rating_record_product_id ON rating.product_rating_record(product_id);
-- create schema if not exists catalogue;
--
-- create table catalogue.t_product
-- (
--     id        serial primary key,
--     c_title   varchar(50) not null check (length(trim(c_title)) >= 3),
--     c_details varchar(1000)
-- );
--
-- create schema if not exists rating;
--
-- CREATE TABLE IF NOT EXISTS rating.product_rating (
--                                                      product_id INT PRIMARY KEY,
--                                                      rating_average INT,
--                                                      rating_count INT
-- );
--
-- CREATE TABLE IF NOT EXISTS rating.product_rating_record (
--                                                             id SERIAL PRIMARY KEY,
--                                                             product_id INT,
--                                                             rating INT
-- );