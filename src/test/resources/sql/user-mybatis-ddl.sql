create table users (
                       created_date timestamp(6) not null,
                       id bigint not null,
                       last_modified_date timestamp(6) not null,
                       email varchar(255),
                       password varchar(255),
                       username varchar(255),
                       role enum ('ADMIN', 'USER'),
                       status enum ('ACTIVE','INACTIVE','PENDING'),
                       primary key (id)
);

CREATE SEQUENCE USERS_SEQ
    INCREMENT BY 10
    START WITH 10
    MAXVALUE 10000
    MINVALUE 10
    NOCYCLE
  CACHE 2;