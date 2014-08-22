drop table if exists pnrs;
drop table if exists pnrcounts;

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
    pnrs_waitcount int not null default 0,
    pnrs_version int not null default 0,
    constraint primary key (pnrs_id),
    constraint nk_pnrs unique (pnrs_pcc,pnrs_recloc,pnrs_created),
    index idx_pnrs_pcc (pnrs_pcc),
    index idx_pnrs_recloc (pnrs_recloc),
    index idx_pnrs_created (pnrs_created),
    index idx_pnrs_departed (pnrs_departed)
);

create table pnrcounts (
    pnrcounts_id int not null auto_increment,
    pnrcounts_countdate timestamp not null,
    pnrcounts_yearmonth char(6) not null,
    pnrcounts_recloc char(10) not null,
    pnrcounts_pcc char(10) not null,
    pnrcounts_signon char(10) not null,
    pnrcounts_count int not null default 0,
    pnrcounts_waitcount int not null default 0,
    pnrcounts_created date not null,
    pnrcounts_departed date not null,
    pnrcounts_yearmonthday char(8) not null,
    pnrcounts_version int not null default 0,
    constraint primary key (pnrcounts_id),
    constraint nk_pnrcounts unique (pnrcounts_countdate,pnrcounts_recloc,pnrcounts_pcc,pnrcounts_signon),
    index idx_pnrcounts_yearmonthday (pnrcounts_yearmonthday),
    index idx_pnrcounts_pcc (pnrcounts_pcc),
    index idx_pnrcounts_recloc (pnrcounts_recloc),
    index idx_pnrcounts_created (pnrcounts_created),
    index idx_pnrcounts_departed (pnrcounts_departed),
    index idx_pnrcounts_yearmonth (pnrcounts_yearmonth)
);
