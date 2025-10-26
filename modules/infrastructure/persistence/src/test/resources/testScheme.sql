create table if not exists partner
(
    id     bigint auto_increment primary key,
    code   varchar(64)  not null unique,
    name   varchar(255) not null,
    active boolean      not null default true
);

create table if not exists partner_fee_policy
(
    id             bigint auto_increment primary key,
    partner_id     bigint         not null,
    effective_from timestamp      not null,
    percentage     decimal(10, 6) not null,
    fixed_fee      decimal(15, 0) null
);

create table if not exists payment
(
    id               bigint auto_increment primary key,
    partner_id       bigint         not null,
    amount           decimal(15, 0) not null,
    applied_fee_rate decimal(10, 6) not null,
    fee_amount       decimal(15, 0) not null,
    net_amount       decimal(15, 0) not null,
    card_bin         varchar(8)     null,
    card_last4       varchar(4)     null,
    approval_code    varchar(32)    not null,
    approved_at      timestamp      not null,
    status           varchar(20)    not null,
    created_at       timestamp      not null,
    updated_at       timestamp      not null
);


