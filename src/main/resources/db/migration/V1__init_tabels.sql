create table notice
(
    id            bigint auto_increment
        primary key,
    createdDate  datetime      null,
    modifiedDate datetime      null,
    adminId       varchar(255) null,
    displayStatus varchar(255) null,
    body          varchar(255) null,
    title         varchar(255) null
);