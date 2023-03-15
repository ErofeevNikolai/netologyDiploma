--liquibase formatted sql

--changeset Erofeev_N:1
create table role
(
    id   int          not null auto_increment primary key,
    role varchar(255) not null unique
);
