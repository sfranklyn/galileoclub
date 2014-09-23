drop table if exists urls_roles;
drop table if exists users_roles;
drop table if exists news;
drop table if exists users_pccs;
drop table if exists users;
drop table if exists roles;
drop table if exists configs;
drop table if exists pccs;
drop table if exists pnrs;
drop table if exists pnrcounts;
drop table if exists points;
drop table if exists claims;

create table users (
    user_id int not null auto_increment,
    user_name varchar(50) not null,
    user_code char(12),
    full_name varchar(100) not null,
    user_password varchar(128) not null,
    user_email varchar(200),
    user_birthday date,
    user_pcc varchar(10),
    user_son varchar(10),
    user_agentname varchar(50),
    user_office_address1 varchar(50),
    user_office_address2 varchar(50),
    user_office_address3 varchar(50),
    user_office_city varchar(30),
    user_office_zip varchar(10),
    user_office_phone varchar(30),
    user_office_ext varchar(15),
    user_office_fax varchar(30),
    user_mobile_phone varchar(15),
    user_status char(1),
    user_created datetime default null,
    user_created_by varchar(50),
    user_activated datetime default null,
    user_activated_by varchar(50),
    user_updated datetime default null,
    user_updated_by varchar(50),
    user_version int not null default 0,
    user_point_value int default 0,
    user_notes mediumtext,
    constraint primary key (user_id),
    constraint nk_users unique (user_name),
    index idx_users_user_code (user_code),
    index idx_users_user_status (user_status),
    index idx_users_user_pcc (user_pcc),
    index idx_users_user_son (user_son)
);

create table roles (
    role_id int not null auto_increment,
    role_name varchar(50) not null,
    role_desc varchar(100) not null,
    role_menu varchar(50) not null,
    role_version int not null default 0,
    constraint primary key (role_id),
    constraint nk_roles unique (role_name)
);

create table users_roles (
    user_id int not null,
    role_id int not null,
    users_roles_desc varchar(100),
    users_roles_version int not null default 0,
    primary key (user_id,role_id),
    constraint fk_users_roles1 foreign key (user_id) references users (user_id),
    constraint fk_users_roles2 foreign key (role_id) references roles (role_id),
    index idx_users_roles1 (user_id),
    index idx_users_roles2 (role_id)
);

create table urls_roles (
    url_role varchar(250) not null,
    role_id int not null,
    url_role_version int not null default 0,
    primary key (url_role,role_id),
    constraint fk_urls_roles1 foreign key (role_id) references roles (role_id),
    index idx_urls_roles1 (url_role),
    index idx_urls_roles2 (role_id)
);

create table configs (
    config_id int not null auto_increment,
    config_key char(30) not null,
    config_desc varchar(100) not null,
    config_type varchar(100) not null,
    config_value varchar(100) not null,
    config_version int not null default 0,
    constraint primary key (config_id), 
    constraint nk_configs unique (config_key)
);

create table pccs (
    pccs_id int not null auto_increment,
    pccs_pcc char(10) not null,
    pccs_desc varchar(200) not null,
    pccs_note mediumtext,
    pccs_version int not null default 0,
    constraint primary key (pccs_id),
    constraint nk_pccs unique (pccs_pcc)
);

create table users_pccs (
    user_id int not null,
    pccs_id int not null,
    user_pcc_son varchar(10) not null,
    user_pcc_version int not null default 0,
    primary key (user_id,pccs_id),
    constraint fk_users_pccs1 foreign key (user_id) references users (user_id),
    constraint fk_users_pccs2 foreign key (pccs_id) references pccs (pccs_id),
    index idx_users_pccs1 (user_id),
    index idx_users_pccs2 (pccs_id)
);

create table pnrs (
    pnrs_id int not null auto_increment,
    pnrs_pcc char(10) not null,
    pnrs_signon char(10) not null,
    pnrs_recloc char(10) not null,
    pnrs_created date not null,
    pnrs_departed date not null,
    pnrs_active bit not null,
    pnrs_new bit not null,
    pnrs_count int not null default 0,
    pnrs_namecount int not null default 0,
    pnrs_waitcount int not null default 0,
    pnrs_version int not null default 0,
    constraint primary key (pnrs_id),
    constraint nk_pnrs unique (pnrs_pcc,pnrs_recloc,pnrs_created),
    index idx_pnrs_pcc (pnrs_pcc),
    index idx_pnrs_signon (pnrs_signon),
    index idx_pnrs_recloc (pnrs_recloc),
    index idx_pnrs_created (pnrs_created),
    index idx_pnrs_departed (pnrs_departed)
);

create table pnrcounts (
    pnrcounts_id int not null auto_increment,
    pnrcounts_countdate datetime not null,
    pnrcounts_yearmonth char(6) not null,
    pnrcounts_recloc char(10) not null,
    pnrcounts_pcc char(10) not null,
    pnrcounts_signon char(10) not null,
    pnrcounts_count int not null default 0,
    pnrcounts_namecount int not null default 0,
    pnrcounts_waitcount int not null default 0,
    pnrcounts_created date not null,
    pnrcounts_departed date not null,
    pnrcounts_yearmonthday char(8) not null,
    pnrcounts_version int not null default 0,
    constraint primary key (pnrcounts_id),
    constraint nk_pnrcounts unique (pnrcounts_countdate,pnrcounts_recloc,pnrcounts_pcc,pnrcounts_signon),
    index idx_pnrcounts_countdate (pnrcounts_countdate),
    index idx_pnrcounts_yearmonth (pnrcounts_yearmonth),
    index idx_pnrcounts_recloc (pnrcounts_recloc),
    index idx_pnrcounts_pcc (pnrcounts_pcc),
    index idx_pnrcounts_signon (pnrcounts_signon),
    index idx_pnrcounts_count (pnrcounts_count),
    index idx_pnrcounts_waitcount (pnrcounts_waitcount),
    index idx_pnrcounts_created (pnrcounts_created),
    index idx_pnrcounts_departed (pnrcounts_departed),
    index idx_pnrcounts_yearmonthday (pnrcounts_yearmonthday)
);

create table claims (
    claim_id int not null auto_increment,
    claim_user_code char(12) not null,
    claim_date datetime not null,
    claim_desc varchar(767) not null,
    claim_response varchar(255),
    claim_count int not null,
    claim_status char(1) not null,
    claim_ref_doc varchar(50),
    constraint primary key (claim_id),
    index idx_claims_claim_user_code (claim_user_code),
    index idx_claims_claim_date (claim_date),
    index idx_claims_claim_status (claim_status)
);

create table points (
    point_id int not null auto_increment,
    point_user_code char(12) not null,
    point_pcc char(10) not null,
    point_signon char(10) not null,
    point_year int not null,
    point_month int not null,
    point_day int not null,
    point_count int not null,
    claim_id int,
    point_value int default 0,
    constraint fk_points1 foreign key (claim_id) references claims (claim_id),
    constraint primary key (point_id),
    index idx_points_point_user_code (point_user_code),
    index idx_points_point_pcc (point_pcc),
    index idx_points_point_signon (point_signon),
    index idx_points_point_year (point_year),
    index idx_points_point_month (point_month),
    index idx_points_point_day (point_day)
);

create table news (
    news_id int not null auto_increment,
    news_date datetime not null,
    news_text text not null,
    user_id int not null,
    news_status char(1),
    constraint primary key (news_id),
    constraint nk_news unique (news_date),
    constraint fk_news1 foreign key (user_id) references users (user_id),
    index idx_news_date (news_date),
    index idx_news_status (news_status)
);