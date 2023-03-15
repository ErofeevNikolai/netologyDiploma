--liquibase formatted sql

--changeset Erofeev_N:3
create table authorization_status
(
    id      int auto_increment primary key,
    user_id int not null unique,
    token   varchar(255),
    foreign key (user_id) references user (id)

);
