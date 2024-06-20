create sequence roles_id_seq;
create table if not exists roles
(
    id   integer primary key,
    code varchar(255) unique
);

create sequence users_id_seq;
create table if not exists users
(
    id       integer primary key,
    role_id  integer references roles (id),
    username varchar(255) unique,
    password varchar(255)
);