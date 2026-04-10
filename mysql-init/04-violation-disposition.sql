USE violation_disposition;

create table ebike
(
    id                  bigint auto_increment comment '车辆信息ID'
        primary key,
    license_plate       varchar(32)  not null comment '车牌号',
    feature_description varchar(500) not null comment '车辆特征描述',
    owner_name          varchar(64)  not null comment '所有人姓名',
    owner_id_card       varchar(18)  not null comment '身份证号码',
    owner_phone_number  varchar(20)  not null comment '手机号码',
    constraint license_plate
        unique (license_plate)
)
    comment '车辆信息表';

create table user
(
    id           bigint auto_increment
        primary key,
    username     varchar(128)                                        not null,
    password     varchar(255)                                        not null,
    real_name    varchar(50)                                         not null,
    id_card      varchar(25)                                         not null,
    phone_number varchar(20)                                         not null,
    role         enum ('USER', 'AUDITOR', 'ADMIN') default 'USER'    not null,
    status       enum ('ENABLED', 'DISABLED')      default 'ENABLED' not null,
    constraint user_uk_id_card
        unique (id_card),
    constraint user_uk_phone_number
        unique (phone_number),
    constraint user_uk_username
        unique (username)
)
    comment '用户表';

create table violation
(
    id             bigint auto_increment comment '违规记录ID'
        primary key,
    license_plate  varchar(32)                                                            not null comment '识别车牌号',
    location       varchar(512)                                                           not null comment '违规地点',
    evidence_image varchar(1024)                                                          not null comment '检测图片路径',
    time           datetime                                                               not null comment '检测时间',
    status         enum ('UNAPPEALED', 'APPEALING', 'APPEAL_APPROVED', 'APPEAL_REJECTED') not null comment '违规申诉状态'
)
    comment '违规记录表';

create table appeal
(
    id               bigint auto_increment comment '申诉申请ID'
        primary key,
    violation_id     bigint                                                           not null comment '关联违规记录ID',
    create_time      datetime                                    default (now())      not null comment '申诉创建时间',
    auditor_username varchar(128)                                                     not null comment '审核员username',
    status           enum ('PROCESSING', 'APPROVED', 'REJECTED') default 'PROCESSING' not null comment '申诉状态',
    process_time     datetime                                                         null comment '申诉处理时间',
    constraint appeal_pk
        unique (violation_id),
    constraint appeal_user_username_fk
        foreign key (auditor_username) references user (username),
    constraint appeal_violation_id_fk
        foreign key (violation_id) references violation (id)
)
    comment '用户申诉申请表';