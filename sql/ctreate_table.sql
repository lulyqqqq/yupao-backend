CREATE TABLE `users`
(
    `id`           tinyint      NOT NULL AUTO_INCREMENT COMMENT '序号',
    `username`     varchar(255)          DEFAULT NULL COMMENT '用户名称',
    `userAccount`  varchar(255)          DEFAULT NULL COMMENT '用户账号',
    `avatar`       varchar(1024)         DEFAULT NULL COMMENT '用户头像',
    `gender`       tinyint               DEFAULT NULL COMMENT '用户性别',
    `userPassword` varchar(255) NOT NULL COMMENT '用户密码',
    `phone`        varchar(255)          DEFAULT NULL COMMENT '用户手机号',
    `email`        varchar(512)          DEFAULT NULL COMMENT '用户邮箱',
    `userStatus`   int                   DEFAULT '0' COMMENT '用户状态 0代表正常,1是异常',
    `createTime`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '用户创建时间',
    `updateTime`   timestamp    NULL     DEFAULT CURRENT_TIMESTAMP COMMENT '数据更新时间',
    `isDelete`     tinyint               DEFAULT '0' COMMENT '是否删除',
    `userRole`     int          NOT NULL DEFAULT '0' COMMENT '权限0--默认用户  1--管理员',
    `tags`         varchar(1024)         DEFAULT NULL COMMENT '用户标签',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 13
  DEFAULT CHARSET = utf8mb3;
alter table users
    add column tags varchar(1024) null comment '用户标签';


create table client_center.tag
(
    id         bigint auto_increment comment 'id'
        primary key,
    tagName    varchar(256)                        null comment '标签名称',
    userId     bigint                              null comment '用户账号',
    parentId   bigint                              null comment '父标签Id',
    isParent   tinyint                             null comment '0-不是,1 -父标签',
    createTime datetime  default CURRENT_TIMESTAMP not null comment '用户创建时间',
    updateTime timestamp default CURRENT_TIMESTAMP null comment '数据更新时间',
    isDelete   tinyint   default 0                 not null comment '是否删除',
    constraint uniIdx_tagName
        unique (tagName)
) comment '标签';

create index idx_userId
    on client_center.tag (userId);

create table team
(
    id           bigint auto_increment comment 'id'
        primary key,
    name   varchar(256)                   not null comment '队伍名称',
    description varchar(1024)                      null comment '描述',
    maxNum    int      default 1                 not null comment '最大人数',
    expireTime    datetime  null comment '过期时间',
    userId            bigint comment '用户id',
    status    int      default 0                 not null comment '0 - 公开，1 - 私有，2 - 加密',
    password varchar(512)                       null comment '密码',

    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete     tinyint  default 0                 not null comment '是否删除'
)
    comment '队伍';

create table user_team
(
    id           bigint auto_increment comment 'id'
        primary key,
    userId            bigint comment '用户id',
    teamId            bigint comment '队伍id',
    joinTime datetime  null comment '加入时间',
    createTime   datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete     tinyint  default 0                 not null comment '是否删除'
)
    comment '用户队伍关系';