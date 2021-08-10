INSERT INTO roles(`name`)
VALUES ('ROLE_USER'),
       ('ROLE_ADMIN');
INSERT INTO users(`username`, `password`, `balance`, `name`, `surname`, `phone`, `discount`, `role_id`)
VALUES ('nick', '$2a$10$FqMqlNj0UT85V/Vqf50VZ./VgG9bFcCc0BgNFK3K8Zg019wFl.uMO', 10000.00, 'Name', 'Surname',
        '+380998877666', 0.0, 1),
       ('test', '$2a$10$FqMqlNj0UT85V/Vqf50VZ./VgG9bFcCc0BgNFK3K8Zg019wFl.uMO', 0.00, 'Name', 'Surname',
        '+380555555555', 0.0, 1);
INSERT INTO taxi_statuses(`name`)
VALUES ('AVAILABLE'),
       ('BUSY'),
       ('INACTIVE');
INSERT INTO taxi_classes(`name`, `multiplier`)
VALUES ('ECONOMY', 1.0),
       ('COMFORT', 1.3),
       ('BUSINESS', 1.5);
INSERT INTO drivers(`name`, `surname`, `phone`)
VALUES ('Oleksandr', 'Petrov', '+380333333333'),
       ('Ivan', 'Ivanov', '+380112211221'),
       ('John', 'Stevenson', '+380775544333'),
       ('Peter', 'Johnson', '+380998822111');
INSERT INTO taxi(`info`, `taxi_class_id`, `capacity`, `taxi_status_id`, `driver_id`)
VALUES ('Black BMW; Numbers: AA1111AA', 1, 1, 1, 1),
       ('Black Mercedes; Numbers: XX0000XX', 3, 2, 1, 2),
       ('Black Audi; Numbers: XA0110AX', 3, 3, 1, 3);
INSERT INTO order_statuses(`name`)
VALUES ('ACTIVE'),
       ('DONE'),
       ('CANCELED');