--liquibase formatted sql

--changeset Erofeev_N:4
insert into diplom.role  (role) values ('ROLE_USER');
insert into diplom.role  (role) values ('ROLE_ADMIN');