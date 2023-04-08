--liquibase formatted sql

--changeset Erofeev_N:6
create table diplom.upload_file
(
    id        int auto_increment primary key,
    user_id   int          not null,
    file_name varchar(255) not null,
    size      int not null,
    file      longblob not null,
    foreign key (user_id) references user (id)
);
