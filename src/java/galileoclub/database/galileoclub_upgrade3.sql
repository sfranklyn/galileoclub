drop table if exists users_pccs;
create table users_pccs (
    user_id int not null,
    pccs_id int not null,
    user_pcc_son varchar(10) not null,
    user_pcc_version int not null default 0,
    primary key (user_id,pccs_id,user_pcc_son),
    constraint fk_users_pccs1 foreign key (user_id) references users (user_id),
    constraint fk_users_pccs2 foreign key (pccs_id) references pccs (pccs_id),
    index idx_users_pccs1 (user_id),
    index idx_users_pccs2 (pccs_id)
);