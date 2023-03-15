--liquibase formatted sql

--changeset Erofeev_N:3
create table user
(
    id        int          not null auto_increment primary key,
    user_name varchar(255) not null unique,
    password  varchar(255) not null,
    role_id   int          not null,
    foreign key (role_id) references role (id)
);