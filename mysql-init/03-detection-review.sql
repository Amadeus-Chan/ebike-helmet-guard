USE detection_review;

create table camera
(
    id         bigint auto_increment
        primary key,
    location   varchar(1024) not null,
    secret_key varchar(36)   not null
);

create table review_task
(
    id             bigint auto_increment
        primary key,
    camera_id      bigint                                                                   not null,
    detected_image varchar(1024)                                                            not null,
    location       varchar(1024)                                          default 'UNKNOWN' not null,
    capture_time   datetime                                                                 not null,
    review_time    datetime                                                                 null,
    violated       tinyint(1)                                                               not null,
    status         enum ('PENDING', 'PROCESSING', 'PROCESSED', 'INVALID') default 'PENDING' not null,
    retry_count    int                                                    default 0         not null,
    constraint review_task_camera_id_fk
        foreign key (camera_id) references camera (id)
);

create table violation_result
(
    id             bigint auto_increment
        primary key,
    record_id      bigint       not null,
    license_plate  varchar(128) not null,
    violation_time datetime     not null,
    constraint violation_result_detection_record_id_fk
        foreign key (record_id) references review_task (id)
);
