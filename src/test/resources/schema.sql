DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS taxi_classes;
DROP TABLE IF EXISTS taxi_statuses;
DROP TABLE IF EXISTS drivers;
DROP TABLE IF EXISTS taxi;

CREATE TABLE roles
(
    id   INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(250) NOT NULL
);

CREATE TABLE users
(
    id       bigint PRIMARY KEY AUTO_INCREMENT NOT NULL,
    username character varying(26)      NOT NULL,
    password character varying          NOT NULL,
    balance  numeric(7, 2) DEFAULT 0.00 NOT NULL,
    name     character varying(20)      NOT NULL,
    surname  character varying(30)      NOT NULL,
    phone    character varying(13)      NOT NULL,
    discount numeric(3, 1) DEFAULT 0.0  NOT NULL,
    role_id  bigint                     NOT NULL,
    FOREIGN KEY (role_id) references roles (id),
    UNIQUE KEY users_username (username)
);

CREATE TABLE taxi_classes
(
    id         bigint PRIMARY KEY AUTO_INCREMENT NOT NULL,
    name       character varying NOT NULL,
    multiplier numeric(2, 1)
);

CREATE TABLE taxi_statuses
(
    id   bigint PRIMARY KEY AUTO_INCREMENT NOT NULL,
    name character varying NOT NULL
);

CREATE TABLE drivers
(
    id      bigint PRIMARY KEY AUTO_INCREMENT NOT NULL,
    name    character varying NOT NULL,
    surname character varying NOT NULL,
    phone   character varying NOT NULL
);

CREATE TABLE taxi
(
    id             bigint PRIMARY KEY AUTO_INCREMENT NOT NULL,
    info           text    NOT NULL,
    taxi_class_id  bigint  NOT NULL,
    capacity       integer NOT NULL,
    taxi_status_id bigint  NOT NULL,
    driver_id      bigint,
    FOREIGN KEY (taxi_class_id) references taxi_classes (id),
    FOREIGN KEY (taxi_status_id) references taxi_statuses (id),
    FOREIGN KEY (driver_id) references drivers (id)
);

CREATE TABLE order_statuses
(
    id   bigint PRIMARY KEY AUTO_INCREMENT NOT NULL,
    name character varying NOT NULL
);

CREATE TABLE orders
(
    id           bigint PRIMARY KEY AUTO_INCREMENT NOT NULL,
    user_id      bigint                    NOT NULL,
    status_id    bigint                    NOT NULL,
    total        numeric(7, 2)             NOT NULL,
    date         date DEFAULT CURRENT_DATE NOT NULL,
    address_from character varying         NOT NULL,
    address_to   character varying         NOT NULL,
    distance     numeric(5, 2)             NOT NULL,
    people       integer,
    FOREIGN KEY (user_id) references users (id)
);

CREATE TABLE order_taxi
(
    order_id bigint NOT NULL,
    taxi_id  bigint NOT NULL,
    PRIMARY KEY (order_id, taxi_id),
    FOREIGN KEY (order_id) references orders (id),
    FOREIGN KEY (taxi_id) references taxi (id)
);