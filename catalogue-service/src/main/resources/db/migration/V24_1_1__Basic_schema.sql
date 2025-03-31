create schema if not exists catalogue;

create table catalogue.t_product
(
    id        serial primary key,
    c_title   varchar(50) not null check (length(trim(c_title)) >= 3),
    c_details varchar(1000)
);

create schema if not exists rating;

CREATE TABLE IF NOT EXISTS rating.product_rating (
                                                     product_id INT PRIMARY KEY,
                                                     rating_average INT,
                                                     rating_count INT
);

CREATE TABLE IF NOT EXISTS rating.product_rating_record (
                                                            id SERIAL PRIMARY KEY,
                                                            product_id INT,
                                                            rating INT
);