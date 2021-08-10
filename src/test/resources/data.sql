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
VALUES ('Black BMW; Numbers: AA1111AA', 1, 1, 1, 1);