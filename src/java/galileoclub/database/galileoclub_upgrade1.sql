alter table pnrs
add column pnrs_namecount int not null default 0 after pnrs_count;

alter table pnrcounts
add column pnrcounts_namecount int not null default 0 after pnrcounts_count;