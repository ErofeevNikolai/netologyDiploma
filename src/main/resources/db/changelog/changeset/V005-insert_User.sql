
--liquibase formatted sql

--changeset Erofeev_N:5
insert into diplom.user  (user_name, password ,role_id) values ('kolia', '1234','1');
insert into diplom.user  (user_name, password ,role_id) values ('ivan', '1234','1');
insert into diplom.user  (user_name, password ,role_id) values ('Anton', '1234','2');