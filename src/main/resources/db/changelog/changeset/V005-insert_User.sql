
--liquibase formatted sql

--changeset Erofeev_N:5
insert into diplom.user  (user_name, password ,role_id) values ('kolia', '$2a$10$827rq1ZTWfT4bwfjM5wDNuSayLdRLm7vngiQMfq/VAfTHTyQTS5S6','1');   -- pass:1234
insert into diplom.user  (user_name, password ,role_id) values ('ivan', '$2a$10$7ckZp2euyJyxWNXFUOGuQ.8/xiYDQR7TeAgzmwWSkJkd1QOKDdXTi','1');    -- pass:1234
insert into diplom.user  (user_name, password ,role_id) values ('Anton', '$2a$10$7ckZp2euyJyxWNXFUOGuQ.8/xiYDQR7TeAgzmwWSkJkd1QOKDdXTi','2');   -- pass:1234