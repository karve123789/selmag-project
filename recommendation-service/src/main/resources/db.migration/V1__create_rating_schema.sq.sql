create schema if not exists rating;

CREATE TABLE IF NOT EXISTS rating.product_rating (
                                                     product_id INT PRIMARY KEY,
                                                     rating_average INT,
                                                     rating_count INT
);

INSERT INTO rating.product_rating (product_id, rating_average, rating_count) VALUES
                                                                                 (1, 4, 100),
                                                                                 (2, 5, 50),
                                                                                 (3, 3, 200)
