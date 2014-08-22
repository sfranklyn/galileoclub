DROP VIEW IF EXISTS pnrcounts_double;
CREATE VIEW pnrcounts_double AS 
select
count(*) as count1,
pnrcounts_created,
pnrcounts_recloc,
pnrcounts_count,
pnrcounts_waitcount,
pnrcounts_pcc,
pnrcounts_signon,
pnrcounts_namecount
from pnrcounts
where
pnrcounts_yearmonth='201012'
group by
pnrcounts_created,
pnrcounts_recloc,
pnrcounts_count,
pnrcounts_waitcount,
pnrcounts_pcc,
pnrcounts_signon,
pnrcounts_namecount
having count(*)>1
order by
pnrcounts_pcc,
pnrcounts_signon,
pnrcounts_created,
pnrcounts_recloc,
pnrcounts_countdate